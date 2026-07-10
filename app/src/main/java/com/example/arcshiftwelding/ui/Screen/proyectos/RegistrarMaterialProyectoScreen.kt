package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMaterialProyectoScreen(
    proyectoId: Int,
    navController: NavController,
    proyectosViewModel: ProyectosViewModel
) {
    val productos by proyectosViewModel.productosInventario.collectAsState()
    val mensaje by proyectosViewModel.mensaje.collectAsState()

    var productoSeleccionado by remember { mutableStateOf<ProductoEntity?>(null) }
    var busquedaProducto by remember { mutableStateOf("") }

    var cantidadTexto by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    var fechaUso by remember {
        mutableStateOf(formatoFecha.format(Date()))
    }

    var mostrarDatePicker by remember { mutableStateOf(false) }


    val stockDisponible = productoSeleccionado?.stock ?: 0
    val cantidadUsada = cantidadTexto.toIntOrNull() ?: 0
    val cantidadValida = productoSeleccionado != null &&
            cantidadUsada > 0 &&
            cantidadUsada <= stockDisponible

    LaunchedEffect(mensaje) {
        if (mensaje == "Material registrado correctamente") {
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
                            fechaUso = formatoFecha.format(Date(millis))
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
                title = { Text("Registrar material") },
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

            BuscadorListaProyecto(
                textoBusqueda = busquedaProducto,
                onTextoBusquedaChange = { texto ->
                    busquedaProducto = texto
                    productoSeleccionado = null
                    cantidadTexto = ""
                },
                label = "Buscar material",
                placeholder = "Nombre, código o categoría",
                elementos = productos,
                textoPrincipal = { producto ->
                    producto.nombre
                },
                textoSecundario = { producto ->
                    "${producto.codigo} | ${producto.categoria} | Stock: ${producto.stock} ${producto.unidad}"
                },
                onSeleccionar = { producto ->
                    productoSeleccionado = producto
                    busquedaProducto = producto.nombre
                    cantidadTexto = ""
                },
                mostrarLista = productoSeleccionado == null
            )


            if (productoSeleccionado != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Material seleccionado",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = productoSeleccionado!!.nombre,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Disponible: $stockDisponible ${productoSeleccionado!!.unidad}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            OutlinedTextField(
                value = cantidadTexto,
                onValueChange = { nuevoValor ->
                    val cantidad = nuevoValor.toIntOrNull()

                    if (nuevoValor.isBlank()) {
                        cantidadTexto = ""
                    } else if (cantidad != null && cantidad <= stockDisponible) {
                        cantidadTexto = nuevoValor
                    }
                },
                label = { Text("Cantidad usada") },
                supportingText = {
                    when {
                        productoSeleccionado == null -> {
                            Text("Selecciona primero un material.")
                        }

                        stockDisponible <= 0 -> {
                            Text("No hay stock disponible.")
                        }

                        cantidadUsada > stockDisponible -> {
                            Text("No puedes usar más de $stockDisponible ${productoSeleccionado?.unidad ?: ""}.")
                        }

                        else -> {
                            Text("Disponible: $stockDisponible ${productoSeleccionado?.unidad ?: ""}.")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaUso,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de uso") },
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
                    val producto = productoSeleccionado ?: return@Button

                    proyectosViewModel.registrarMaterialUsado(
                        proyectoId = proyectoId,
                        productoId = producto.id,
                        cantidadUsada = cantidadUsada,
                        fechaUso = fechaUso,
                        observaciones = observaciones
                    )
                },
                enabled = cantidadValida,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar material")
            }
        }
    }
}