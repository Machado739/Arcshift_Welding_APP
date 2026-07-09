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
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoGastoScreen(
    navController: NavController,
    viewModel: GastosViewModel,
    proyectoIdRelacionado: Int? = null
) {
    var concepto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    var subtotal by remember { mutableStateOf("") }
    var ivaPorcentaje by remember { mutableStateOf("16") }
    var metodoPago by remember { mutableStateOf("") }
//    var formaPago by remember { mutableStateOf("") }

    var observaciones by remember { mutableStateOf("") }

    var proyecto by remember { mutableStateOf("") }
    var clienteSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var cotizacionSeleccionadaId by remember { mutableStateOf<Int?>(null) }
    val proyectosDb by viewModel.proyectos.collectAsState(initial = emptyList())
    val clientesDb by viewModel.clientesActivos.collectAsState(initial = emptyList())
    val cotizacionesDb by viewModel.cotizaciones.collectAsState(initial = emptyList())

    val cotizacionesFiltradas = if (clienteSeleccionadoId != null) {
        cotizacionesDb.filter { it.clienteId == clienteSeleccionadoId }
    } else {
        cotizacionesDb
    }

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
                proveedor.trim().isNotBlank() &&
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

    val metodosPago = listOf(
        "Efectivo",
        "Tarjeta",
        "Transferencia",
        "Cheque",
        "Crédito"
    )


    val opcionesIva = listOf(
        "0",
        "8",
        "16"
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

                    CampoTextoCompacto(
                        label = "Proveedor *",
                        value = proveedor,
                        onValueChange = {
                            if (it.length <= 80) proveedor = it
                        },
                        placeholder = "Ej. Aceros del Norte",
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

                    CampoDropdownCompacto(
                        label = "IVA (%)",
                        value = ivaPorcentaje,
                        opciones = opcionesIva,
                        onValueChange = { ivaPorcentaje = it },
                        placeholder = "IVA",
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
                        placeholder = "Método de pago",
                        modifier = Modifier.fillMaxWidth()
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
                    CampoSelectorProyectoNuevoGasto(
                        label = "Proyecto",
                        proyectos = proyectosDb,
                        proyectoSeleccionado = proyecto,
                        onProyectoSeleccionado = { proyecto = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    CampoSelectorClienteNuevoGasto(
                        label = "Cliente",
                        clientes = clientesDb,
                        clienteSeleccionadoId = clienteSeleccionadoId,
                        onClienteSeleccionado = { nuevoClienteId ->
                            clienteSeleccionadoId = nuevoClienteId

                            val cotizacionActual = cotizacionesDb.firstOrNull {
                                it.id == cotizacionSeleccionadaId
                            }

                            if (nuevoClienteId == null || cotizacionActual?.clienteId != nuevoClienteId) {
                                cotizacionSeleccionadaId = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    CampoSelectorCotizacionNuevoGasto(
                        label = "Cotización",
                        cotizaciones = cotizacionesFiltradas,
                        cotizacionSeleccionadaId = cotizacionSeleccionadaId,
                        onCotizacionSeleccionada = { nuevaCotizacionId ->
                            cotizacionSeleccionadaId = nuevaCotizacionId

                            val cotizacionSeleccionada = cotizacionesDb.firstOrNull {
                                it.id == nuevaCotizacionId
                            }

                            if (cotizacionSeleccionada != null) {
                                clienteSeleccionadoId = cotizacionSeleccionada.clienteId
                            }
                        },
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
                                proveedor = proveedor.trim(),
                                subtotal = subtotalValor,
                                ivaPorcentaje = ivaValor,
                                iva = ivaCalculado,
                                proyectoId = proyectoIdRelacionado,
                                proyectoNombre = "",
                                total = totalCalculado,
                                metodoPago = metodoPago,
                                formaPago = "",
                                telefonoProveedor = telefonoProveedor.ifBlank { null },
                                correoProveedor = correoProveedor.ifBlank { null },
                                rfcProveedor = rfcProveedor.ifBlank { null },
                                observaciones = observaciones.ifBlank { null },
                                proyecto = proyecto.takeIf { it.isNotBlank() },
                                clienteId = clienteSeleccionadoId,
                                cotizacionId = cotizacionSeleccionadaId
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSelectorClienteNuevoGasto(
    label: String,
    clientes: List<ClienteEntity>,
    clienteSeleccionadoId: Int?,
    onClienteSeleccionado: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val clienteSeleccionado = clientes.firstOrNull {
        it.id == clienteSeleccionadoId
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = clienteSeleccionado?.nombre ?: "Sin cliente",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin cliente") },
                onClick = {
                    onClienteSeleccionado(null)
                    expanded = false
                }
            )

            clientes.forEach { cliente ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(cliente.nombre, fontWeight = FontWeight.SemiBold)
                            if (cliente.empresa.isNotBlank()) {
                                Text(
                                    cliente.empresa,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    },
                    onClick = {
                        onClienteSeleccionado(cliente.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSelectorCotizacionNuevoGasto(
    label: String,
    cotizaciones: List<CotizacionEntity>,
    cotizacionSeleccionadaId: Int?,
    onCotizacionSeleccionada: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val cotizacionSeleccionada = cotizaciones.firstOrNull {
        it.id == cotizacionSeleccionadaId
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = cotizacionSeleccionada?.folio ?: "Sin cotización",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin cotización") },
                onClick = {
                    onCotizacionSeleccionada(null)
                    expanded = false
                }
            )

            cotizaciones.forEach { cotizacion ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(cotizacion.folio, fontWeight = FontWeight.SemiBold)
                            Text(
                                cotizacion.descripcionTrabajo,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    },
                    onClick = {
                        onCotizacionSeleccionada(cotizacion.id)
                        expanded = false
                    }
                )
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

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = convertirFechaCompactoAMillis(value)
    )

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
                            onValueChange(
                                formatearFechaCompactoUTC(fechaSeleccionada)
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

fun formatearFechaCompactoUTC(millis: Long): String {
    val calendario = java.util.Calendar.getInstance(
        java.util.TimeZone.getTimeZone("UTC")
    ).apply {
        timeInMillis = millis
    }

    val dia = calendario.get(java.util.Calendar.DAY_OF_MONTH)
    val mes = calendario.get(java.util.Calendar.MONTH) + 1
    val anio = calendario.get(java.util.Calendar.YEAR)

    return "%02d/%02d/%04d".format(dia, mes, anio)
}

fun convertirFechaCompactoAMillis(fecha: String): Long? {
    return try {
        val formato = java.text.SimpleDateFormat(
            "dd/MM/yyyy",
            java.util.Locale.getDefault()
        )

        formato.timeZone = java.util.TimeZone.getTimeZone("UTC")
        formato.isLenient = false

        formato.parse(fecha)?.time
    } catch (e: Exception) {
        null
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSelectorProyectoNuevoGasto(
    label: String,
    proyectos: List<ProyectoEntity>,
    proyectoSeleccionado: String,
    onProyectoSeleccionado: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = proyectoSeleccionado.ifBlank { "Sin proyecto" },
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin proyecto") },
                onClick = {
                    onProyectoSeleccionado("")
                    expanded = false
                }
            )

            proyectos.forEach { proyecto ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = proyecto.nombre,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (proyecto.estado.isNotBlank()) {
                                Text(
                                    text = proyecto.estado,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    },
                    onClick = {
                        onProyectoSeleccionado(proyecto.nombre)
                        expanded = false
                    }
                )
            }
        }
    }
}