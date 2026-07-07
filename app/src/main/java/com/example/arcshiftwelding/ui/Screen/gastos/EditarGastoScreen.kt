package com.example.arcshiftwelding.ui.gastos

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
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.Screen.gastos.TarjetaDetalleGasto
import com.example.arcshiftwelding.ui.gastos.GastosViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarGastoScreen(
    navController: NavController,
    gastoId: Int?,
    viewModel: GastosViewModel
) {
    val gastoActual by if (gastoId != null) {
        viewModel.obtenerGastoPorId(gastoId).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    val categoriasGasto = listOf(
        "Materiales",
        "Servicios",
        "Transporte",
        "Nómina",
        "Herramientas",
        "Seguridad",
        "Otro"
    )


    val porcentajesIva = listOf(
        "0",
        "8",
        "16"
    )

    val metodosPago = listOf(
        "Efectivo",
        "Tarjeta",
        "Transferencia",
        "Cheque",
        "Crédito"
    )
    val proyectosDb by viewModel.proyectos.collectAsState(initial = emptyList())

    val opcionesProyectos = remember(proyectosDb) {
        listOf("Sin proyecto") + proyectosDb.map { proyecto ->
            proyecto.nombre
        }
    }


    val clientesDb by viewModel.clientesActivos.collectAsState(initial = emptyList())
    val cotizacionesDb by viewModel.cotizaciones.collectAsState(initial = emptyList())

    var concepto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    var subtotal by remember { mutableStateOf("") }
    var ivaPorcentaje by remember { mutableStateOf("16") }
    var metodoPago by remember { mutableStateOf("") }
  //  var formaPago by remember { mutableStateOf("") }

    var telefonoProveedor by remember { mutableStateOf("") }
    var correoProveedor by remember { mutableStateOf("") }
    var rfcProveedor by remember { mutableStateOf("") }

    var observaciones by remember { mutableStateOf("") }

    var proyecto by remember { mutableStateOf("") }
    var clienteSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var cotizacionSeleccionadaId by remember { mutableStateOf<Int?>(null) }


    val subtotalValor = subtotal.replace(",", ".").toDoubleOrNull() ?: 0.0
    val ivaValor = ivaPorcentaje.replace(",", ".").toDoubleOrNull() ?: 0.0
    val ivaCalculado = subtotalValor * (ivaValor / 100.0)
    val totalCalculado = subtotalValor + ivaCalculado

    val cotizacionesFiltradas = if (clienteSeleccionadoId != null) {
        cotizacionesDb.filter { it.clienteId == clienteSeleccionadoId }
    } else {
        cotizacionesDb
    }

    LaunchedEffect(gastoActual) {
        gastoActual?.let { gasto ->
            concepto = gasto.concepto
            categoria = gasto.categoria
            fecha = gasto.fecha
            proveedor = gasto.proveedor

            subtotal = gasto.subtotal.toString()
            ivaPorcentaje = gasto.ivaPorcentaje.toString()
            metodoPago = gasto.metodoPago
          //  formaPago = gasto.formaPago

            telefonoProveedor = gasto.telefonoProveedor ?: ""
            correoProveedor = gasto.correoProveedor ?: ""
            rfcProveedor = gasto.rfcProveedor ?: ""

            observaciones = gasto.observaciones ?: ""

            proyecto = gasto.proyecto ?: ""
            clienteSeleccionadoId = gasto.clienteId
            cotizacionSeleccionadaId = gasto.cotizacionId
        }
    }

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
                    text = "Editar Gasto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )



            }

        },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            TarjetaDetalleGasto(
                titulo = "Información general",
                icono = Icons.Default.Info
            ) {
                CampoTextoEditar(
                    label = "Concepto *",
                    value = concepto,
                    onValueChange = { concepto = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoSelectorEditar(
                        label = "Categoría *",
                        value = categoria,
                        opciones = categoriasGasto,
                        onValueChange = { categoria = it },
                        modifier = Modifier.weight(1f)
                    )

                    CampoFechaEditar(
                        label = "Fecha *",
                        value = fecha,
                        onValueChange = { fecha = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CampoTextoEditar(
                    label = "Proveedor *",
                    value = proveedor,
                    onValueChange = {
                        if (it.length <= 80) proveedor = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TarjetaDetalleGasto(
                titulo = "Información financiera",
                icono = Icons.Default.AttachMoney
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoTextoEditar(
                        label = "Subtotal *",
                        value = subtotal,
                        onValueChange = { subtotal = it },
                        modifier = Modifier.weight(1f)
                    )

                    CampoSelectorEditar(
                        label = "IVA (%)",
                        value = ivaPorcentaje,
                        opciones = porcentajesIva,
                        onValueChange = { ivaPorcentaje = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CampoTextoEditar(
                    label = "IVA calculado",
                    value = "$ ${"%.2f".format(ivaCalculado)}",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Total",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = "$ ${"%.2f".format(totalCalculado)}",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoSelectorEditar(
                        label = "Método de pago *",
                        value = metodoPago,
                        opciones = metodosPago,
                        onValueChange = { metodoPago = it },
                        modifier = Modifier.weight(1f)
                    )

                }
            }
/*
            TarjetaDetalleGasto(
                titulo = "Proveedor",
                icono = Icons.Default.Business
            ) {
                CampoTextoEditar(
                    label = "Teléfono",
                    value = telefonoProveedor,
                    onValueChange = { telefonoProveedor = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoTextoEditar(
                    label = "Correo",
                    value = correoProveedor,
                    onValueChange = { correoProveedor = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoTextoEditar(
                    label = "RFC",
                    value = rfcProveedor,
                    onValueChange = { rfcProveedor = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
*/
            TarjetaDetalleGasto(
                titulo = "Factura / Comprobante",
                icono = Icons.Default.AttachFile
            ) {
                Text(
                    text = "Archivos actuales",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                ArchivoEditableFactura(
                    nombre = "FACTURA_1005.pdf",
                    peso = "522 KB"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ArchivoEditableFactura(
                    nombre = "TICKET_250520.jpg",
                    peso = "186 KB"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cambiar PDF")
                    }

                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nueva foto")
                    }
                }
            }

            TarjetaDetalleGasto(
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
                    shape = RoundedCornerShape(10.dp),
                    placeholder = {
                        Text("Agrega observaciones")
                    },
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

            TarjetaDetalleGasto(
                titulo = "Relacionado con",
                icono = Icons.Default.Link
            ) {
                CampoSelectorEditar(
                    label = "Proyecto",
                    value = proyecto.ifBlank { "Sin proyecto" },
                    opciones = opcionesProyectos,
                    onValueChange = { nuevoProyecto ->
                        proyecto = if (nuevoProyecto == "Sin proyecto") {
                            ""
                        } else {
                            nuevoProyecto
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoSelectorClienteGasto(
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

                Spacer(modifier = Modifier.height(8.dp))

                CampoSelectorCotizacionGasto(
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (gastoId != null) {
                            val gastoEditado = GastoEntity(
                                id = gastoId,
                                concepto = concepto,
                                categoria = categoria,
                                fecha = fecha,
                                proveedor = proveedor.trim(),
                                subtotal = subtotalValor,
                                ivaPorcentaje = ivaValor,
                                iva = ivaCalculado,
                                total = totalCalculado,
                                metodoPago = metodoPago,
                                formaPago = "",
                                telefonoProveedor = telefonoProveedor.ifBlank { null },
                                correoProveedor = correoProveedor.ifBlank { null },
                                rfcProveedor = rfcProveedor.ifBlank { null },
                                observaciones = observaciones.ifBlank { null },
                                proyecto = proyecto.takeIf { it.isNotBlank() && it != "Sin proyecto" },
                                clienteId = clienteSeleccionadoId,
                                cotizacionId = cotizacionSeleccionadaId
                            )

                            viewModel.actualizarGasto(gastoEditado) {
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Actualizar")
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSelectorClienteGasto(
    label: String,
    clientes: List<ClienteEntity>,
    clienteSeleccionadoId: Int?,
    onClienteSeleccionado: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val clienteSeleccionado = clientes.firstOrNull {
        it.id == clienteSeleccionadoId
    }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = {
            expandido = !expandido
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = clienteSeleccionado?.nombre ?: "Sin cliente",
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = {
                Text(label)
            },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandido
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                expandido = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text("Sin cliente")
                },
                onClick = {
                    onClienteSeleccionado(null)
                    expandido = false
                }
            )

            clientes.forEach { cliente ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = cliente.nombre,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (cliente.empresa.isNotBlank()) {
                                Text(
                                    text = cliente.empresa,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    },
                    onClick = {
                        onClienteSeleccionado(cliente.id)
                        expandido = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSelectorCotizacionGasto(
    label: String,
    cotizaciones: List<CotizacionEntity>,
    cotizacionSeleccionadaId: Int?,
    onCotizacionSeleccionada: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val cotizacionSeleccionada = cotizaciones.firstOrNull {
        it.id == cotizacionSeleccionadaId
    }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = {
            expandido = !expandido
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = cotizacionSeleccionada?.folio ?: "Sin cotización",
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = {
                Text(label)
            },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandido
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                expandido = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text("Sin cotización")
                },
                onClick = {
                    onCotizacionSeleccionada(null)
                    expandido = false
                }
            )

            cotizaciones.forEach { cotizacion ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = cotizacion.folio,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = cotizacion.descripcionTrabajo,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                maxLines = 1
                            )

                            Text(
                                text = "$ ${"%.2f".format(cotizacion.total)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    },
                    onClick = {
                        onCotizacionSeleccionada(cotizacion.id)
                        expandido = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaEditar(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = convertirFechaAMillis(value)
    )

    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(label)
            },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        mostrarCalendario = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Seleccionar fecha"
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    mostrarCalendario = true
                }
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
                            onValueChange(formatearFecha(fechaSeleccionada))
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
fun formatearFecha(millis: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    formato.timeZone = TimeZone.getTimeZone("UTC")
    return formato.format(Date(millis))
}

fun convertirFechaAMillis(fecha: String): Long? {
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.timeZone = TimeZone.getTimeZone("UTC")
        formato.isLenient = false
        formato.parse(fecha)?.time
    } catch (e: Exception) {
        null
    }
}
@Composable
fun CampoTextoEditar(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = {
            Text(label)
        },
        readOnly = readOnly,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = leadingIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoSelectorEditar(
    label: String,
    value: String,
    opciones: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = {
            expandido = !expandido
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = {
                Text(label)
            },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandido
                )
            }
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                expandido = false
            }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(opcion)
                    },
                    onClick = {
                        onValueChange(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
fun ArchivoEditableFactura(
    nombre: String,
    peso: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = peso,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = "Ver archivo"
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar archivo",
                    tint = Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
fun SeccionAccionesRapidasGasto(
    onEditarClick: () -> Unit
) {
    TarjetaDetalleGasto(
        titulo = "Acciones rápidas",
        icono = Icons.Default.Build
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BotonAccionDetalleGasto(
                texto = "Editar",
                icono = Icons.Default.Edit,
                color = Color(0xFF2563EB),
                modifier = Modifier.weight(1f),
                onClick = onEditarClick
            )

            BotonAccionDetalleGasto(
                texto = "Duplicar",
                icono = Icons.Default.ContentCopy,
                color = Color(0xFF374151),
                modifier = Modifier.weight(1f),
                onClick = { }
            )

            BotonAccionDetalleGasto(
                texto = "Descargar PDF",
                icono = Icons.Default.Download,
                color = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f),
                onClick = { }
            )

            BotonAccionDetalleGasto(
                texto = "Eliminar",
                icono = Icons.Default.Delete,
                color = Color(0xFFDC2626),
                modifier = Modifier.weight(1f),
                onClick = { }
            )
        }
    }
}

@Composable
fun BotonAccionDetalleGasto(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}