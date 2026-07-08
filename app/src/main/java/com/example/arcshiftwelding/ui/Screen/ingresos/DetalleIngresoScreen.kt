package com.example.arcshiftwelding.ui.Screen.ingresos

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.arcshiftwelding.navigation.AppRoutes

import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleIngresoScreen(
    navController: NavController,
    ingresoId: Int,
    viewModel: IngresosViewModel
) {
    val ingreso by viewModel.obtenerIngreso(ingresoId).collectAsState(initial = null)

    if (ingreso == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Ingreso no encontrado")
        }
        return
    }

    val ingresoActual = ingreso!!

    val pagosProgramados by viewModel
        .obtenerPagosDetallePorIngreso(ingresoActual.id)
        .collectAsState(initial = emptyList())

    val resumenCobro = viewModel.calcularResumenCobroIngreso(
        ingreso = ingresoActual,
        pagos = pagosProgramados
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
                    text = "Detalle de Ingreso",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        navController.navigate(AppRoutes.editarIngreso(ingresoActual.id))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Ingreso"
                    )
                }
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaPrincipalIngreso(
                ingreso = ingresoActual,
                resumen = resumenCobro
            )

            SeccionDetalleInformacionGeneralIngreso(
                ingreso = ingresoActual,
                resumen = resumenCobro
            )

            SeccionDetalleInformacionFinancieraIngreso(
                ingreso = ingresoActual,
                resumen = resumenCobro
            )

            SeccionDetalleComprobanteIngreso(ingresoActual)

            SeccionPlanPagosIngreso(
                pagos = pagosProgramados,
                resumen = resumenCobro,
                onMarcarPagado = { pago, fechaPago, montoPagado, metodoPago, comprobanteUri, tipoComprobante ->
                    viewModel.marcarPagoProgramadoComoPagado(
                        pagoId = pago.id,
                        fechaPago = fechaPago,
                        montoPagado = montoPagado,
                        metodoPago = metodoPago,
                        comprobanteUri = comprobanteUri,
                        tipoComprobante = tipoComprobante
                    ) {
                        // Se actualiza solo por Flow
                    }
                }
            )
            SeccionDetalleObservacionesIngreso(ingresoActual)

            SeccionDetalleRelacionadoIngreso(ingresoActual)

            SeccionAccionesRapidasIngreso(
                onEditar = {
                    navController.navigate(AppRoutes.editarIngreso(ingresoActual.id))
                },
                onEnviarFactura = {},
                onDescargarPDF = {},
                onEliminar = {
                    navController.navigate(AppRoutes.eliminarIngreso(ingresoActual.id))
                }
            )
        }
    }
}


@Composable
fun TarjetaPrincipalIngreso(
    ingreso: IngresoUI,
    resumen: ResumenCobroIngresoUI
) {
    val estaPagado = resumen.estadoCobro == "Pagado"

    val textoPrincipal = if (estaPagado) {
        resumen.totalRecibido.formatoDinero()
    } else {
        ingreso.total
    }

    val etiqueta = if (estaPagado) {
        "Pagos"
    } else {
        ingreso.categoria
    }

    val colorEstado = if (estaPagado) {
        Color(0xFF16A34A)
    } else if (ingreso.formaPago == "Anticipo") {
        Color(0xFFF59E0B)
    } else {
        Color(0xFF16A34A)
    }

    val fondoEstado = if (estaPagado) {
        Color(0xFFEAF7EE)
    } else if (ingreso.formaPago == "Anticipo") {
        Color(0xFFFFF7E6)
    } else {
        Color(0xFFEAF7EE)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(fondoEstado, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (estaPagado) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.AttachMoney
                    },
                    contentDescription = null,
                    tint = colorEstado,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ingreso.concepto.ifBlank { ingreso.trabajo },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = textoPrincipal,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorEstado
                )

                Text(
                    text = "Cliente: ${ingreso.cliente}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DatoIconoPequenoIngreso(
                        icono = Icons.Default.DateRange,
                        texto = ingreso.fecha
                    )

                    DatoIconoPequenoIngreso(
                        icono = Icons.Default.Payment,
                        texto = ingreso.metodoPago.ifBlank { "Sin método" }
                    )
                }
            }

            AssistChip(
                onClick = { },
                label = {
                    Text(etiqueta)
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = fondoEstado,
                    labelColor = colorEstado
                )
            )
        }
    }
}


