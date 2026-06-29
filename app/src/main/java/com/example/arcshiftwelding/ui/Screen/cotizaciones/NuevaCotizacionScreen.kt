package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity
import com.example.arcshiftwelding.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCotizacionScreen(
    navController: NavController,
    viewModel: CotizacionesViewModel
) {
    val clientes by viewModel.clientesActivos.collectAsState(initial = emptyList())

    var clienteSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var errorCliente by remember { mutableStateOf(false) }
    var proyecto by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("19/05/2026") }
    var vigencia by remember { mutableStateOf("02/06/2026") }
    var folio by remember { mutableStateOf("COT-00025") }
    var descripcion by remember { mutableStateOf("") }

    var descuento by remember { mutableStateOf("0") }
    var iva by remember { mutableStateOf("16") }
    var anticipo by remember { mutableStateOf("50") }
    var observaciones by remember { mutableStateOf("") }



    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(
                start = 8.dp,
                top = 0.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            HeaderNuevaCotizacion(navController = navController)
        }

        item {
            SeccionInformacionGeneralNuevaCotizacion(
                clientes = clientes,
                clienteSeleccionadoId = clienteSeleccionadoId,
                onClienteSeleccionado = {
                    clienteSeleccionadoId = it
                    errorCliente = false
                },
                errorCliente = errorCliente,
                proyecto = proyecto,
                onProyectoChange = { proyecto = it },
                fecha = fecha,
                onFechaChange = { fecha = it },
                vigencia = vigencia,
                onVigenciaChange = { vigencia = it },
                folio = folio,
                onFolioChange = { folio = it },
                descripcion = descripcion,
                onDescripcionChange = { descripcion = it }
            )
        }

        item {
            SeccionConceptosNuevaCotizacion()
        }

        item {
            SeccionResumenNuevaCotizacion(
                descuento = descuento,
                onDescuentoChange = { descuento = it },
                iva = iva,
                onIvaChange = { iva = it },
                anticipo = anticipo,
                onAnticipoChange = { anticipo = it }
            )
        }

        item {
            SeccionArchivosNuevaCotizacion()
        }

        item {
            SeccionObservacionesNuevaCotizacion(
                observaciones = observaciones,
                onObservacionesChange = { observaciones = it }
            )
        }

        item {
            BotonesNuevaCotizacion(
                onCancelarClick = {
                    navController.popBackStack()
                },
                onGuardarClick = {
                    val clienteId = clienteSeleccionadoId

                    if (clienteId == null) {
                        errorCliente = true
                        return@BotonesNuevaCotizacion
                    }

                    val detallesCotizacion = listOf(
                        DetalleCotizacionEntity(
                            cotizacionId = 0,
                            descripcion = descripcion.ifBlank { "Trabajo cotizado" },
                            cantidad = 1.0,
                            precioUnitario = 8900.0,
                            total = 8900.0
                        )
                    )

                    val subtotalCalculado = detallesCotizacion.sumOf { it.total }
                    val ivaPorcentaje = iva.toDoubleOrNull() ?: 16.0
                    val ivaCalculado = subtotalCalculado * (ivaPorcentaje / 100.0)
                    val totalCalculado = subtotalCalculado + ivaCalculado

                    viewModel.guardarCotizacion(
                        folio = folio,
                        clienteId = clienteId,
                        descripcionTrabajo = descripcion.ifBlank { "Trabajo cotizado" },
                        subtotal = subtotalCalculado,
                        iva = ivaCalculado,
                        total = totalCalculado,
                        fecha = fecha,
                        estado = "Pendiente",
                        detalles = detallesCotizacion,
                        onFinish = {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun HeaderNuevaCotizacion(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
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
            text = "Nueva Cotización",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones"
            )
        }

        TextButton(
            onClick = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        ) {
            Text(
                text = "Log\nOut",
                fontSize = 9.sp,
                lineHeight = 10.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionInformacionGeneralNuevaCotizacion(
    clientes: List<ClienteEntity>,
    clienteSeleccionadoId: Int?,
    onClienteSeleccionado: (Int) -> Unit,
    errorCliente: Boolean,
    proyecto: String,
    onProyectoChange: (String) -> Unit,
    fecha: String,
    onFechaChange: (String) -> Unit,
    vigencia: String,
    onVigenciaChange: (String) -> Unit,
    folio: String,
    onFolioChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit
) {
    CardSeccionFormularioCotizacion(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectorClienteCotizacion(
                clientes = clientes,
                clienteSeleccionadoId = clienteSeleccionadoId,
                onClienteSeleccionado = onClienteSeleccionado,
                mostrarError = errorCliente,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    // Aquí puedes navegar a NuevoCliente si quieres
                    // navController.navigate(AppRoutes.NUEVO_CLIENTE)
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar cliente"
                )
            }
        }

        CampoFormularioCotizacion(
            titulo = "Proyecto (opcional)",
            valor = proyecto,
            placeholder = "Seleccionar proyecto",
            onValueChange = onProyectoChange,
            trailingIcon = Icons.Default.KeyboardArrowDown
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoFormularioCotizacion(
                titulo = "Fecha *",
                valor = fecha,
                placeholder = "Fecha",
                onValueChange = onFechaChange,
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.CalendarMonth
            )

            CampoFormularioCotizacion(
                titulo = "Vigencia *",
                valor = vigencia,
                placeholder = "Vigencia",
                onValueChange = onVigenciaChange,
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.CalendarMonth
            )
        }

        CampoFormularioCotizacion(
            titulo = "Folio / Número",
            valor = folio,
            placeholder = "COT-00025",
            onValueChange = onFolioChange
        )

        CampoTextoLargoCotizacion(
            titulo = "Descripción del trabajo *",
            valor = descripcion,
            placeholder = "Describe el trabajo o proyecto que se va a cotizar...",
            onValueChange = onDescripcionChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorClienteCotizacion(
    clientes: List<ClienteEntity>,
    clienteSeleccionadoId: Int?,
    onClienteSeleccionado: (Int) -> Unit,
    mostrarError: Boolean,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val clienteSeleccionado = clientes.firstOrNull { cliente ->
        cliente.id == clienteSeleccionadoId
    }

    Column(
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = "Cliente *",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = {
                expandido = !expandido
            }
        ) {
            OutlinedTextField(
                value = clienteSeleccionado?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = "Seleccionar cliente",
                        fontSize = 10.sp
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandido
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 10.sp
                ),
                shape = RoundedCornerShape(7.dp),
                isError = mostrarError
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = {
                    expandido = false
                }
            ) {
                if (clientes.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text("No hay clientes activos")
                        },
                        onClick = {
                            expandido = false
                        }
                    )
                } else {
                    clientes.forEach { cliente ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = cliente.nombre,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    if (cliente.empresa.isNotBlank()) {
                                        Text(
                                            text = cliente.empresa,
                                            fontSize = 10.sp,
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

        if (mostrarError) {
            Text(
                text = "Selecciona un cliente",
                fontSize = 9.sp,
                color = Color(0xFFDC2626),
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

@Composable
fun SeccionConceptosNuevaCotizacion() {
    CardSeccionFormularioCotizacion(
        titulo = "Conceptos",
        icono = Icons.Default.FormatListBulleted
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabConceptoCotizacion(
                texto = "Materiales",
                seleccionado = true,
                modifier = Modifier.weight(1f)
            )

            TabConceptoCotizacion(
                texto = "Mano de obra",
                seleccionado = false,
                modifier = Modifier.weight(1f)
            )

            TabConceptoCotizacion(
                texto = "Gastos adicionales",
                seleccionado = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        EncabezadoNuevaCotizacionConceptos()

        ConceptoNuevaCotizacionItem(
            concepto = "PTR 2x2 Cal. 14",
            cantidad = "12",
            unidad = "Pza",
            precio = "$450.00",
            importe = "$5,400.00"
        )

        ConceptoNuevaCotizacionItem(
            concepto = "Soldadura",
            cantidad = "1",
            unidad = "Servicio",
            precio = "$2,000.00",
            importe = "$2,000.00"
        )

        ConceptoNuevaCotizacionItem(
            concepto = "Pintura anticorrosiva",
            cantidad = "1",
            unidad = "Servicio",
            precio = "$1,500.00",
            importe = "$1,500.00"
        )

        TextButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF16A34A)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Agregar concepto",
                fontSize = 11.sp,
                color = Color(0xFF16A34A)
            )
        }
    }
}

@Composable
fun TabConceptoCotizacion(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .background(
                color = if (seleccionado) Color.White else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 9.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            color = if (seleccionado) Color(0xFF15803D) else Color.Gray,
            maxLines = 1
        )
    }
}


@Composable
fun EncabezadoNuevaCotizacionConceptos() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Concepto",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1.3f)
        )

        Text(
            text = "Cant.",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.5f)
        )

        Text(
            text = "Unidad",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.7f)
        )

        Text(
            text = "Precio",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.8f)
        )

        Text(
            text = "Importe",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.8f)
        )

        Spacer(modifier = Modifier.width(24.dp))
    }

    Divider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = Color(0xFFE2E8F0)
    )
}

@Composable
fun ConceptoNuevaCotizacionItem(
    concepto: String,
    cantidad: String,
    unidad: String,
    precio: String,
    importe: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = concepto,
            fontSize = 8.sp,
            color = Color.Black,
            maxLines = 1,
            modifier = Modifier.weight(1.3f)
        )

        CampoMiniCotizacion(
            texto = cantidad,
            modifier = Modifier.weight(0.5f)
        )

        CampoMiniCotizacion(
            texto = unidad,
            modifier = Modifier.weight(0.7f)
        )

        CampoMiniCotizacion(
            texto = precio,
            modifier = Modifier.weight(0.8f)
        )

        CampoMiniCotizacion(
            texto = importe,
            modifier = Modifier.weight(0.8f)
        )

        IconButton(
            onClick = { },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Eliminar",
                modifier = Modifier.size(15.dp),
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun CampoMiniCotizacion(
    texto: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(28.dp)
            .padding(horizontal = 2.dp)
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 8.sp,
            color = Color.Black,
            maxLines = 1
        )
    }
}

@Composable
fun SeccionResumenNuevaCotizacion(
    descuento: String,
    onDescuentoChange: (String) -> Unit,
    iva: String,
    onIvaChange: (String) -> Unit,
    anticipo: String,
    onAnticipoChange: (String) -> Unit
) {
    CardSeccionFormularioCotizacion(
        titulo = "Resumen de la cotización",
        icono = Icons.Default.ReceiptLong
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CampoFormularioCotizacion(
                    titulo = "Subtotal",
                    valor = "$8,900.00",
                    placeholder = "",
                    onValueChange = { }
                )

                CampoFormularioCotizacion(
                    titulo = "Anticipo requerido (%)",
                    valor = anticipo,
                    placeholder = "50",
                    onValueChange = onAnticipoChange
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                CampoFormularioCotizacion(
                    titulo = "Descuento (%)",
                    valor = descuento,
                    placeholder = "0",
                    onValueChange = onDescuentoChange
                )

                CampoFormularioCotizacion(
                    titulo = "IVA (%)",
                    valor = iva,
                    placeholder = "16",
                    onValueChange = onIvaChange
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Total",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Text(
                    text = "$10,324.00",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFEAF7EE),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Anticipo sugerido",
                            fontSize = 8.sp,
                            color = Color(0xFF15803D)
                        )

                        Text(
                            text = "$5,162.00",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Saldo restante",
                    fontSize = 8.sp,
                    color = Color.Gray
                )

                Text(
                    text = "$5,162.00",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun SeccionArchivosNuevaCotizacion() {
    CardSeccionFormularioCotizacion(
        titulo = "Archivos adjuntos",
        icono = Icons.Default.AttachFile
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BotonArchivoNuevaCotizacion(
                texto = "Agregar imagen",
                icono = Icons.Default.Image,
                modifier = Modifier.weight(1f)
            )

            BotonArchivoNuevaCotizacion(
                texto = "Adjuntar archivo",
                icono = Icons.Default.AttachFile,
                subtitulo = "Máx. 10 MB",
                modifier = Modifier.weight(1f)
            )

            BotonArchivoNuevaCotizacion(
                texto = "Subir PDF",
                icono = Icons.Default.Description,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BotonArchivoNuevaCotizacion(
    texto: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    subtitulo: String? = null
) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF334155)
            )

            Text(
                text = texto,
                fontSize = 8.sp,
                color = Color.Black,
                maxLines = 1
            )

            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    fontSize = 7.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SeccionObservacionesNuevaCotizacion(
    observaciones: String,
    onObservacionesChange: (String) -> Unit
) {
    CardSeccionFormularioCotizacion(
        titulo = "Observaciones",
        icono = Icons.Default.Search
    ) {
        CampoTextoLargoCotizacion(
            titulo = "Observaciones opcional",
            valor = observaciones,
            placeholder = "Agrega notas u observaciones adicionales...",
            onValueChange = onObservacionesChange
        )
    }
}


@Composable
fun BotonesNuevaCotizacion(
    onCancelarClick: () -> Unit,
    onGuardarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCancelarClick,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Cancelar",
                fontSize = 12.sp
            )
        }

        Button(
            onClick = onGuardarClick,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF15803D)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Guardar Cotización",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CardSeccionFormularioCotizacion(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            contenido()
        }
    }
}

@Composable
fun CampoFormularioCotizacion(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    Column(
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 10.sp
                )
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
            trailingIcon = if (trailingIcon != null) {
                {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp
            ),
            shape = RoundedCornerShape(7.dp)
        )
    }
}

@Composable
fun CampoTextoLargoCotizacion(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 10.sp
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp
            ),
            shape = RoundedCornerShape(7.dp),
            maxLines = 3
        )
    }
}