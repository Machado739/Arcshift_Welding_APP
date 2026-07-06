package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.ProyectoDao
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import kotlinx.coroutines.flow.combine
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import kotlinx.coroutines.flow.Flow
data class ProyectoUI(
    val id: Int,
    val nombre: String,
    val cliente: String,
    val clienteId: Int,
    val cotizacionId: Int?,
    val cotizacion: String,
    val descripcion: String,
    val fechaInicio: String,
    val fechaEstimadaFin: String,
    val fechaFinReal: String,
    val estado: String,
    val avance: Int,
    val presupuestoEstimado: Double,
    val costoMaterial: Double,
    val costoManoObra: Double,
    val costoTotal: Double,
    val observaciones: String,

    // Estos son temporales hasta conectar las tablas intermedias
    val empleadosAsignados: Int = 0,
    val materialesUsados: Int = 0
)

data class CotizacionProyectoUI(
    val id: Int,
    val texto: String,
    val clienteId: Int,
    val total: Double
)

data class ResumenProyectosUI(
    val total: Int = 0,
    val pendientes: Int = 0,
    val enTrabajo: Int = 0,
    val terminados: Int = 0,
    val cancelados: Int = 0
)

class ProyectosViewModel(
    private val proyectoDao: ProyectoDao,
    private val clienteDao: ClienteDao,
    private val cotizacionDao: CotizacionDao
) : ViewModel() {

    val clientes: StateFlow<List<ClienteEntity>> =
        clienteDao.obtenerClientesActivos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val cotizaciones: StateFlow<List<CotizacionProyectoUI>> =
        cotizacionDao.obtenerCotizaciones()
            .combine(clientes) { cotizaciones, clientes ->
                cotizaciones.map { cotizacion ->
                    val cliente = clientes.find { it.id == cotizacion.clienteId }

                    CotizacionProyectoUI(
                        id = cotizacion.id,
                        texto = "${cotizacion.folio} - ${cliente?.nombre ?: "Cliente #${cotizacion.clienteId}"}",
                        clienteId = cotizacion.clienteId,
                        total = cotizacion.total
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val proyectos: StateFlow<List<ProyectoUI>> =
        combine(
            proyectoDao.obtenerProyectos(),
            clienteDao.obtenerClientesActivos(),
            cotizacionDao.obtenerCotizaciones()
        ) { proyectos, clientes, cotizaciones ->

            proyectos.map { proyecto ->
                val cliente = clientes.find { it.id == proyecto.clienteId }
                val cotizacion = cotizaciones.find { it.id == proyecto.cotizacionId }

                ProyectoUI(
                    id = proyecto.id,
                    nombre = proyecto.nombre,
                    cliente = cliente?.nombre ?: "Cliente #${proyecto.clienteId}",
                    clienteId = proyecto.clienteId,
                    cotizacionId = proyecto.cotizacionId,
                    cotizacion = cotizacion?.folio ?: "Sin cotización",
                    descripcion = proyecto.descripcion,
                    fechaInicio = proyecto.fechaInicio,
                    fechaEstimadaFin = proyecto.fechaEstimadaFin,
                    fechaFinReal = proyecto.fechaFinReal,
                    estado = proyecto.estado,
                    avance = proyecto.avance,
                    presupuestoEstimado = proyecto.presupuestoEstimado,
                    costoMaterial = proyecto.costoMaterial,
                    costoManoObra = proyecto.costoManoObra,
                    costoTotal = proyecto.costoTotal,
                    observaciones = proyecto.observaciones,
                    empleadosAsignados = 0,
                    materialesUsados = 0
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val resumen: StateFlow<ResumenProyectosUI> =
        proyectos
            .combine(clientes) { listaProyectos, _ ->
                ResumenProyectosUI(
                    total = listaProyectos.size,
                    pendientes = listaProyectos.count { it.estado == "Pendiente" },
                    enTrabajo = listaProyectos.count { it.estado == "En trabajo" },
                    terminados = listaProyectos.count { it.estado == "Terminado" },
                    cancelados = listaProyectos.count { it.estado == "Cancelado" }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ResumenProyectosUI()
            )

    fun registrarProyecto(
        nombre: String,
        clienteId: Int,
        cotizacionId: Int?,
        descripcion: String,
        fechaInicio: String,
        fechaEstimadaFin: String,
        estado: String,
        avance: Int,
        presupuestoEstimado: Double,
        observaciones: String
    ) {
        viewModelScope.launch {
            val proyecto = ProyectoEntity(
                nombre = nombre.trim(),
                clienteId = clienteId,
                cotizacionId = cotizacionId,
                descripcion = descripcion.trim(),
                fechaInicio = fechaInicio.trim(),
                fechaEstimadaFin = fechaEstimadaFin.trim(),
                fechaFinReal = "",
                estado = estado,
                avance = avance,
                presupuestoEstimado = presupuestoEstimado,
                costoMaterial = 0.0,
                costoManoObra = 0.0,
                costoTotal = 0.0,
                observaciones = observaciones.trim()
            )

            proyectoDao.insertarProyecto(proyecto)
        }
    }

    fun avanceProyectoPorEstado(estado: String): Int {
        return when (estado) {
            "Pendiente" -> 10
            "En trabajo" -> 55
            "Terminado" -> 100
            "Cancelado" -> 0
            else -> 0
        }
    }

    fun actualizarProyecto(
        id: Int,
        nombre: String,
        clienteId: Int,
        cotizacionId: Int?,
        descripcion: String,
        fechaInicio: String,
        fechaEstimadaFin: String,
        fechaFinReal: String,
        estado: String,
        avance: Int,
        presupuestoEstimado: Double,
        costoMaterial: Double,
        costoManoObra: Double,
        costoTotal: Double,
        observaciones: String
    ) {
        viewModelScope.launch {
            val proyecto = ProyectoEntity(
                id = id,
                nombre = nombre.trim(),
                clienteId = clienteId,
                cotizacionId = cotizacionId,
                descripcion = descripcion.trim(),
                fechaInicio = fechaInicio.trim(),
                fechaEstimadaFin = fechaEstimadaFin.trim(),
                fechaFinReal = fechaFinReal.trim(),
                estado = estado,
                avance = avance,
                presupuestoEstimado = presupuestoEstimado,
                costoMaterial = costoMaterial,
                costoManoObra = costoManoObra,
                costoTotal = costoTotal,
                observaciones = observaciones.trim()
            )

            proyectoDao.actualizarProyecto(proyecto)
        }
    }

    fun terminarProyecto(proyecto: ProyectoUI) {
        viewModelScope.launch {
            val proyectoTerminado = ProyectoEntity(
                id = proyecto.id,
                nombre = proyecto.nombre,
                clienteId = proyecto.clienteId,
                cotizacionId = proyecto.cotizacionId,
                descripcion = proyecto.descripcion,
                fechaInicio = proyecto.fechaInicio,
                fechaEstimadaFin = proyecto.fechaEstimadaFin,
                fechaFinReal = obtenerFechaActualProyectoSistema(),
                estado = "Terminado",
                avance = 100,
                presupuestoEstimado = proyecto.presupuestoEstimado,
                costoMaterial = proyecto.costoMaterial,
                costoManoObra = proyecto.costoManoObra,
                costoTotal = proyecto.costoTotal,
                observaciones = proyecto.observaciones
            )

            proyectoDao.actualizarProyecto(proyectoTerminado)
        }
    }

    fun eliminarProyecto(proyecto: ProyectoUI) {
        viewModelScope.launch {
            val proyectoEliminar = ProyectoEntity(
                id = proyecto.id,
                nombre = proyecto.nombre,
                clienteId = proyecto.clienteId,
                cotizacionId = proyecto.cotizacionId,
                descripcion = proyecto.descripcion,
                fechaInicio = proyecto.fechaInicio,
                fechaEstimadaFin = proyecto.fechaEstimadaFin,
                fechaFinReal = proyecto.fechaFinReal,
                estado = proyecto.estado,
                avance = proyecto.avance,
                presupuestoEstimado = proyecto.presupuestoEstimado,
                costoMaterial = proyecto.costoMaterial,
                costoManoObra = proyecto.costoManoObra,
                costoTotal = proyecto.costoTotal,
                observaciones = proyecto.observaciones
            )

            proyectoDao.eliminarProyecto(proyectoEliminar)
        }
    }

    fun obtenerFechaActualProyectoSistema(): String {
        return java.text.SimpleDateFormat(
            "dd/MM/yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
    }

    fun obtenerCotizacionPorId(cotizacionId: Int): Flow<CotizacionEntity?> {
        return cotizacionDao.observarCotizacionPorId(cotizacionId)
    }
}