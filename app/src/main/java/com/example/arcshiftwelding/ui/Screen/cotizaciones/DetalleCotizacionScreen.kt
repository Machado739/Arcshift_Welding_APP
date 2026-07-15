package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity
import com.example.arcshiftwelding.data.local.relation.CotizacionCompleta
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.Screen.clientes.TituloSeccionCliente
import com.example.arcshiftwelding.utils.ComprobanteArchivoSeleccionado
import com.example.arcshiftwelding.utils.abrirComprobante
import com.example.arcshiftwelding.utils.deserializarComprobantes
import com.example.arcshiftwelding.utils.formatearTamanoComprobante
import com.example.arcshiftwelding.ui.components.DialogoExportarArchivo
import com.example.arcshiftwelding.utils.compartirPdfCotizacion
import com.example.arcshiftwelding.utils.generarPdfCotizacion
import com.example.arcshiftwelding.utils.obtenerTipoRealComprobante
import android.widget.Toast
import java.io.File
import com.example.arcshiftwelding.ui.theme.arcshiftColors
data class CotizacionDetalleUI(
    val id: Int,
    val folio: String,
    val cliente: String,
    val contacto: String,
    val telefono: String,
    val correo: String,
    val trabajo: String,
    val descripcion: String,
    val proyecto: String,
    val registradoPor: String,
    val fecha: String,
    val vigencia: String,
    val estado: String,
    val fechaAprobacion: String,
    val fechaActualizacion: String,
    val subtotal: String,
    val iva: String,
    val total: String,
    val anticipo: String,
    val saldo: String,
    val observaciones: String,
    val descuento: String,
    val ivaPorcentaje: String,
    val anticipoPorcentaje: String
)