@Composable
fun SeccionDetalleInformacionGeneralIngreso(
    ingreso: IngresoUI,
    resumen: ResumenCobroIngresoUI
) {
    TarjetaDetalleIngreso(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        ItemDatoDetalleIngreso(
            titulo = "Concepto",
            valor = ingreso.concepto
        )

        Spacer(modifier = Modifier.height(10.dp))

        ItemDatoDetalleIngreso(
            titulo = "Trabajo / Proyecto",
            valor = ingreso.trabajo.ifBlank { ingreso.proyecto }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ItemDatoDetalleIngreso(
            titulo = "Folio / Referencia",
            valor = ingreso.folio.ifBlank { "Sin folio" }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ItemDatoDetalleIngreso(
            titulo = "Fecha de ingreso inicial",
            valor = ingreso.fecha
        )

        Spacer(modifier = Modifier.height(10.dp))

        ItemDatoDetalleIngreso(
            titulo = "Método de pago inicial",
            valor = ingreso.metodoPago.ifBlank { "Sin método" }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ItemDatoDetalleIngreso(
            titulo = "Tipo de ingreso inicial",
            valor = ingreso.formaPago
        )

        Spacer(modifier = Modifier.height(10.dp))

        ItemDatoDetalleIngreso(
            titulo = "Estado de cobro",
            valor = resumen.estadoCobro
        )
    }
}

@Composable
fun SeccionDetalleClienteIngreso() {
    TarjetaDetalleIngreso(
        titulo = "Cliente",
        icono = Icons.Default.Person
    ) {
        ItemDatoDetalleIngreso(
            titulo = "Constructora del Bajío S.A. de C.V.",
            valor = ""
        )

        Spacer(modifier = Modifier.height(8.dp))

        ItemDatoDetalleIngreso(
            titulo = "Contacto:",
            valor = "Ing. Juan Pérez"
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoDetalleIngreso(
            titulo = "Teléfono:",
            valor = "477 123 4567"
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoDetalleIngreso(
            titulo = "Correo:",
            valor = "jperez@cbajio.com"
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoDetalleIngreso(
            titulo = "RFC:",
            valor = "CDB2105314K7"
        )
    }
}

@Composable
fun SeccionDetalleInformacionFinancieraIngreso(
    ingreso: IngresoUI,
    resumen: ResumenCobroIngresoUI
) {
    val esProyecto = ingreso.proyectoId != null || ingreso.montoTotalProyectoNumero > 0.0
    val estaPagado = resumen.estadoCobro == "Pagado"

    TarjetaDetalleIngreso(
        titulo = "Información financiera",
        icono = Icons.Default.AttachMoney
    ) {
        if (esProyecto) {
            FilaMontoDetalleIngreso(
                titulo = "Monto total del proyecto",
                valor = resumen.totalProyecto.formatoDinero()
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilaMontoDetalleIngreso(
                titulo = "Anticipo inicial recibido",
                valor = resumen.anticipoInicial.formatoDinero()
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilaMontoDetalleIngreso(
                titulo = "Pagos posteriores recibidos",
                valor = resumen.pagosPagados.formatoDinero()
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilaMontoDetalleIngreso(
                titulo = "Total recibido",
                valor = resumen.totalRecibido.formatoDinero(),
                destacar = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilaMontoDetalleIngreso(
                titulo = "Pendiente por cobrar",
                valor = resumen.pendiente.formatoDinero()
            )

            Divider(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            FilaMontoDetalleIngreso(
                titulo = "Estado",
                valor = if (estaPagado) "Pagado completamente" else "Pago parcial",
                destacar = estaPagado
            )
        } else {
            FilaMontoDetalleIngreso(
                titulo = "Subtotal",
                valor = ingreso.subtotal
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilaMontoDetalleIngreso(
                titulo = "IVA",
                valor = ingreso.iva
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilaMontoDetalleIngreso(
                titulo = "Total recibido",
                valor = ingreso.total,
                destacar = true
            )
        }
    }
}

@Composable
fun SeccionPlanPagosIngreso(
    pagos: List<PagoProgramadoDetalleUI>,
    resumen: ResumenCobroIngresoUI,
    onMarcarPagado: (
        PagoProgramadoDetalleUI,
        String,
        Double,
        String,
        String,
        String
    ) -> Unit
) {
    var pagoSeleccionado by remember {
        mutableStateOf<PagoProgramadoDetalleUI?>(null)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Plan de pagos",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (resumen.estadoCobro == "Pagado") {
                        Color(0xFFEAF7EE)
                    } else {
                        Color(0xFFFFF7E6)
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilaResumenPlanPago(
                        titulo = "Total proyecto",
                        valor = resumen.totalProyecto.formatoDinero()
                    )

                    FilaResumenPlanPago(
                        titulo = "Anticipo inicial",
                        valor = resumen.anticipoInicial.formatoDinero()
                    )

                    FilaResumenPlanPago(
                        titulo = "Pagos recibidos",
                        valor = resumen.pagosPagados.formatoDinero()
                    )

                    FilaResumenPlanPago(
                        titulo = "Total recibido",
                        valor = resumen.totalRecibido.formatoDinero()
                    )

                    FilaResumenPlanPago(
                        titulo = "Pendiente",
                        valor = resumen.pendiente.formatoDinero()
                    )

                    FilaResumenPlanPago(
                        titulo = "Estado",
                        valor = resumen.estadoCobro
                    )
                }
            }

            if (pagos.isEmpty()) {
                Text(
                    text = "No hay pagos programados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                pagos.forEach { pago ->
                    ItemPagoProgramadoDetalle(
                        pago = pago,
                        onMarcarPagado = {
                            pagoSeleccionado = pago
                        }
                    )
                }
            }
        }
    }

    pagoSeleccionado?.let { pago ->
        DialogMarcarPagoProgramado(
            pago = pago,
            onDismiss = {
                pagoSeleccionado = null
            },
            onConfirmar = { fechaPago, montoPagado, metodoPago, comprobanteUri, tipoComprobante ->
                onMarcarPagado(
                    pago,
                    fechaPago,
                    montoPagado,
                    metodoPago,
                    comprobanteUri,
                    tipoComprobante
                )

                pagoSeleccionado = null
            }
        )
    }
}

@Composable
fun SeccionDetalleComprobanteIngreso(
    ingreso: IngresoUI
) {
    TarjetaDetalleIngreso(
        titulo = "Comprobante",
        icono = Icons.Default.AttachFile
    ) {
        if (ingreso.comprobanteUri.isBlank()) {
            Text(
                text = "Sin comprobante registrado.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (ingreso.tipoComprobante == "PDF") {
                            Icons.Default.PictureAsPdf
                        } else {
                            Icons.Default.Image
                        },
                        contentDescription = null,
                        tint = if (ingreso.tipoComprobante == "PDF") {
                            Color(0xFFDC2626)
                        } else {
                            Color(0xFF2563EB)
                        },
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = ingreso.tipoComprobante.ifBlank { "Comprobante" },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = ingreso.folio.ifBlank { "Archivo registrado" },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "Registrado",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FilaResumenPlanPago(
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
    }
}

@Composable
fun SeccionDetalleObservacionesIngreso(
    ingreso: IngresoUI
) {
    TarjetaDetalleIngreso(
        titulo = "Observaciones",
        icono = Icons.Default.Edit
    ) {
        Text(
            text = ingreso.observaciones.ifBlank { "Sin observaciones registradas." },
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )
    }
}

@Composable
fun ItemPagoProgramadoDetalle(
    pago: PagoProgramadoDetalleUI,
    onMarcarPagado: () -> Unit
) {
    val pagado = pago.estado == "Pagado"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (pagado) {
                Color(0xFFF0FDF4)
            } else {
                Color(0xFFF8FAFC)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (pagado) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Schedule
                    },
                    contentDescription = null,
                    tint = if (pagado) {
                        Color(0xFF16A34A)
                    } else {
                        Color(0xFFF59E0B)
                    },
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (pagado) "Pago realizado" else "Pago pendiente",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Programado: ${pago.fechaProgramada}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Text(
                    text = pago.montoProgramado,
                    fontWeight = FontWeight.Bold,
                    color = if (pagado) {
                        Color(0xFF16A34A)
                    } else {
                        Color(0xFFF59E0B)
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (pagado) {
                Text(
                    text = "Pagado el ${pago.fechaPago} · ${pago.metodoPago}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )

                if (pago.comprobanteUri.isNotBlank()) {
                    Text(
                        text = "Comprobante registrado",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onMarcarPagado,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Marcar como pagado")
                }
            }
        }
    }
}

@Composable
fun DialogMarcarPagoProgramado(
    pago: PagoProgramadoDetalleUI,
    onDismiss: () -> Unit,
    onConfirmar: (
        String,
        Double,
        String,
        String,
        String
    ) -> Unit
) {
    var fechaPago by remember {
        mutableStateOf(fechaActual())
    }

    var montoPagado by remember {
        mutableStateOf(pago.montoProgramadoNumero.sinDecimalesSiAplica())
    }

    var metodoPago by remember {
        mutableStateOf("")
    }

    var comprobanteUri by remember {
        mutableStateOf("")
    }

    var tipoComprobante by remember {
        mutableStateOf("")
    }

    val seleccionarArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            comprobanteUri = uri.toString()
            tipoComprobante = if (
                uri.toString().contains(".pdf", ignoreCase = true)
            ) {
                "PDF"
            } else {
                "Imagen"
            }
        }
    }

    val metodosPago = listOf(
        "Efectivo",
        "Tarjeta",
        "Transferencia",
        "Cheque",
        "Crédito"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Marcar pago como pagado")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Monto programado: ${pago.montoProgramado}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                CampoFechaIngreso(
                    titulo = "Fecha de pago",
                    valor = fechaPago,
                    onFechaSeleccionada = {
                        fechaPago = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                CampoTextoIngreso(
                    titulo = "Monto pagado",
                    valor = montoPagado,
                    placeholder = "$ 0.00",
                    onValueChange = {
                        montoPagado = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                CampoDropdownIngreso(
                    titulo = "Método de pago",
                    valor = metodoPago,
                    opciones = metodosPago,
                    placeholder = "Selecciona método",
                    onValueChange = {
                        metodoPago = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            seleccionarArchivo.launch("application/pdf")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("PDF")
                    }

                    OutlinedButton(
                        onClick = {
                            seleccionarArchivo.launch("image/*")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Foto")
                    }
                }

                if (comprobanteUri.isNotBlank()) {
                    Text(
                        text = "Comprobante seleccionado: $tipoComprobante",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmar(
                        fechaPago,
                        montoPagado.aDouble(),
                        metodoPago,
                        comprobanteUri,
                        tipoComprobante
                    )
                }
            ) {
                Text("Guardar pago")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun SeccionDetalleRelacionadoIngreso(
    ingreso: IngresoUI
) {
    TarjetaDetalleIngreso(
        titulo = "Relacionado con",
        icono = Icons.Default.Link
    ) {
        ItemDatoConLinkIngreso(
            titulo = "Cotización:",
            valor = ingreso.cotizacion.ifBlank { "Sin cotización" }
        )

      /*  Spacer(modifier = Modifier.height(6.dp))

        ItemDatoConLinkIngreso(
            titulo = "Orden de trabajo:",
            valor = ingreso.ordenTrabajo.ifBlank { "Sin orden" }
        )*/

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoConLinkIngreso(
            titulo = "Proyecto:",
            valor = ingreso.proyecto.ifBlank { "Sin proyecto" }
        )
    }
}

@Composable
fun SeccionAccionesRapidasIngreso(
    onEditar: () -> Unit,
    onEnviarFactura: () -> Unit,
    onDescargarPDF: () -> Unit,
    onEliminar: () -> Unit
) {
    TarjetaDetalleIngreso(
        titulo = "Acciones rápidas",
        icono = Icons.Default.Build
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BotonAccionRapida(
                texto = "Editar",
                icono = Icons.Default.Edit,
                onClick = onEditar,
                modifier = Modifier.weight(1f)
            )

            BotonAccionRapida(
                texto = "Enviar",
                icono = Icons.Default.Send,
                onClick = onEnviarFactura,
                modifier = Modifier.weight(1f),
                iconTint = Color(0xFF2563EB)
            )

            BotonAccionRapida(
                texto = "PDF",
                icono = Icons.Default.AddCircleOutline,
                onClick = onDescargarPDF,
                modifier = Modifier.weight(1f),
                iconTint = Color(0xFF1B7F3A)
            )

            BotonAccionRapida(
                texto = "Eliminar",
                icono = Icons.Default.RemoveCircleOutline,
                onClick = onEliminar,
                modifier = Modifier.weight(1f),
                iconTint = Color(0xFFB42318)
            )
        }
    }
}

@Composable
fun TarjetaDetalleIngreso(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            contenido()
        }
    }
}

@Composable
fun ItemDatoDetalleIngreso(
    titulo: String,
    valor: String
) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        if (valor.isNotEmpty()) {
            Text(
                text = valor,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}

@Composable
fun DatoIconoPequenoIngreso(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.DarkGray
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray
        )
    }
}

@Composable
fun FilaMontoDetalleIngreso(
    titulo: String,
    valor: String,
    destacar: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (destacar) Color(0xFF2E7D32) else Color.Black
        )
    }
}

@Composable
fun ItemDatoConLinkIngreso(
    titulo: String,
    valor: String
) {
    Row {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ArchivoComprobanteIngresoCard(
    nombre: String,
    peso: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(95.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                tint = Color(0xFFE53935)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Text(
                    text = "PDF · $peso",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.RemoveRedEye,
                contentDescription = "Ver archivo",
                modifier = Modifier.size(18.dp),
                tint = Color.DarkGray
            )
        }
    }
}
@Composable
fun BotonAccionRapida(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color(0xFF2563EB)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = iconTint
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = iconTint,
                maxLines = 1
            )
        }
    }
}