package com.example.arcshiftwelding.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.EmpleadoDao
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity
import com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoDetalleUI
import com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoEliminarUI
import com.example.arcshiftwelding.ui.Screen.empleados.EmpleadoUI
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmpleadosViewModel(
    private val empleadoDao: EmpleadoDao
) : ViewModel() {

    val empleados: StateFlow<List<EmpleadoUI>> = empleadoDao.obtenerEmpleados()
        .map { lista -> lista.map(EmpleadoEntity::toUi) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun insertarEmpleado(empleado: EmpleadoEntity) {
        viewModelScope.launch { empleadoDao.insertarEmpleado(empleado) }
    }

    fun actualizarEmpleado(empleado: EmpleadoEntity) {
        viewModelScope.launch { empleadoDao.actualizarEmpleado(empleado) }
    }

    fun eliminarEmpleado(empleado: EmpleadoEntity) {
        viewModelScope.launch { empleadoDao.eliminarEmpleado(empleado) }
    }

    fun observarEmpleado(id: Int): Flow<EmpleadoEntity?> =
        empleadoDao.observarEmpleadoPorId(id)
}

class EmpleadosViewModelFactory(
    private val empleadoDao: EmpleadoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmpleadosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmpleadosViewModel(empleadoDao) as T
        }
        throw IllegalArgumentException(
            "EmpleadosViewModelFactory no puede crear: ${modelClass.name}"
        )
    }
}

fun EmpleadoEntity.toUi(): EmpleadoUI {
    return EmpleadoUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        trabajo = trabajoActual.ifBlank { "Sin trabajo asignado" },
        contrato = descripcionModalidadPago(),
        pagoTotal = salario.formatoMoneda(),
        periodoPago = etiquetaPeriodoPago(),
        estado = if (activo) "Activo" else "Inactivo",
        color = if (activo) Color(0xFF2563EB) else Color(0xFF64748B),
        salario = salario,
        fotoUri = fotoUri
    )
}

fun EmpleadoEntity.toDetalleUi(): EmpleadoDetalleUI {
    return EmpleadoDetalleUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        telefono = telefono.ifBlank { "Sin teléfono registrado" },
        fechaIngreso = fechaIngreso.ifBlank { "Sin fecha registrada" },
        correo = correo.ifBlank { "Sin correo registrado" },
        direccion = direccion.ifBlank { "Sin dirección registrada" },
        porcentajeContrato = descripcionModalidadPago(),
        trabajoActual = trabajoActual.ifBlank { "Sin trabajo asignado" },
        pagoTotalSemana = salario.formatoMoneda(),
        estado = if (activo) "Activo" else "Inactivo",
        notas = notas.ifBlank { "Sin notas registradas" },
        fotoUri = fotoUri
    )
}

fun EmpleadoEntity.toEliminarUi(): EmpleadoEliminarUI {
    return EmpleadoEliminarUI(
        id = id,
        nombre = nombre,
        puesto = puesto,
        estado = if (activo) "Activo" else "Inactivo",
        trabajoActual = trabajoActual.ifBlank { "Sin trabajo asignado" },
        contrato = descripcionModalidadPago(),
        pagoTotal = salario.formatoMoneda()
    )
}

private fun EmpleadoEntity.descripcionModalidadPago(): String {
    val tipo = tipoPago.trim().lowercase(Locale("es", "MX"))
    return when {
        tipo.contains("%") || tipo.contains("trabajo") || tipo.contains("proyecto") -> {
            val porcentaje = porcentajeContrato.formatoPorcentajeContrato()
            if (porcentaje == "No registrado") "% por trabajo" else "% por trabajo: $porcentaje"
        }
        tipo.contains("día") || tipo.contains("dia") -> "Pago por día: ${salario.formatoMoneda()}"
        tipo.contains("semana") -> "Pago por semana: ${salario.formatoMoneda()}"
        tipoPago.isNotBlank() -> "$tipoPago: ${salario.formatoMoneda()}"
        else -> "Modalidad no registrada"
    }
}

private fun EmpleadoEntity.etiquetaPeriodoPago(): String {
    val tipo = tipoPago.lowercase(Locale("es", "MX"))
    return when {
        tipo.contains("día") || tipo.contains("dia") -> "Por día"
        tipo.contains("semana") -> "Por semana"
        tipo.contains("%") || tipo.contains("trabajo") || tipo.contains("proyecto") -> "Por proyecto"
        else -> "Pago"
    }
}

fun String.formatoPorcentajeContrato(): String {
    val valor = trim().replace("%", "")
    if (valor.isBlank()) return "No registrado"
    return "$valor%"
}

fun Double.formatoMoneda(): String {
    return "$" + String.format(Locale("es", "MX"), "%,.2f", this)
}

fun String.aDoubleMoneda(): Double {
    return replace("$", "")
        .replace(",", "")
        .trim()
        .toDoubleOrNull()
        ?: 0.0
}