@Composable
fun DetalleCotizacionScreen(
    navController: NavController,
    cotizacionId: Int,
    viewModel: CotizacionesViewModel
) {
    val cotizacionCompleta by viewModel
        .obtenerCotizacionCompleta(cotizacionId)
        .collectAsState(initial = null)

    if (cotizacionCompleta == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("Cotización no encontrada")
        }
        return
    }

    val context = LocalContext.current
    val cotizacionActual = cotizacionCompleta!!
    val cotizacionUi = cotizacionActual.toDetalleUi()
    val detalles = cotizacionActual.detalles
    val archivosAdjuntos = remember(cotizacionActual.cotizacion.archivosAdjuntosJson) {
        deserializarComprobantes(
            cotizacionActual.cotizacion.archivosAdjuntosJson
        )
    }

    var mostrarDialogoCrearProyecto by remember { mutableStateOf(false) }
    var pdfPendiente by remember { mutableStateOf<File?>(null) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
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
                    text = "Detalle de Cotización",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        navController.navigate(AppRoutes.editarCotizacion(cotizacionId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar cotizacion"
                    )
                }

            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            CardPrincipalDetalleCotizacion(cotizacion = cotizacionUi)

            SeccionInformacionGeneralCotizacion(cotizacion = cotizacionUi)

            SeccionClienteDetalleCotizacion(cotizacion = cotizacionUi)

            SeccionResumenFinancieroCotizacion(cotizacion = cotizacionUi)

            SeccionEstadoCotizacion(cotizacion = cotizacionUi)

            SeccionConceptosCotizados(detalles = detalles)

            SeccionArchivosCotizacion(
                archivos = archivosAdjuntos
            )

            SeccionObservacionesCotizacion(cotizacion = cotizacionUi)

            SeccionAccionesRapidasCotizacion(
                estado = cotizacionUi.estado,
                onEditarClick = {
                    navController.navigate(AppRoutes.editarCotizacion(cotizacionId))
                },
                onAprobarClick = {
                    viewModel.aprobarCotizacion(cotizacionId) {
                        mostrarDialogoCrearProyecto = true
                    }
                },
                onRechazarClick = {
                    viewModel.rechazarCotizacion(cotizacionId)
                },
                onGenerarPdfClick = {
                    generarPdfCotizacion(
                        context = context,
                        cotizacionCompleta = cotizacionActual
                    ).onSuccess { archivo ->
                        pdfPendiente = archivo
                    }.onFailure { error ->
                        Toast.makeText(
                            context,
                            error.message ?: "No fue posible generar el PDF.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
            )

        }
    }
    if (mostrarDialogoCrearProyecto) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoCrearProyecto = false
            },
            title = {
                Text(
                    text = "Cotización aprobada",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Deseas crear un proyecto a partir de esta cotización? Se cargarán automáticamente los datos disponibles y podrás completar la información faltante."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCrearProyecto = false
                        navController.navigate(
                            AppRoutes.nuevoProyectoDesdeCotizacion(cotizacionId)
                        )
                    }
                ) {
                    Text("Crear proyecto")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCrearProyecto = false
                    }
                ) {
                    Text("No, solo aprobar")
                }
            }
        )
    }

    pdfPendiente?.let { archivo ->
        DialogoExportarArchivo(
            archivo = archivo,
            mimeType = "application/pdf",
            titulo = "Cotización generada",
            onDismiss = { pdfPendiente = null },
            onCompartir = {
                compartirPdfCotizacion(
                    context = context,
                    archivoPdf = archivo,
                    folio = cotizacionActual.cotizacion.folio
                ).onFailure { error ->
                    Toast.makeText(
                        context,
                        error.message ?: "No fue posible compartir el PDF.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}

private fun CotizacionCompleta.toDetalleUi(): CotizacionDetalleUI {
    val cotizacionActual = cotizacion
    val clienteActual = cliente



    return CotizacionDetalleUI(
        id = cotizacionActual.id,
        folio = cotizacionActual.folio,
        cliente = clienteActual?.nombre ?: "Cliente no encontrado",
        contacto = clienteActual?.personaContacto ?: "",
        telefono = clienteActual?.telefono ?: "",
        correo = clienteActual?.correo ?: "",
        trabajo = cotizacionActual.descripcionTrabajo,
        descripcion = cotizacionActual.descripcionTrabajo,
        proyecto = cotizacionActual.proyecto.ifBlank { "Sin proyecto" },
        registradoPor = "Administrador",
        fecha = cotizacionActual.fecha,
        vigencia = cotizacionActual.vigencia.ifBlank { "Sin vigencia" },
        estado = cotizacionActual.estado,
        fechaAprobacion = cotizacionActual.fechaAprobacion,
        fechaActualizacion = cotizacionActual.fechaActualizacion.ifBlank {
            cotizacionActual.fecha
        },

        subtotal = cotizacionActual.subtotal.formatoMoneda(),
        descuento = cotizacionActual.descuento.formatoMoneda(),
        ivaPorcentaje = cotizacionActual.ivaPorcentaje.formatoNumeroCotizacion(),
        iva = cotizacionActual.iva.formatoMoneda(),
        total = cotizacionActual.total.formatoMoneda(),
        anticipoPorcentaje = cotizacionActual.anticipoPorcentaje.formatoNumeroCotizacion(),
        anticipo = cotizacionActual.anticipo.formatoMoneda(),
        saldo = cotizacionActual.saldo.formatoMoneda(),

        observaciones = cotizacionActual.observaciones.ifBlank {
            "Sin observaciones registradas."
        }
    )
}

@Composable
fun CardPrincipalDetalleCotizacion(
    cotizacion: CotizacionDetalleUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = MaterialTheme.arcshiftColors.successContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.arcshiftColors.success,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cotizacion.folio,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.arcshiftColors.success
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = cotizacion.estado,
                        fontSize = 8.sp,
                        color = colorEstadoCotizacion(cotizacion.estado),
                        modifier = Modifier
                            .background(
                                color = fondoEstadoCotizacion(cotizacion.estado),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = cotizacion.trabajo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cotizacion.total,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.arcshiftColors.success
                )

                Text(
                    text = "Cliente: ${cotizacion.cliente}",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Row {
                    Text(
                        text = "Creada: ${cotizacion.fecha}",
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Vigencia: ${cotizacion.vigencia}",
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SeccionInformacionGeneralCotizacion(
    cotizacion: CotizacionDetalleUI
) {
    CardSeccionCotizacion(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        TextoDetalleCotizacion("Descripción del trabajo", cotizacion.descripcion)
        TextoDetalleCotizacion("Proyecto", cotizacion.proyecto)
        TextoDetalleCotizacion("Registrado por", cotizacion.registradoPor)
    }
}

@Composable
fun SeccionClienteDetalleCotizacion(
    cotizacion: CotizacionDetalleUI
) {
    CardSeccionCotizacion(
        titulo = "Cliente",
        icono = Icons.Default.Person
    ) {
        TextoDetalleCotizacion("Cliente", cotizacion.cliente)
        TextoDetalleCotizacion("Contacto", cotizacion.contacto)
        TextoDetalleCotizacion("Teléfono", cotizacion.telefono)
        TextoDetalleCotizacion("Correo", cotizacion.correo)
    }
}

@Composable
fun SeccionResumenFinancieroCotizacion(
    cotizacion: CotizacionDetalleUI
) {
    CardSeccionCotizacion(
        titulo = "Resumen financiero",
        icono = Icons.Default.AttachMoney
    ) {
        FilaMontoCotizacion("Subtotal", cotizacion.subtotal, MaterialTheme.colorScheme.onSurface)

        if (cotizacion.descuento != "$0.00" && cotizacion.descuento != "$ 0.00") {
            FilaMontoCotizacion("Descuento", cotizacion.descuento, MaterialTheme.colorScheme.error)
        }

        FilaMontoCotizacion(
            "IVA (${cotizacion.ivaPorcentaje}%)",
            cotizacion.iva,
            MaterialTheme.colorScheme.onSurface
        )

        Divider(
            modifier = Modifier.padding(vertical = 6.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        FilaMontoCotizacion("Total", cotizacion.total, MaterialTheme.arcshiftColors.success, true)

        FilaMontoCotizacion(
            "Anticipo requerido (${cotizacion.anticipoPorcentaje}%)",
            cotizacion.anticipo,
            MaterialTheme.colorScheme.onSurface
        )

        FilaMontoCotizacion("Saldo restante", cotizacion.saldo, MaterialTheme.colorScheme.onSurface)
    }
}
@Composable
fun SeccionEstadoCotizacion(
    cotizacion: CotizacionDetalleUI
) {
    CardSeccionCotizacion(
        titulo = "Estado de la cotización",
        icono = Icons.Default.Flag
    ) {
        Text(
            text = "Estado actual",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = cotizacion.estado,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = colorEstadoCotizacion(cotizacion.estado),
            modifier = Modifier
                .background(
                    color = fondoEstadoCotizacion(cotizacion.estado),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextoDetalleCotizacion(
            titulo = "Situación",
            valor = mensajeEstadoCotizacion(cotizacion.estado)
        )

        if (
            cotizacion.estado.equals("Aprobada", ignoreCase = true) &&
            cotizacion.fechaAprobacion.isNotBlank()
        ) {
            TextoDetalleCotizacion(
                titulo = "Fecha de aprobación",
                valor = cotizacion.fechaAprobacion
            )
        }

        TextoDetalleCotizacion(
            titulo = "Última actualización",
            valor = cotizacion.fechaActualizacion
        )
    }
}

@Composable

private fun colorEstadoCotizacion(estado: String): Color {
    return when {
        estado.equals("Aprobada", ignoreCase = true) -> MaterialTheme.arcshiftColors.success
        estado.equals("Rechazada", ignoreCase = true) -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.arcshiftColors.warning
    }
}

@Composable

private fun fondoEstadoCotizacion(estado: String): Color {
    return when {
        estado.equals("Aprobada", ignoreCase = true) -> MaterialTheme.arcshiftColors.successContainer
        estado.equals("Rechazada", ignoreCase = true) -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.arcshiftColors.warningContainer
    }
}

private fun mensajeEstadoCotizacion(estado: String): String {
    return when {
        estado.equals("Aprobada", ignoreCase = true) ->
            "Aprobada por el cliente."

        estado.equals("Rechazada", ignoreCase = true) ->
            "Rechazada por el cliente."

        else ->
            "En espera de respuesta del cliente."
    }
}

@Composable
fun SeccionConceptosCotizados(
    detalles: List<DetalleCotizacionEntity>
) {
    CardSeccionCotizacion(
        titulo = "Conceptos cotizados",
        icono = Icons.Default.FormatListBulleted
    ) {
        EncabezadoConceptoCotizacion()

        detalles.forEach { detalle ->
            ConceptoCotizacionItem(
                concepto = detalle.descripcion,
                cantidad = detalle.cantidad.toString(),
                unidad = detalle.unidad,
                precio = detalle.precioUnitario.formatoMoneda(),
                importe = detalle.total.formatoMoneda()
            )
        }

        Divider(
            modifier = Modifier.padding(vertical = 6.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        FilaMontoCotizacion(
            titulo = "Subtotal de conceptos",
            valor = detalles.sumOf { it.total }.formatoMoneda(),
            color = MaterialTheme.arcshiftColors.success,
            negrita = true
        )
    }
}

@Composable
fun EncabezadoConceptoCotizacion() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Concepto", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1.4f))
        Text("Cant.", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.5f))
        Text("Unidad", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.6f))
        Text("Importe", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.8f))
    }

    Divider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
fun ConceptoCotizacionItem(
    concepto: String,
    cantidad: String,
    unidad: String,
    precio: String,
    importe: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1.4f)
        ) {
            Text(
                text = concepto,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Text(
                text = precio,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Text(
            text = cantidad,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.5f)
        )

        Text(
            text = unidad,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.6f)
        )

        Text(
            text = importe,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.8f)
        )
    }

    Spacer(modifier = Modifier.height(5.dp))
}

@Composable
fun SeccionArchivosCotizacion(
    archivos: List<ComprobanteArchivoSeleccionado>
) {
    val context = LocalContext.current

    CardSeccionCotizacion(
        titulo = "Archivos adjuntos (${archivos.size})",
        icono = Icons.Default.AttachFile
    ) {
        if (archivos.isEmpty()) {
            Text(
                text = "No se adjuntaron archivos a esta cotización.",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            archivos.forEachIndexed { index, archivo ->
                ArchivoCotizacionItem(
                    archivo = archivo,
                    onAbrirClick = {
                        if (!abrirComprobante(context, archivo)) {
                            Toast.makeText(
                                context,
                                "No se encontró una aplicación para abrir el archivo.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )

                if (index < archivos.lastIndex) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
fun ArchivoCotizacionItem(
    archivo: ComprobanteArchivoSeleccionado,
    onAbrirClick: () -> Unit
) {
    val context = LocalContext.current
    val tipoReal = obtenerTipoRealComprobante(context, archivo)

    val icono = when (tipoReal) {
        "PDF" -> Icons.Default.PictureAsPdf
        "Imagen" -> Icons.Default.Image
        else -> Icons.Default.InsertDriveFile
    }

    val color = when (tipoReal) {
        "PDF" -> MaterialTheme.colorScheme.error
        "Imagen" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val fondo = when (tipoReal) {
        "PDF" -> MaterialTheme.colorScheme.errorContainer
        "Imagen" -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onAbrirClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(fondo, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = tipoReal,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = archivo.nombre,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )

            Text(
                text = "$tipoReal · ${formatearTamanoComprobante(archivo.tamanoBytes)}",
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = "Ver archivo",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun SeccionObservacionesCotizacion(
    cotizacion: CotizacionDetalleUI
) {
    CardSeccionCotizacion(
        titulo = "Observaciones",
        icono = Icons.Default.Search
    ) {
        Text(
            text = cotizacion.observaciones,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun SeccionHistorialCotizacion() {
    CardSeccionCotizacion(
        titulo = "Historial de cambios",
        icono = Icons.Default.History
    ) {
        HistorialCotizacionItem(
            fecha = "19/05/2026 10:30 a.m.",
            cambio = "Cotización creada",
            usuario = "Administrador"
        )

        Divider(
            modifier = Modifier.padding(vertical = 5.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        HistorialCotizacionItem(
            fecha = "19/05/2026 10:30 a.m.",
            cambio = "Estado: Pendiente",
            usuario = "Administrador"
        )
    }
}

@Composable
fun HistorialCotizacionItem(
    fecha: String,
    cambio: String,
    usuario: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fecha,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = cambio,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = usuario,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
fun SeccionAccionesRapidasCotizacion(
    estado: String,
    onEditarClick: () -> Unit,
    onAprobarClick: () -> Unit,
    onRechazarClick: () -> Unit,
    onGenerarPdfClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            TituloSeccionCotizacion(
                titulo = "Acciones rápidas",
                icono = Icons.Default.Bolt,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BotonAccionCotizacion(
                    texto = "Editar",
                    icono = Icons.Default.Edit,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onEditarClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionCotizacion(
                    texto = "Generar PDF",
                    icono = Icons.Default.PictureAsPdf,
                    color = MaterialTheme.colorScheme.error,
                    onClick = onGenerarPdfClick,
                    modifier = Modifier.weight(1f)
                )

                if (estado.equals("Pendiente", ignoreCase = true)) {
                    BotonAccionCotizacion(
                        texto = "Aprobar",
                        icono = Icons.Default.CheckCircle,
                        color = MaterialTheme.arcshiftColors.success,
                        onClick = onAprobarClick,
                        modifier = Modifier.weight(1f)
                    )

                    BotonAccionCotizacion(
                        texto = "Rechazar",
                        icono = Icons.Default.Cancel,
                        color = MaterialTheme.colorScheme.error,
                        onClick = onRechazarClick,
                        modifier = Modifier.weight(1f)
                    )
                }

            }
        }
    }
}

@Composable
fun BotonAccionCotizacion(
    texto: String,
    icono: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = color,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = texto,
                fontSize = 8.sp,
                color = color,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CardSeccionCotizacion(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            contenido()
        }
    }
}

@Composable
fun TextoDetalleCotizacion(
    titulo: String,
    valor: String
) {
    Column(
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = valor,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FilaMontoCotizacion(
    titulo: String,
    valor: String,
    color: Color,
    negrita: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            fontSize = 10.sp,
            color = color,
            fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TituloSeccionCotizacion(
    titulo: String,
    icono: ImageVector,
    color: Color
){
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}