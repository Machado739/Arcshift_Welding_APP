package com.example.arcshiftwelding.ui.Screen.empleados

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

    val empleados: StateFlow<List<EmpleadoUI>> =
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
        if (modelClass.isAssignableFrom(EmpleadosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmpleadosViewModel(empleadoDao) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}

fun EmpleadoEntity.toUi(): EmpleadoUI {
    return EmpleadoUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        trabajo = "Sin trabajo asignado",
        contrato = "Salario registrado",
        pagoTotal = salario.formatoMoneda(),
        periodoPago = "Pago total",
        estado = if (activo) "Activo" else "Inactivo",
        color = if (activo) Color(0xFF2563EB) else Color(0xFF64748B),
        salario = salario
    )
}

fun EmpleadoEntity.toDetalleUi(): EmpleadoDetalleUI {
    return EmpleadoDetalleUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        telefono = telefono,
        fechaIngreso = fechaIngreso,
        correo = correo,
        direccion = "Sin dirección registrada",
        porcentajeContrato = "No registrado",
        trabajoActual = "Sin trabajo asignado",
        pagoTotalSemana = salario.formatoMoneda(),
        estado = if (activo) "Activo" else "Inactivo"
    )
}

fun EmpleadoEntity.toEliminarUi(): EmpleadoEliminarUI {
    return EmpleadoEliminarUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        estado = if (activo) "Activo" else "Inactivo",
        trabajoActual = "Sin trabajo asignado",
        contrato = "No registrado",
        pagoTotal = salario.formatoMoneda()
    )
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