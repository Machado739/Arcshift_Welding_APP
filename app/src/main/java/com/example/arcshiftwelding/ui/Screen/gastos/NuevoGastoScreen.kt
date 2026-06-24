package com.example.arcshiftwelding.ui.Screen.gastos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.ui.gastos.GastosViewModel
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoGastoScreen(
    navController: NavController,
    viewModel: GastosViewModel
) {
    var concepto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    var subtotal by remember { mutableStateOf("") }
    var ivaPorcentaje by remember { mutableStateOf("16") }
    var metodoPago by remember { mutableStateOf("") }
    var formaPago by remember { mutableStateOf("") }

    var observaciones by remember { mutableStateOf("") }

    var proyecto by remember { mutableStateOf("") }
    var cotizacion by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }

    var mostrarError by remember { mutableStateOf(false) }

    var telefonoProveedor by remember { mutableStateOf("") }
    var correoProveedor by remember { mutableStateOf("") }
    var rfcProveedor by remember { mutableStateOf("") }


    val subtotalValor = subtotal.replace(",", ".").toDoubleOrNull() ?: 0.0
    val ivaValor = ivaPorcentaje.replace(",", ".").toDoubleOrNull() ?: 0.0
    val ivaCalculado = subtotalValor * (ivaValor / 100.0)
    val totalCalculado = subtotalValor + ivaCalculado

    val datosValidos =
        concepto.isNotBlank() &&
                categoria.isNotBlank() &&
                fecha.isNotBlank() &&
                proveedor.isNotBlank() &&
                subtotalValor > 0.0 &&
                metodoPago.isNotBlank()

    val categorias = listOf(
        "Materiales",
        "Servicios",
        "Transporte",
        "Nómina",
        "Herramientas",
        "Seguridad",
        "Otros"
    )

    val proveedores = listOf(
        "Aceros del Norte",
        "Gasolinera PEMEX",
        "Taller Mecánico JR",
        "CFE",
        "Ferretería Industrial",
        "Infra",
        "JMAS",
        "Otro"
    )

    val metodosPago = listOf(
        "Efectivo",
        "Tarjeta",
        "Transferencia",
        "Cheque",
        "Crédito"
    )

    val formasPago = listOf(
        "Contado",
        "Crédito",
        "Anticipo",
        "Parcialidad"
    )

    val proyectos = listOf(
        "Portón metálico",
        "Estructura para techo",
        "Reparación de remolque",
        "Escalera industrial",
        "Sin proyecto"
    )

    val cotizaciones = listOf(
        "COT-001 - Portón metálico",
        "COT-002 - Estructura para techo",
        "COT-003 - Reparación de remolque",
        "Sin cotización"
    )

    val clientes = listOf(
        "Eduardo Barrios",
        "Jose Vera",
        "Maria Lopez",
        "Carlos Ruiz",
        "Sin cliente"
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(
                        start = 17.dp,
                        top = 8.dp,
                        end = 14.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }

                Text(
                    text = "Nuevo Gasto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            if (mostrarError) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEE2E2)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Completa los campos obligatorios antes de guardar.",
                        color = Color(0xFFDC2626),
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            TarjetaSeccion(
                titulo = "Información general",
                icono = Icons.Default.Info
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoTextoCompacto(
                        label = "Concepto *",
                        value = concepto,
                        onValueChange = { concepto = it },
                        placeholder = "Ej. Compra de material",
                        modifier = Modifier.weight(1f)
                    )

                    CampoDropdownCompacto(
                        label = "Categoría *",
                        value = categoria,
                        opciones = categorias,
                        onValueChange = { categoria = it },
                        placeholder = "Categoría",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoFechaCompacto(
                        label = "Fecha *",
                        value = fecha,
                        onValueChange = { fecha = it },
                        modifier = Modifier.weight(0.9f)
                    )

                    CampoDropdownCompacto(
                        label = "Proveedor *",
                        value = proveedor,
                        opciones = proveedores,
                        onValueChange = { proveedor = it },
                        placeholder = "Proveedor",
                        modifier = Modifier.weight(1.1f)
                    )
                }
            }

            TarjetaSeccion(
                titulo = "Información financiera",
                icono = Icons.Default.AttachMoney
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoFinancieroCompacto(
                        label = "Subtotal *",
                        value = subtotal,
                        onValueChange = { subtotal = it },
                        placeholder = "$ 0.00",
                        modifier = Modifier.weight(1f)
                    )

                    CampoFinancieroCompacto(
                        label = "IVA (%)",
                        value = ivaPorcentaje,
                        onValueChange = { ivaPorcentaje = it },
                        placeholder = "16",
                        modifier = Modifier.weight(1f)
                    )

                    CampoFinancieroCompacto(
                        label = "IVA",
                        value = "$ ${"%.2f".format(ivaCalculado)}",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEFF7EF)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Total *",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "$ ${"%.2f".format(totalCalculado)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoDropdownCompacto(
                        label = "Método de pago *",
                        value = metodoPago,
                        opciones = metodosPago,
                        onValueChange = { metodoPago = it },
                        placeholder = "Método",
                        modifier = Modifier.weight(1.1f)
                    )

                    CampoDropdownCompacto(
                        label = "Forma de pago",
                        value = formaPago,
                        opciones = formasPago,
                        onValueChange = { formaPago = it },
                        placeholder = "Forma",
                        modifier = Modifier.weight(0.9f)
                    )
                }
            }

            TarjetaSeccion(
                titulo = "Evidencia / Comprobantes",
                icono = Icons.Default.AttachFile
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotonAdjunto(
                        texto = "Tomar foto",
                        icono = Icons.Default.CameraAlt,
                        modifier = Modifier.weight(1f)
                    )

                    BotonAdjunto(
                        texto = "Subir PDF",
                        icono = Icons.Default.Description,
                        modifier = Modifier.weight(1f)
                    )

                    BotonAdjunto(
                        texto = "Adjuntar archivo",
                        subtitulo = "Máx. 10 MB",
                        icono = Icons.Default.AttachFile,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            TarjetaSeccion(
                titulo = "Observaciones",
                icono = Icons.Default.Edit
            ) {
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = {
                        if (it.length <= 300) {
                            observaciones = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text("Agrega notas u observaciones (opcional)")
                    },
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${observaciones.length}/300",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            TarjetaSeccion(
                titulo = "Relacionado con (opcional)",
                icono = Icons.Default.Link
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoDropdownCompacto(
                        label = "Proyecto",
                        value = proyecto,
                        opciones = proyectos,
                        onValueChange = { proyecto = it },
                        placeholder = "Seleccionar proyecto",
                        modifier = Modifier.fillMaxWidth()
                    )

                    CampoDropdownCompacto(
                        label = "Cotización",
                        value = cotizacion,
                        opciones = cotizaciones,
                        onValueChange = { cotizacion = it },
                        placeholder = "Seleccionar cotización",
                        modifier = Modifier.fillMaxWidth()
                    )

                    CampoDropdownCompacto(
                        label = "Cliente",
                        value = cliente,
                        opciones = clientes,
                        onValueChange = { cliente = it },
                        placeholder = "Seleccionar cliente",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (datosValidos) {
                            viewModel.guardarGasto(
                                concepto = concepto,
                                categoria = categoria,
                                fecha = fecha,
                                proveedor = proveedor,
                                subtotal = subtotalValor,
                                ivaPorcentaje = ivaValor,
                                iva = ivaCalculado,
                                total = totalCalculado,
                                metodoPago = metodoPago,
                                formaPago = formaPago,
                                telefonoProveedor = telefonoProveedor,
                                correoProveedor = correoProveedor,
                                rfcProveedor = rfcProveedor,
                                observaciones = observaciones,
                                proyecto = proyecto,
                                cotizacion = cotizacion,
                                cliente = cliente
                            )

                            navController.popBackStack()
                        } else {
                            mostrarError = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Guardar Gasto")
                }
            }
        }
    }
}


@Composable
fun CampoFinancieroCompacto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(58.dp),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        },
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        },
        readOnly = readOnly,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = RoundedCornerShape(10.dp)
    )
}
@Composable
fun TarjetaSeccion(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF424242),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            contenido()
        }
    }
}

