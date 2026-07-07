package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.foundation.background
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
import androidx.compose.ui.focus.focusModifier
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
import com.example.arcshiftwelding.utils.calcularResumenCotizacion
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
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            Text("Cotización no encontrada")
        }
        return
    }

    val cotizacionActual = cotizacionCompleta!!
    val cotizacionUi = cotizacionActual.toDetalleUi()
    val detalles = cotizacionActual.detalles

    var mostrarDialogoCrearProyecto by remember { mutableStateOf(false) }

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
        containerColor = Color(0xFFF5F5F5),
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

            SeccionObservacionesCotizacion(cotizacion = cotizacionUi)

            SeccionAccionesRapidasCotizacion(
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
                onGenerarPdfClick = { },
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
            containerColor = Color.White
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
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
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
                        color = Color(0xFF15803D)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = cotizacion.estado,
                        fontSize = 8.sp,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFFF7E6),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = cotizacion.trabajo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cotizacion.total,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)
                )

                Text(
                    text = "Cliente: ${cotizacion.cliente}",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    maxLines = 1
                )

                Row {
                    Text(
                        text = "Creada: ${cotizacion.fecha}",
                        fontSize = 8.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Vigencia: ${cotizacion.vigencia}",
                        fontSize = 8.sp,
                        color = Color.Gray
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
        FilaMontoCotizacion("Subtotal", cotizacion.subtotal, Color.Black)

        if (cotizacion.descuento != "$0.00" && cotizacion.descuento != "$ 0.00") {
            FilaMontoCotizacion("Descuento", cotizacion.descuento, Color(0xFFDC2626))
        }

        FilaMontoCotizacion(
            "IVA (${cotizacion.ivaPorcentaje}%)",
            cotizacion.iva,
            Color.Black
        )

        Divider(
            modifier = Modifier.padding(vertical = 6.dp),
            color = Color(0xFFE2E8F0)
        )

        FilaMontoCotizacion("Total", cotizacion.total, Color(0xFF16A34A), true)

        FilaMontoCotizacion(
            "Anticipo requerido (${cotizacion.anticipoPorcentaje}%)",
            cotizacion.anticipo,
            Color.Black
        )

        FilaMontoCotizacion("Saldo restante", cotizacion.saldo, Color.Black)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Estado actual",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = cotizacion.estado,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B),
                modifier = Modifier
                    .background(
                        color = Color(0xFFFFF7E6),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextoDetalleCotizacion(
            titulo = "En espera de respuesta",
            valor = "del cliente."
        )

        TextoDetalleCotizacion(
            titulo = "Última actualización",
            valor = "19/05/2026 10:30 a.m."
        )
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
            color = Color(0xFFE2E8F0)
        )

        FilaMontoCotizacion(
            titulo = "Subtotal de conceptos",
            valor = detalles.sumOf { it.total }.formatoMoneda(),
            color = Color(0xFF16A34A),
            negrita = true
        )
    }
}

@Composable
fun EncabezadoConceptoCotizacion() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Concepto", fontSize = 8.sp, color = Color.Gray, modifier = Modifier.weight(1.4f))
        Text("Cant.", fontSize = 8.sp, color = Color.Gray, modifier = Modifier.weight(0.5f))
        Text("Unidad", fontSize = 8.sp, color = Color.Gray, modifier = Modifier.weight(0.6f))
        Text("Importe", fontSize = 8.sp, color = Color.Gray, modifier = Modifier.weight(0.8f))
    }

    Divider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = Color(0xFFE2E8F0)
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
                color = Color.Black,
                maxLines = 1
            )

            Text(
                text = precio,
                fontSize = 8.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }

        Text(
            text = cantidad,
            fontSize = 9.sp,
            color = Color.DarkGray,
            modifier = Modifier.weight(0.5f)
        )

        Text(
            text = unidad,
            fontSize = 9.sp,
            color = Color.DarkGray,
            modifier = Modifier.weight(0.6f)
        )

        Text(
            text = importe,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(0.8f)
        )
    }

    Spacer(modifier = Modifier.height(5.dp))
}

@Composable
fun SeccionArchivosCotizacion() {
    CardSeccionCotizacion(
        titulo = "Archivos adjuntos",
        icono = Icons.Default.AttachFile
    ) {
        ArchivoCotizacionItem(
            nombre = "plano_estructura.pdf",
            detalle = "245 KB",
            icono = Icons.Default.PictureAsPdf,
            color = Color(0xFFDC2626),
            fondo = Color(0xFFFEE2E2)
        )

        Spacer(modifier = Modifier.height(6.dp))

        ArchivoCotizacionItem(
            nombre = "referencia.jpg",
            detalle = "1.2 MB",
            icono = Icons.Default.Image,
            color = Color(0xFF2563EB),
            fondo = Color(0xFFEFF6FF)
        )
    }
}

@Composable
fun ArchivoCotizacionItem(
    nombre: String,
    detalle: String,
    icono: ImageVector,
    color: Color,
    fondo: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp)
            )
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
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = nombre,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1
            )

            Text(
                text = detalle,
                fontSize = 8.sp,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = "Ver archivo",
            tint = Color.Gray,
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
            color = Color.DarkGray,
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
            color = Color(0xFFE2E8F0)
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
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = cambio,
            fontSize = 8.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = usuario,
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.8f)
        )
    }
}

@Composable
fun SeccionAccionesRapidasCotizacion(
    onEditarClick: () -> Unit,
    onAprobarClick: () -> Unit,
    onRechazarClick: () -> Unit,
    onGenerarPdfClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            TituloSeccionCotizacion(
                titulo = "Acciones rápidas",
                icono = Icons.Default.Bolt,
                color = Color(0xFF2563EB)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BotonAccionCotizacion(
                    texto = "Editar",
                    icono = Icons.Default.Edit,
                    color = Color(0xFF2563EB),
                    onClick = onEditarClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionCotizacion(
                    texto = "PDF",
                    icono = Icons.Default.PictureAsPdf,
                    color = Color(0xFFDC2626),
                    onClick = onGenerarPdfClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionCotizacion(
                    texto = "Aprobar",
                    icono = Icons.Default.CheckCircle,
                    color = Color(0xFF16A34A),
                    onClick = onAprobarClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionCotizacion(
                    texto = "Rechazar",
                    icono = Icons.Default.Cancel,
                    color = Color(0xFFDC2626),
                    onClick = onRechazarClick,
                    modifier = Modifier.weight(1f)
                )

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
            color = Color.Gray
        )

        Text(
            text = valor,
            fontSize = 10.sp,
            color = Color.Black,
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
            color = Color.DarkGray,
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