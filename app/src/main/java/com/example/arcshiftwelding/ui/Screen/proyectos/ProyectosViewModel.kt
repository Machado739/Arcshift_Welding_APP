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

data class ProyectoUI(
    val id: Int,
    val nombre: String,
    val cliente: String,
    val clienteId: Int,
    val fechaInicio: String,
    val fechaEstimadaFin: String,
    val fechaFinReal: String,
    val estado: String
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
    private val clienteDao: ClienteDao
) : ViewModel() {

    val clientes: StateFlow<List<ClienteEntity>> =
        clienteDao.obtenerClientesActivos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val proyectos: StateFlow<List<ProyectoUI>> =
        combine(
            proyectoDao.obtenerProyectos(),
            clienteDao.obtenerClientesActivos()
        ) { proyectos, clientes ->

            proyectos.map { proyecto ->
                val cliente = clientes.find { it.id == proyecto.clienteId }

                ProyectoUI(
                    id = proyecto.id,
                    nombre = proyecto.nombre,
                    cliente = cliente?.nombre ?: "Cliente #${proyecto.clienteId}",
                    clienteId = proyecto.clienteId,
                    fechaInicio = proyecto.fechaInicio,
                    fechaEstimadaFin = proyecto.fechaEstimadaFin,
                    fechaFinReal = proyecto.fechaFinReal,
                    estado = proyecto.estado
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
        fechaInicio: String,
        fechaFin: String,
        estado: String
    ) {
        viewModelScope.launch {
            val proyecto = ProyectoEntity(
                nombre = nombre.trim(),
                clienteId = clienteId,
                fechaInicio = fechaInicio.trim(),
                fechaEstimadaFin = fechaFin.trim(),
                fechaFinReal = fechaFin.trim(),
                estado = estado
            )

            proyectoDao.insertarProyecto(proyecto)
        }
    }

    fun actualizarEstadoProyecto(
        proyecto: ProyectoUI,
        nuevoEstado: String
    ) {
        viewModelScope.launch {
            val proyectoActualizado = ProyectoEntity(
                id = proyecto.id,
                nombre = proyecto.nombre,
                clienteId = proyecto.clienteId,
                fechaInicio = proyecto.fechaInicio,
                fechaEstimadaFin = proyecto.fechaEstimadaFin,
                fechaFinReal = proyecto.fechaFinReal,
                estado = nuevoEstado
            )

            proyectoDao.actualizarProyecto(proyectoActualizado)
        }
    }
}