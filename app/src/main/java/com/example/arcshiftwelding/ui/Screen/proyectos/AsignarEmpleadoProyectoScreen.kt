package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
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

    var empleadoSeleccionado by remember { mutableStateOf<EmpleadoEntity?>(null) }
    var busquedaEmpleado by remember { mutableStateOf("") }
    var expandidoEmpleado by remember { mutableStateOf(false) }
    var observaciones by remember { mutableStateOf("") }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    var fechaAsignacion by remember {
        mutableStateOf(formatoFecha.format(Date()))
    }

    var mostrarDatePicker by remember { mutableStateOf(false) }

    val empleadosFiltrados = empleados.filter { empleado ->
        empleado.nombre.contains(busquedaEmpleado, ignoreCase = true) ||
                empleado.puesto.contains(busquedaEmpleado, ignoreCase = true)
    }

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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ExposedDropdownMenuBox(
                expanded = expandidoEmpleado,
                onExpandedChange = {
                    expandidoEmpleado = !expandidoEmpleado
                }
            ) {
                OutlinedTextField(
                    value = busquedaEmpleado,
                    onValueChange = {
                        busquedaEmpleado = it
                        expandidoEmpleado = true
                        empleadoSeleccionado = null
                    },
                    label = { Text("Buscar empleado") },
                    placeholder = { Text("Nombre o puesto") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoEmpleado)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandidoEmpleado,
                    onDismissRequest = {
                        expandidoEmpleado = false
                    }
                ) {
                    if (empleadosFiltrados.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text("No se encontraron empleados")
                            },
                            onClick = {}
                        )
                    } else {
                        empleadosFiltrados.forEach { empleado ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = empleado.nombre,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = empleado.puesto,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                },
                                onClick = {
                                    empleadoSeleccionado = empleado
                                    busquedaEmpleado = empleado.nombre
                                    expandidoEmpleado = false
                                }
                            )
                        }
                    }
                }
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
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val empleado = empleadoSeleccionado ?: return@Button

                    proyectosViewModel.asignarEmpleadoAProyecto(
                        proyectoId = proyectoId,
                        empleado = empleado,
                        fechaAsignacion = fechaAsignacion,
                        observaciones = observaciones
                    )
                },
                enabled = empleadoSeleccionado != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Asignar empleado")
            }
        }
    }
}