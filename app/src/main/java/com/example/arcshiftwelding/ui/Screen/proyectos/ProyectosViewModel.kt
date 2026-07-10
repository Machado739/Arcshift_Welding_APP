package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.EmpleadoDao
import com.example.arcshiftwelding.data.local.dao.GastoDao
import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoCostoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoEmpleadoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoMaterialDao
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoCostoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoMaterialEntity
import com.example.arcshiftwelding.data.repository.ProyectoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


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
    val folio: String = "",
    val texto: String,
    val clienteId: Int,
    val total: Double,
    val proyecto: String = "",
    val descripcionTrabajo: String = "",
    val vigencia: String = ""
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
    private val cotizacionDao: CotizacionDao,
    private val empleadoDao: EmpleadoDao,
    private val productoDao: ProductoDao,
    private val gastoDao: GastoDao,
    private val proyectoEmpleadoDao: ProyectoEmpleadoDao,
    private val proyectoMaterialDao: ProyectoMaterialDao,
    private val proyectoCostoDao: ProyectoCostoDao,
    private val proyectoRepository: ProyectoRepository
) : ViewModel() {


    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()


    val productosInventario: StateFlow<List<ProductoEntity>> =
        productoDao.obtenerProductosParaProyecto()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val empleadosDisponibles: StateFlow<List<EmpleadoEntity>> =
        empleadoDao.obtenerEmpleadosParaProyecto()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun obtenerGastosProyecto(proyectoId: Int): Flow<List<GastoEntity>> {
        return gastoDao.obtenerGastosPorProyecto(proyectoId)
    }

    fun obtenerEmpleadosProyecto(proyectoId: Int): Flow<List<ProyectoEmpleadoEntity>> {
        return proyectoEmpleadoDao.obtenerPorProyecto(proyectoId)
    }

    fun obtenerMaterialesProyecto(proyectoId: Int): Flow<List<ProyectoMaterialEntity>> {
        return proyectoMaterialDao.obtenerPorProyecto(proyectoId)
    }

    fun obtenerCostosProyecto(proyectoId: Int): Flow<List<ProyectoCostoEntity>> {
        return proyectoCostoDao.obtenerPorProyecto(proyectoId)
    }

    fun obtenerResumenCostosProyecto(
        proyectoId: Int,
        presupuestoEstimado: Double
    ): Flow<ResumenCostosProyecto> {
        return combine(
            proyectoMaterialDao.totalMaterialesPorProyecto(proyectoId),
            proyectoEmpleadoDao.totalManoObraPorProyecto(proyectoId),
            gastoDao.totalGastosPorProyecto(proyectoId)
        ) { materiales, manoObra, gastos ->
            ResumenCostosProyecto(
                precioCotizado = presupuestoEstimado,
                costoMateriales = materiales,
                costoManoObra = manoObra,
                costosAdicionales = gastos
            )
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }


    fun registrarMaterialUsado(
        proyectoId: Int,
        productoId: Int,
        cantidadUsada: Int,
        fechaUso: String,
        observaciones: String
    ) {
        viewModelScope.launch {
            val resultado = proyectoRepository.registrarMaterialUsado(
                proyectoId = proyectoId,
                productoId = productoId,
                cantidadUsada = cantidadUsada,
                fechaUso = fechaUso,
                observaciones = observaciones
            )

            _mensaje.value = if (resultado.isSuccess) {
                "Material registrado correctamente"
            } else {
                resultado.exceptionOrNull()?.message ?: "Error al registrar material"
            }
        }
    }

    fun eliminarMaterialUsado(materialId: Int) {
        viewModelScope.launch {
            val resultado = proyectoRepository.eliminarMaterialUsado(materialId)

            _mensaje.value = if (resultado.isSuccess) {
                "Material eliminado y stock restaurado"
            } else {
                resultado.exceptionOrNull()?.message ?: "Error al eliminar material"
            }
        }
    }

    fun agregarCostoProyecto(
        proyectoId: Int,
        tipo: String,
        descripcion: String,
        monto: Double,
        fecha: String,
        comprobanteUri: String?,
        observaciones: String
    ) {
        viewModelScope.launch {
            val costo = ProyectoCostoEntity(
                proyectoId = proyectoId,
                tipo = tipo,
                descripcion = descripcion,
                monto = monto,
                fecha = fecha,
                comprobanteUri = comprobanteUri,
                observaciones = observaciones
            )

            proyectoRepository.agregarCosto(costo)
            _mensaje.value = "Costo agregado al proyecto"
        }
    }

    private fun calcularCostoEmpleado(
        tipoPago: String,
        pagoAcordado: Double,
        diasTrabajados: Double,
        horasTrabajadas: Double,
        porcentaje: Double,
        precioCotizado: Double
    ): Double {
        return when (tipoPago) {
            "Día" -> diasTrabajados * pagoAcordado
            "Hora" -> horasTrabajadas * pagoAcordado
            "Semana" -> pagoAcordado
            "Trabajo" -> pagoAcordado
            "Porcentaje" -> precioCotizado * (porcentaje / 100)
            else -> 0.0
        }
    }

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
                        folio = cotizacion.folio,
                        texto = "${cotizacion.folio} - ${cliente?.nombre ?: "Cliente #${cotizacion.clienteId}"}",
                        clienteId = cotizacion.clienteId,
                        total = cotizacion.total,
                        proyecto = cotizacion.proyecto,
                        descripcionTrabajo = cotizacion.descripcionTrabajo,
                        vigencia = cotizacion.vigencia
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
            cotizacionDao.obtenerCotizaciones(),
            proyectoEmpleadoDao.obtenerTodosEmpleadosProyecto(),
            proyectoMaterialDao.obtenerTodosMaterialesProyecto()
        ) { proyectos, clientes, cotizaciones, empleadosProyecto, materialesProyecto ->

            proyectos.map { proyecto ->
                val cliente = clientes.find { it.id == proyecto.clienteId }
                val cotizacion = cotizaciones.find { it.id == proyecto.cotizacionId }

                val empleadosDelProyecto = empleadosProyecto.filter {
                    it.proyectoId == proyecto.id
                }

                val materialesDelProyecto = materialesProyecto.filter {
                    it.proyectoId == proyecto.id
                }

                val costoMaterialCalculado = materialesDelProyecto.sumOf {
                    it.subtotal
                }

                val costoManoObraCalculado = empleadosDelProyecto.sumOf {
                    it.costoCalculado
                }

                val costoTotalCalculado = costoMaterialCalculado + costoManoObraCalculado

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

                    costoMaterial = costoMaterialCalculado,
                    costoManoObra = costoManoObraCalculado,
                    costoTotal = costoTotalCalculado,

                    observaciones = proyecto.observaciones,
                    empleadosAsignados = empleadosDelProyecto.size,
                    materialesUsados = materialesDelProyecto.size
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


    fun asignarEmpleadoAProyecto(
        proyectoId: Int,
        empleado: EmpleadoEntity,
        presupuestoEstimado: Double,
        diasTrabajados: Double,
        horasTrabajadas: Double,
        fechaAsignacion: String,
        observaciones: String
    ) {
        viewModelScope.launch {
            val yaExiste = proyectoEmpleadoDao.existeEmpleadoEnProyecto(
                proyectoId = proyectoId,
                empleadoId = empleado.id
            ) > 0

            if (yaExiste) {
                _mensaje.value = "Este empleado ya está asignado al proyecto"
                return@launch
            }

            val tipoPago = obtenerTipoPagoDesdeContratoEmpleado(empleado.porcentajeContrato)
            val valorContrato = obtenerValorDesdeContratoEmpleado(empleado.porcentajeContrato)

            val pagoAcordado = when (tipoPago) {
                "Día", "Semana", "Hora" -> valorContrato
                else -> 0.0
            }

            val porcentaje = when (tipoPago) {
                "Porcentaje" -> valorContrato
                else -> 0.0
            }

            val costoCalculado = calcularCostoEmpleadoProyecto(
                tipoPago = tipoPago,
                pagoAcordado = pagoAcordado,
                diasTrabajados = diasTrabajados,
                horasTrabajadas = horasTrabajadas,
                porcentaje = porcentaje,
                presupuestoEstimado = presupuestoEstimado
            )

            val proyectoEmpleado = ProyectoEmpleadoEntity(
                proyectoId = proyectoId,
                empleadoId = empleado.id,
                nombreEmpleado = empleado.nombre,
                puesto = empleado.puesto,
                tipoPago = tipoPago,
                pagoAcordado = pagoAcordado,
                diasTrabajados = diasTrabajados,
                horasTrabajadas = horasTrabajadas,
                porcentaje = porcentaje,
                costoCalculado = costoCalculado,
                fechaAsignacion = fechaAsignacion,
                observaciones = observaciones
            )

            proyectoRepository.asignarEmpleado(proyectoEmpleado)
            _mensaje.value = "Empleado asignado al proyecto"
        }
    }

    private fun calcularCostoEmpleadoProyecto(
        tipoPago: String,
        pagoAcordado: Double,
        diasTrabajados: Double,
        horasTrabajadas: Double,
        porcentaje: Double,
        presupuestoEstimado: Double
    ): Double {
        return when (tipoPago) {
            "Día" -> diasTrabajados * pagoAcordado
            "Semana" -> diasTrabajados * pagoAcordado
            "Hora" -> horasTrabajadas * pagoAcordado
            "Porcentaje" -> presupuestoEstimado * (porcentaje / 100)
            else -> 0.0
        }
    }
    private fun obtenerTipoPagoDesdeContratoEmpleado(contrato: String): String {
        val contratoLimpio = contrato.trim()

        return when {
            contratoLimpio.contains("% por trabajo", ignoreCase = true) -> "Porcentaje"
            contratoLimpio.contains("por trabajo", ignoreCase = true) -> "Porcentaje"
            contratoLimpio.contains("%") -> "Porcentaje"

            contratoLimpio.contains("pago por día", ignoreCase = true) -> "Día"
            contratoLimpio.contains("pago por dia", ignoreCase = true) -> "Día"
            contratoLimpio.contains("día", ignoreCase = true) -> "Día"
            contratoLimpio.contains("dia", ignoreCase = true) -> "Día"

            contratoLimpio.contains("semana", ignoreCase = true) -> "Semana"
            contratoLimpio.contains("hora", ignoreCase = true) -> "Hora"

            else -> "Sin definir"
        }
    }

    private fun obtenerValorDesdeContratoEmpleado(contrato: String): Double {
        val contratoLimpio = contrato.trim()

        if (contratoLimpio.isBlank()) {
            return 0.0
        }

        val textoValor = if (contratoLimpio.contains(":")) {
            contratoLimpio.substringAfter(":")
        } else {
            contratoLimpio
        }

        return textoValor
            .replace("$", "")
            .replace("%", "")
            .replace(",", "")
            .trim()
            .toDoubleOrNull() ?: 0.0
    }


    fun eliminarGastoProyecto(gasto: GastoEntity) {
        viewModelScope.launch {
            gastoDao.eliminarGasto(gasto)
            _mensaje.value = "Gasto eliminado"
        }
    }

    fun obtenerEmpleadoProyectoPorId(
        empleadoProyectoId: Int
    ): Flow<ProyectoEmpleadoEntity?> {
        return proyectoEmpleadoDao.observarEmpleadoProyectoPorId(empleadoProyectoId)
    }

    fun eliminarEmpleadoAsignadoProyecto(
        empleadoProyectoId: Int
    ) {
        viewModelScope.launch {
            proyectoEmpleadoDao.eliminarEmpleadoProyectoPorId(empleadoProyectoId)
            _mensaje.value = "Empleado eliminado del proyecto"
        }
    }

    fun actualizarEmpleadoAsignadoProyecto(
        empleadoProyecto: ProyectoEmpleadoEntity,
        presupuestoEstimado: Double,
        diasTrabajados: Double,
        horasTrabajadas: Double,
        observaciones: String
    ) {
        viewModelScope.launch {
            val costoCalculado = calcularCostoEmpleadoProyecto(
                tipoPago = empleadoProyecto.tipoPago,
                pagoAcordado = empleadoProyecto.pagoAcordado,
                diasTrabajados = diasTrabajados,
                horasTrabajadas = horasTrabajadas,
                porcentaje = empleadoProyecto.porcentaje,
                presupuestoEstimado = presupuestoEstimado
            )

            proyectoEmpleadoDao.actualizarEmpleadoProyecto(
                empleadoProyectoId = empleadoProyecto.id,
                diasTrabajados = diasTrabajados,
                horasTrabajadas = horasTrabajadas,
                porcentaje = empleadoProyecto.porcentaje,
                costoCalculado = costoCalculado,
                observaciones = observaciones
            )

            _mensaje.value = "Empleado actualizado"
        }
    }

    fun eliminarGastoProyecto(
        gastoId: Int
    ) {
        viewModelScope.launch {
            gastoDao.eliminarGastoPorId(gastoId)
            _mensaje.value = "Gasto eliminado"
        }
    }

    fun actualizarAvanceProyecto(
        proyectoId: Int,
        avance: Int
    ) {
        viewModelScope.launch {
            val estado = when {
                avance >= 100 -> "Terminado"
                avance <= 0 -> "Pendiente"
                else -> "En trabajo"
            }

            proyectoDao.actualizarAvanceProyecto(
                proyectoId = proyectoId,
                avance = avance.coerceIn(0, 100),
                estado = estado
            )

            _mensaje.value = "Avance actualizado"
        }
    }
}

data class ResumenCostosProyecto(
    val precioCotizado: Double = 0.0,
    val costoMateriales: Double = 0.0,
    val costoManoObra: Double = 0.0,
    val costosAdicionales: Double = 0.0
) {
    val costoTotal: Double
        get() = costoMateriales + costoManoObra + costosAdicionales

    val utilidad: Double
        get() = precioCotizado - costoTotal
}