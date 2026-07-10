package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEmpleadoProyectoScreen(
    proyectoId: Int,
    empleadoProyectoId: Int,
    navController: NavController,
    proyectosViewModel: ProyectosViewModel
) {
    val empleadoProyecto by proyectosViewModel
        .obtenerEmpleadoProyectoPorId(empleadoProyectoId)
        .collectAsState(initial = null)

    val proyectos by proyectosViewModel.proyectos.collectAsState()
    val proyectoActual = proyectos.firstOrNull { it.id == proyectoId }

    var tiempoTexto by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(empleadoProyecto?.id) {
        val empleado = empleadoProyecto

        if (empleado != null) {
            tiempoTexto = when (empleado.tipoPago) {
                "Hora" -> empleado.horasTrabajadas.toString()
                "Día", "Semana" -> empleado.diasTrabajados.toString()
                else -> ""
            }

            observaciones = empleado.observaciones
        }
    }

    if (empleadoProyecto == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Editar empleado") },
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
            ) {
                Text("Empleado asignado no encontrado.")
            }
        }

        return
    }

    val empleado = empleadoProyecto!!

    val requiereTiempo = empleado.tipoPago == "Día" ||
            empleado.tipoPago == "Semana" ||
            empleado.tipoPago == "Hora"

    val tiempo = tiempoTexto.replace(",", ".").toDoubleOrNull() ?: 0.0

    val diasTrabajados = when (empleado.tipoPago) {
        "Día", "Semana" -> tiempo
        else -> 0.0
    }

    val horasTrabajadas = when (empleado.tipoPago) {
        "Hora" -> tiempo
        else -> 0.0
    }

    val costoEstimado = calcularCostoEmpleadoAsignadoPreview(
        tipoPago = empleado.tipoPago,
        pagoAcordado = empleado.pagoAcordado,
        diasTrabajados = diasTrabajados,
        horasTrabajadas = horasTrabajadas,
        porcentaje = empleado.porcentaje,
        presupuestoEstimado = proyectoActual?.presupuestoEstimado ?: 0.0
    )

    val formularioValido = !requiereTiempo || tiempo > 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar empleado") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { mostrarDialogoEliminar = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar empleado",
                            tint = Color(0xFFDC2626)
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = empleado.nombreEmpleado,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = empleado.puesto.ifBlank { "Sin puesto" },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )

                    Text(
                        text = "Tipo de pago: ${textoTipoPagoEmpleadoAsignado(empleado.tipoPago)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.SemiBold
                    )

                    val detallePago = when (empleado.tipoPago) {
                        "Porcentaje" -> "${empleado.porcentaje}% del proyecto"
                        else -> formatoMonedaProyecto(empleado.pagoAcordado)
                    }

                    Text(
                        text = "Pago: $detallePago",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2563EB)
                    )
                }
            }

            if (requiereTiempo) {
                OutlinedTextField(
                    value = tiempoTexto,
                    onValueChange = { nuevoValor ->
                        val valido = nuevoValor.isBlank() ||
                                nuevoValor.matches(Regex("^\\d*([.,]\\d*)?$"))

                        if (valido) {
                            tiempoTexto = nuevoValor
                        }
                    },
                    label = {
                        Text(
                            when (empleado.tipoPago) {
                                "Semana" -> "Semanas trabajadas"
                                "Hora" -> "Horas trabajadas"
                                else -> "Días trabajados"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            when (empleado.tipoPago) {
                                "Semana" -> "Ej. 2"
                                "Hora" -> "Ej. 8"
                                else -> "Ej. 5"
                            }
                        )
                    },
                    supportingText = {
                        Text("Modifica este dato si el proyecto se alargó.")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (empleado.tipoPago == "Porcentaje") {
                Text(
                    text = "Este empleado se calcula automáticamente con ${empleado.porcentaje}% sobre el presupuesto del proyecto.",
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEFF6FF)
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Costo calculado",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1E40AF)
                    )

                    Text(
                        text = formatoMonedaProyecto(costoEstimado),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF1E40AF),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    proyectosViewModel.actualizarEmpleadoAsignadoProyecto(
                        empleadoProyecto = empleado,
                        presupuestoEstimado = proyectoActual?.presupuestoEstimado ?: 0.0,
                        diasTrabajados = diasTrabajados,
                        horasTrabajadas = horasTrabajadas,
                        observaciones = observaciones
                    )

                    navController.popBackStack()
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )

                Text("Guardar cambios")
            }

            OutlinedButton(
                onClick = { mostrarDialogoEliminar = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFDC2626)
                )

                Text(
                    text = "Eliminar del proyecto",
                    color = Color(0xFFDC2626)
                )
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = {
                Text("Eliminar empleado")
            },
            text = {
                Text("¿Deseas eliminar a ${empleado.nombreEmpleado} de este proyecto?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        proyectosViewModel.eliminarEmpleadoAsignadoProyecto(empleado.id)
                        mostrarDialogoEliminar = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminar = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

fun calcularCostoEmpleadoAsignadoPreview(
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

fun textoTipoPagoEmpleadoAsignado(tipoPago: String): String {
    return when (tipoPago) {
        "Día" -> "Pago por día"
        "Semana" -> "Pago por semana"
        "Hora" -> "Pago por hora"
        "Porcentaje" -> "% por trabajo"
        else -> "Sin definir"
    }
}