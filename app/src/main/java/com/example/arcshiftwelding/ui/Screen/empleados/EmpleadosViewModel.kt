package com.example.arcshiftwelding.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.EmpleadoDao
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class EmpleadosViewModel(
    private val empleadoDao: EmpleadoDao
) : ViewModel() {

    val empleados: StateFlow<List<com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoUI>> =
        empleadoDao.obtenerEmpleados()
            .map { lista ->
                lista.map { empleado ->
                    empleado.toUi()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insertarEmpleado(empleado: EmpleadoEntity) {
        viewModelScope.launch {
            empleadoDao.insertarEmpleado(empleado)
        }
    }

    fun actualizarEmpleado(empleado: EmpleadoEntity) {
        viewModelScope.launch {
            empleadoDao.actualizarEmpleado(empleado)
        }
    }

    fun eliminarEmpleado(empleado: EmpleadoEntity) {
        viewModelScope.launch {
            empleadoDao.eliminarEmpleado(empleado)
        }
    }

    fun observarEmpleado(id: Int): Flow<EmpleadoEntity?> {
        return empleadoDao.observarEmpleadoPorId(id)
    }
}


class EmpleadosViewModelFactory(
    private val empleadoDao: EmpleadoDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(_root_ide_package_.com.example.arcshiftwelding.ui.Screen.empleados.EmpleadosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return _root_ide_package_.com.example.arcshiftwelding.ui.Screen.empleados.EmpleadosViewModel(
                empleadoDao
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}

fun EmpleadoEntity.toUi(): com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoUI {
    val porcentaje = porcentajeContrato.formatoPorcentajeContrato()

    return _root_ide_package_.com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        trabajo = trabajoActual.ifBlank { "Sin trabajo asignado" },
        contrato = if (porcentaje == "No registrado") "Contrato no registrado" else "Contrato $porcentaje",
        pagoTotal = salario.formatoMoneda(),
        periodoPago = "Pago total",
        estado = if (activo) "Activo" else "Inactivo",
        color = if (activo) Color(0xFF2563EB) else Color(0xFF64748B),
        salario = salario,
        fotoUri = fotoUri
    )
}

fun EmpleadoEntity.toDetalleUi(): com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoDetalleUI {
    return _root_ide_package_.com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoDetalleUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        telefono = telefono.ifBlank { "Sin teléfono registrado" },
        fechaIngreso = fechaIngreso.ifBlank { "Sin fecha registrada" },
        correo = correo.ifBlank { "Sin correo registrado" },
        direccion = direccion.ifBlank { "Sin dirección registrada" },
        porcentajeContrato = porcentajeContrato.formatoPorcentajeContrato(),
        trabajoActual = trabajoActual.ifBlank { "Sin trabajo asignado" },
        pagoTotalSemana = salario.formatoMoneda(),
        estado = if (activo) "Activo" else "Inactivo",
        notas = notas.ifBlank { "Sin notas registradas" },
        fotoUri = fotoUri
    )
}

fun EmpleadoEntity.toEliminarUi(): com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoEliminarUI {
    return _root_ide_package_.com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoEliminarUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        estado = if (activo) "Activo" else "Inactivo",
        trabajoActual = trabajoActual.ifBlank { "Sin trabajo asignado" },
        contrato = porcentajeContrato.formatoPorcentajeContrato(),
        pagoTotal = salario.formatoMoneda()
    )
}

fun String.formatoPorcentajeContrato(): String {
    val valor = trim()

    if (valor.isBlank()) return "No registrado"

    return if (valor.contains("%")) {
        valor
    } else {
        "$valor%"
    }
}

fun Double.formatoMoneda(): String {
    return "$" + String.format(Locale("es", "MX"), "%,.2f", this)
}

fun String.aDoubleMoneda(): Double {
    return this
        .replace("$", "")
        .replace(",", "")
        .trim()
        .toDoubleOrNull() ?: 0.0
}