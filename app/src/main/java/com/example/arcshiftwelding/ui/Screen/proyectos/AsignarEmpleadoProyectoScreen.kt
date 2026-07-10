package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignarEmpleadoProyectoScreen(
    proyectoId: Int,
    navController: NavController,
    proyectosViewModel: ProyectosViewModel
) {
    val empleados by proyectosViewModel.empleadosDisponibles.collectAsState()
    val mensaje by proyectosViewModel.mensaje.collectAsState()
    val proyectos by proyectosViewModel.proyectos.collectAsState()

    val proyectoActual = proyectos.firstOrNull { it.id == proyectoId }

    var empleadoSeleccionado by remember { mutableStateOf<EmpleadoEntity?>(null) }
    var busquedaEmpleado by remember { mutableStateOf("") }
    var tiempoTrabajoTexto by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    var fechaAsignacion by remember {
        mutableStateOf(formatoFecha.format(Date()))
    }

    var mostrarDatePicker by remember { mutableStateOf(false) }



    val contratoEmpleado = empleadoSeleccionado?.porcentajeContrato.orEmpty()
    val tipoPagoEmpleado = obtenerTipoPagoEmpleadoProyecto(contratoEmpleado)
    val valorPagoEmpleado = obtenerValorPagoEmpleadoProyecto(
        contrato = contratoEmpleado,
        salarioRespaldo = empleadoSeleccionado?.salario ?: 0.0
    )

    val esPagoPorDia = tipoPagoEmpleado == "Día"
    val esPagoPorSemana = tipoPagoEmpleado == "Semana"
    val esPagoPorHora = tipoPagoEmpleado == "Hora"
    val esPagoPorPorcentaje = tipoPagoEmpleado == "Porcentaje"

    val requiereTiempo = esPagoPorDia || esPagoPorSemana || esPagoPorHora
    val tiempoTrabajo = tiempoTrabajoTexto.replace(",", ".").toDoubleOrNull() ?: 0.0

    val formularioValido = empleadoSeleccionado != null &&
            tipoPagoEmpleado != "Sin definir" &&
            (!requiereTiempo || tiempoTrabajo > 0.0)

    LaunchedEffect(mensaje) {
        if (mensaje == "Empleado asignado al proyecto") {
            proyectosViewModel.limpiarMensaje()
            navController.popBackStack()
        }
    }

    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = {
                mostrarDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            fechaAsignacion = formatoFecha.format(Date(millis))
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar empleado") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            BuscadorListaProyecto(
                textoBusqueda = busquedaEmpleado,
                onTextoBusquedaChange = { texto ->
                    busquedaEmpleado = texto
                    empleadoSeleccionado = null
                    tiempoTrabajoTexto = ""
                },
                label = "Buscar empleado",
                placeholder = "Nombre o puesto",
                elementos = empleados,
                textoPrincipal = { empleado ->
                    empleado.nombre
                },
                textoSecundario = { empleado ->
                    "${empleado.puesto} | ${empleado.porcentajeContrato.ifBlank { "Sin contrato" }}"
                },
                onSeleccionar = { empleado ->
                    empleadoSeleccionado = empleado
                    busquedaEmpleado = empleado.nombre
                    tiempoTrabajoTexto = ""
                },
                mostrarLista = empleadoSeleccionado == null
            )

            if (empleadoSeleccionado != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Empleado seleccionado",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Text(
                            text = empleadoSeleccionado!!.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Puesto: ${empleadoSeleccionado!!.puesto.ifBlank { "Sin puesto" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )

                        Text(
                            text = "Tipo de pago: ${textoTipoPagoEmpleadoProyecto(tipoPagoEmpleado)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.SemiBold
                        )

                        if (esPagoPorPorcentaje) {
                            Text(
                                text = "Porcentaje registrado: ${"%.2f".format(valorPagoEmpleado)}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2563EB)
                            )
                        } else {
                            Text(
                                text = "Pago base: ${formatoMonedaProyecto(valorPagoEmpleado)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2563EB)
                            )
                        }
                    }
                }
            }

            if (empleadoSeleccionado != null && requiereTiempo) {
                OutlinedTextField(
                    value = tiempoTrabajoTexto,
                    onValueChange = { nuevoValor ->
                        val valorValido = nuevoValor.isBlank() ||
                                nuevoValor.matches(Regex("^\\d*([.,]\\d*)?$"))

                        if (valorValido) {
                            tiempoTrabajoTexto = nuevoValor
                        }
                    },
                    label = {
                        Text(
                            when {
                                esPagoPorSemana -> "Semanas estimadas"
                                esPagoPorHora -> "Horas estimadas"
                                else -> "Días estimados"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            when {
                                esPagoPorSemana -> "Ej. 2"
                                esPagoPorHora -> "Ej. 8"
                                else -> "Ej. 5"
                            }
                        )
                    },
                    supportingText = {
                        Text("Este dato se puede modificar después si el proyecto se alarga.")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (empleadoSeleccionado != null && esPagoPorPorcentaje) {
                Text(
                    text = "El costo se calculará con el porcentaje del empleado sobre el presupuesto del proyecto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2563EB),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                )
            }

            OutlinedTextField(
                value = fechaAsignacion,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de asignación") },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            mostrarDatePicker = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Seleccionar fecha"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (!mensaje.isNullOrBlank()) {
                Text(
                    text = mensaje ?: "",
                    color = if (mensaje == "Este empleado ya está asignado al proyecto") {
                        Color(0xFFDC2626)
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val empleado = empleadoSeleccionado ?: return@Button

                    val diasTrabajados = when {
                        esPagoPorDia || esPagoPorSemana -> tiempoTrabajo
                        else -> 0.0
                    }

                    val horasTrabajadas = when {
                        esPagoPorHora -> tiempoTrabajo
                        else -> 0.0
                    }

                    proyectosViewModel.asignarEmpleadoAProyecto(
                        proyectoId = proyectoId,
                        empleado = empleado,
                        presupuestoEstimado = proyectoActual?.presupuestoEstimado ?: 0.0,
                        diasTrabajados = diasTrabajados,
                        horasTrabajadas = horasTrabajadas,
                        fechaAsignacion = fechaAsignacion,
                        observaciones = observaciones
                    )
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Asignar empleado")
            }
        }
    }
}

fun obtenerTipoPagoEmpleadoProyecto(contrato: String): String {
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

fun obtenerValorPagoEmpleadoProyecto(
    contrato: String,
    salarioRespaldo: Double
): Double {
    val contratoLimpio = contrato.trim()

    if (contratoLimpio.isBlank()) {
        return salarioRespaldo
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
        .toDoubleOrNull() ?: salarioRespaldo
}

fun textoTipoPagoEmpleadoProyecto(tipoPago: String): String {
    return when (tipoPago) {
        "Porcentaje" -> "% por trabajo"
        "Día" -> "Pago por día"
        "Semana" -> "Pago por semana"
        "Hora" -> "Pago por hora"
        else -> "Sin definir"
    }
}