@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        placeholder = {
            if (placeholder.isNotEmpty()) Text(placeholder)
        },
        readOnly = readOnly,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = leadingIcon
    )
}

@Composable
fun CampoSelector(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        label = { Text(label) },
        readOnly = true,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        trailingIcon = {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Desplegar")
        }
    )
}

@Composable
fun BotonAdjunto(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    subtitulo: String = ""
) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(82.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, contentDescription = null)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium
            )
            if (subtitulo.isNotEmpty()) {
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDropdown(
    label: String,
    value: String,
    opciones: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Seleccionar"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = {
                Text(label)
            },
            placeholder = {
                Text(placeholder)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(opcion)
                    },
                    onClick = {
                        onValueChange(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = {
            Text(label)
        },
        placeholder = {
            Text("Seleccionar fecha")
        },
        leadingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = null)
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    mostrarCalendario = true
                }
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Seleccionar fecha")
            }
        },
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    )

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = {
                mostrarCalendario = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val fechaSeleccionada = datePickerState.selectedDateMillis

                        if (fechaSeleccionada != null) {
                            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            onValueChange(
                                formato.format(Date(fechaSeleccionada))
                            )
                        }

                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarCalendario = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDropdownCompacto(
    label: String,
    value: String,
    opciones: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Seleccionar"
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(58.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opcion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onValueChange(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaCompacto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Box(
        modifier = modifier
            .height(58.dp)
            .clickable {
                mostrarCalendario = true
            }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            singleLine = true,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            placeholder = {
                Text(
                    text = "Fecha",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Seleccionar fecha",
                    modifier = Modifier.size(18.dp)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Gray,
                disabledLabelColor = Color.DarkGray,
                disabledTrailingIconColor = Color.DarkGray,
                disabledPlaceholderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxSize()
        )
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = {
                mostrarCalendario = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val fechaSeleccionada = datePickerState.selectedDateMillis

                        if (fechaSeleccionada != null) {
                            val formato = java.text.SimpleDateFormat(
                                "dd/MM/yyyy",
                                java.util.Locale.getDefault()
                            )

                            onValueChange(
                                formato.format(java.util.Date(fechaSeleccionada))
                            )
                        }

                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarCalendario = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@Composable
fun CampoTextoCompacto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(58.dp),
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
        },
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        readOnly = readOnly,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(10.dp)
    )
}