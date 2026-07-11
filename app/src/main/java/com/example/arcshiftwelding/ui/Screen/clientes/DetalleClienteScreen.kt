package com.example.arcshiftwelding.ui.Screen.clientes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import java.text.NumberFormat
import java.util.Locale

data class ClienteDetalleUI(
    val id: Int,
    val nombre: String,
    val empresa: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val rfc: String,
    val tipoCliente: String,
    val registradoPor: String,
    val fechaRegistro: String,
    val ultimaActualizacion: String,
    val estado: String,
    val personaContacto: String,
    val ultimaActividad: String,
    val totalCotizaciones: Int,
    val totalIngresos: Int,
    val totalProyectos: Int,
    val totalFacturado: Double,
    val notas: String,
    val fotoUri: String
)

data class CotizacionClienteUI(
    val id: Int,
    val folio: String,
    val descripcion: String,
    val fecha: String,
    val estado: String,
    val monto: Double
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleClienteScreen(
    navController: NavController,
    clienteId: Int,
    viewModel: ClientesViewModel
) {
    val context = LocalContext.current

    val clienteFlow = remember(clienteId) {
        viewModel.obtenerClienteDetalle(clienteId)
    }

    val cliente by clienteFlow.collectAsState()

    val clienteConCotizacionesFlow = remember(clienteId) {
        viewModel.obtenerClienteConCotizaciones(clienteId)
    }

    val ingresosCliente by remember(clienteId) {
        viewModel.obtenerIngresosPorCliente(clienteId)
    }.collectAsState()

    val proyectosCliente by remember(clienteId) {
        viewModel.obtenerProyectosPorCliente(clienteId)
    }.collectAsState()
    val clienteConCotizaciones by clienteConCotizacionesFlow.collectAsState(initial = null)

    if (cliente == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val cotizacionesCliente = clienteConCotizaciones
        ?.cotizaciones
        ?.map { cotizacion ->
            CotizacionClienteUI(
                id = cotizacion.id,
                folio = cotizacion.folio,
                descripcion = cotizacion.descripcionTrabajo,
                fecha = cotizacion.fecha,
                estado = cotizacion.estado,
                monto = cotizacion.total
            )
        }
        ?: emptyList()

    val clienteActual = cliente!!.copy(
        totalCotizaciones = cotizacionesCliente.size,
        totalIngresos = ingresosCliente.size,
        totalProyectos = proyectosCliente.size,
        totalFacturado = ingresosCliente.sumOf { it.total }
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
                    text = "Detalle Cliente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        navController.navigate(AppRoutes.editarCliente(clienteActual.id))                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar cliente"
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

            CardPrincipalCliente(cliente = clienteActual)

            CardsContactoCliente(cliente = clienteActual)

            SeccionInformacionGeneralCliente(cliente = clienteActual)

            SeccionHistorialCliente(cliente = clienteActual)

            SeccionCotizacionesCliente(
                cotizaciones = cotizacionesCliente,
                onVerTodoClick = {
                    navController.navigate(
                        AppRoutes.cotizacionesCliente(clienteActual.id)
                    )
                },
                onCotizacionClick = { cotizacion ->
                    navController.navigate(AppRoutes.detalleCotizacion(cotizacion.id))
                }
            )

            SeccionNotasCliente(cliente = clienteActual)

            SeccionAccionesRapidasCliente(
                onEditarClick = {
                    navController.navigate(AppRoutes.editarCliente(clienteActual.id))
                },
                onLlamarClick = {
                    abrirMarcadorTelefono(
                        context = context,
                        telefono = clienteActual.telefono
                    )
                },
                onNuevaCotizacionClick = {
                    navController.navigate(
                        AppRoutes.nuevaCotizacion(clienteActual.id)
                    )
                },
                onEliminarClick = {
                    navController.navigate(AppRoutes.eliminarCliente(clienteActual.id))
                }
            )
        }
    }
}

@Composable
fun SeccionNotasCliente(
    cliente: ClienteDetalleUI
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
            TituloSeccionCliente(
                titulo = "Notas del cliente",
                icono = Icons.Default.Notes,
                color = Color(0xFFF59E0B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cliente.notas,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun CardPrincipalCliente(
    cliente: ClienteDetalleUI
) {
    val colorEstado = obtenerColorEstadoCliente(cliente.estado)
    val textoEstado = when (cliente.estado) {
        "Activo" -> "Cliente activo"
        "Inactivo" -> "Cliente inactivo"
        "Pendiente" -> "Cliente pendiente"
        else -> cliente.estado
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                contentAlignment = Alignment.Center
            ) {
                ImagenPerfilCliente(
                    fotoUri = cliente.fotoUri,
                    iniciales = obtenerIniciales(cliente.nombre),
                    modifier = Modifier.size(64.dp),
                    colorFondo = colorEstado.copy(alpha = 0.12f),
                    colorContenido = colorEstado
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(colorEstado)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cliente.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = cliente.empresa,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BadgeEstadoCliente(
                        texto = textoEstado,
                        color = colorEstado
                    )

                    BadgeEstadoCliente(
                        texto = cliente.tipoCliente,
                        color = Color(0xFF2563EB)
                    )
                }
            }
        }
    }
}

fun obtenerIniciales(nombre: String): String {
    return nombre
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
}
fun obtenerColorEstadoCliente(estado: String): Color {
    return when (estado) {
        "Activo" -> Color(0xFF16A34A)
        "Inactivo" -> Color(0xFF64748B)
        "Pendiente" -> Color(0xFFF59E0B)
        else -> Color.Gray
    }
}
@Composable
fun BadgeEstadoCliente(
    texto: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardsContactoCliente(
    cliente: ClienteDetalleUI
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardContactoCliente(
                titulo = "Teléfono",
                valor = cliente.telefono,
                icono = Icons.Default.Phone,
                color = Color(0xFF2563EB),
                maxLines = 2,
                onLongClick = {
                    copiarInformacionCliente(
                        context = context,
                        clipboardManager = clipboardManager,
                        titulo = "Teléfono",
                        valor = cliente.telefono
                    )
                },
                modifier = Modifier.weight(1f)
            )

            CardContactoCliente(
                titulo = "Correo",
                valor = cliente.correo,
                icono = Icons.Default.Email,
                color = Color(0xFF7C3AED),
                maxLines = 2,
                onLongClick = {
                    copiarInformacionCliente(
                        context = context,
                        clipboardManager = clipboardManager,
                        titulo = "Correo",
                        valor = cliente.correo
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        CardContactoCliente(
            titulo = "Dirección",
            valor = cliente.direccion,
            icono = Icons.Default.LocationOn,
            color = Color(0xFF16A34A),
            maxLines = 3,
            onLongClick = {
                copiarInformacionCliente(
                    context = context,
                    clipboardManager = clipboardManager,
                    titulo = "Dirección",
                    valor = cliente.direccion
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Mantén presionada una tarjeta para copiar la información.",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardContactoCliente(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    maxLines: Int,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val valorVisible = valor.ifBlank { "No registrado" }

    Card(
        modifier = modifier
            .heightIn(min = 76.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = valorVisible,
                style = MaterialTheme.typography.bodySmall,
                color = if (valor.isBlank()) Color.Gray else Color(0xFF334155),
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SeccionInformacionGeneralCliente(
    cliente: ClienteDetalleUI
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
            TituloSeccionCliente(
                titulo = "Información general",
                icono = Icons.Default.Info,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    ItemInfoCliente("RFC", cliente.rfc)
                    ItemInfoCliente("Tipo de cliente", cliente.tipoCliente)
                    ItemInfoCliente("Registrado por", cliente.registradoPor)
                    ItemInfoCliente("Persona de contacto", cliente.personaContacto)
                }

                Column(modifier = Modifier.weight(1f)) {
                    ItemInfoCliente("Fecha de registro", cliente.fechaRegistro)
                    ItemInfoCliente("Última actualización", cliente.ultimaActualizacion)
                    ItemInfoCliente("Estatus", cliente.estado)
                    ItemInfoCliente("Último contacto", cliente.ultimaActividad)
                }
            }
        }
    }
}

@Composable
fun ItemInfoCliente(
    titulo: String,
    valor: String
) {
    Column(
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun SeccionHistorialCliente(
    cliente: ClienteDetalleUI
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
            TituloSeccionCliente(
                titulo = "Historial / Actividad",
                icono = Icons.Default.History,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CardResumenActividad(
                    titulo = "Cotizaciones",
                    valor = cliente.totalCotizaciones.toString(),
                    subtitulo = "Registradas",
                    icono = Icons.Default.Description,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )

                CardResumenActividad(
                    titulo = "Proyectos",
                    valor = cliente.totalProyectos.toString(),
                    subtitulo = "Relacionados",
                    icono = Icons.Default.Work,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            CardResumenActividad(
                titulo = "Total recibido en ingresos",
                valor = cliente.totalFacturado.formatoMonedaCliente(),
                subtitulo = "${cliente.totalIngresos} ingreso${if (cliente.totalIngresos == 1) "" else "s"} registrado${if (cliente.totalIngresos == 1) "" else "s"}",
                icono = Icons.Default.AttachMoney,
                color = Color(0xFF16A34A),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CardResumenActividad(
    titulo: String,
    valor: String,
    subtitulo: String? = null,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.heightIn(min = 84.dp),
        shape = RoundedCornerShape(12.dp),
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = valor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!subtitulo.isNullOrBlank()) {
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SeccionCotizacionesCliente(
    cotizaciones: List<CotizacionClienteUI>,
    onVerTodoClick: () -> Unit,
    onCotizacionClick: (CotizacionClienteUI) -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TituloSeccionCliente(
                    titulo = "Cotizaciones recientes",
                    icono = Icons.Default.RequestQuote,
                    color = Color(0xFF2563EB)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Ver todas",
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onVerTodoClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            cotizaciones.forEach { cotizacion ->
                ItemCotizacionCliente(
                    cotizacion = cotizacion,
                    onClick = {
                        onCotizacionClick(cotizacion)
                    }
                )
            }
        }
    }
}

@Composable
fun ItemCotizacionCliente(
    cotizacion: CotizacionClienteUI,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cotizacion.folio,
                color = Color(0xFF2563EB),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = cotizacion.descripcion,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = cotizacion.fecha,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            BadgeCotizacionEstado(estado = cotizacion.estado)

            Text(
                text = "$ ${String.format("%,.2f", cotizacion.monto)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
fun BadgeCotizacionEstado(
    estado: String
) {
    val color = when (estado) {
        "Aprobado" -> Color(0xFF16A34A)
        "Pendiente" -> Color(0xFFF59E0B)
        "Rechazado" -> Color(0xFFDC2626)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = estado,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SeccionAccionesRapidasCliente(
    onEditarClick: () -> Unit,
    onLlamarClick: () -> Unit,
    onNuevaCotizacionClick: () -> Unit,
    onEliminarClick: () -> Unit
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
            TituloSeccionCliente(
                titulo = "Acciones rápidas",
                icono = Icons.Default.Bolt,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonAccionCliente(
                    texto = "Editar",
                    icono = Icons.Default.Edit,
                    color = Color(0xFF2563EB),
                    onClick = onEditarClick,
                    modifier = Modifier.weight(1f)
                )


                BotonAccionCliente(
                    texto = "Llamar",
                    icono = Icons.Default.Phone,
                    color = Color(0xFF2563EB),
                    onClick = onLlamarClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionCliente(
                    texto = "Nueva",
                    icono = Icons.Default.Description,
                    color = Color(0xFF7C3AED),
                    onClick = onNuevaCotizacionClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionCliente(
                    texto = "Eliminar",
                    icono = Icons.Default.Delete,
                    color = Color(0xFFDC2626),
                    onClick = onEliminarClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BotonAccionCliente(
    texto: String,
    icono: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                color = color,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TituloSeccionCliente(
    titulo: String,
    icono: ImageVector,
    color: Color
) {
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

private fun abrirMarcadorTelefono(
    context: Context,
    telefono: String
) {
    val numero = telefono.trim()

    if (numero.isBlank()) {
        Toast.makeText(
            context,
            "El cliente no tiene un teléfono registrado.",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val intent = Intent(
        Intent.ACTION_DIAL,
        Uri.fromParts("tel", numero, null)
    )

    runCatching {
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(
            context,
            "No se encontró una aplicación para realizar llamadas.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

private fun copiarInformacionCliente(
    context: Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    titulo: String,
    valor: String
) {
    val contenido = valor.trim()

    if (contenido.isBlank()) {
        Toast.makeText(
            context,
            "$titulo no registrado.",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    clipboardManager.setText(AnnotatedString(contenido))

    Toast.makeText(
        context,
        "$titulo copiado.",
        Toast.LENGTH_SHORT
    ).show()
}

private fun Double.formatoMonedaCliente(): String {
    return NumberFormat
        .getCurrencyInstance(Locale("es", "MX"))
        .format(this)
}

