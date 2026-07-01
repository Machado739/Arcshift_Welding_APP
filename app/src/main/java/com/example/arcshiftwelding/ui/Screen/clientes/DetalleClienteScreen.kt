package com.example.arcshiftwelding.ui.Screen.clientes

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.arcshiftwelding.navigation.AppRoutes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.IconButton

import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.BottomNavigationBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlin.collections.emptyList
import androidx.room.Transaction
import com.example.arcshiftwelding.data.local.relation.ClienteConCotizaciones
import kotlinx.coroutines.flow.Flow

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
    val notas: String
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


    val clienteFlow = remember(clienteId) {
        viewModel.obtenerClienteDetalle(clienteId)
    }

    val cliente by clienteFlow.collectAsState()

    val clienteConCotizacionesFlow = remember(clienteId) {
        viewModel.obtenerClienteConCotizaciones(clienteId)
    }

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
        totalFacturado = cotizacionesCliente.sumOf { it.monto }
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
                    navController.navigate(AppRoutes.COTIZACIONES)
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
           /*     onWhatsappClick = { },*/
                onLlamarClick = { },
                onNuevaCotizacionClick = {
                    navController.navigate(AppRoutes.NUEVA_COTIZACION)
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
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colorEstado.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = obtenerIniciales(cliente.nombre),
                    color = colorEstado,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
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

@Composable
fun CardsContactoCliente(
    cliente: ClienteDetalleUI
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardContactoCliente(
            titulo = "Teléfono",
            valor = cliente.telefono,
        /*    accion = "Llamar",*/
            icono = Icons.Default.Phone,
            color = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )

        CardContactoCliente(
            titulo = "Correo",
            valor = cliente.correo,
        /*    accion = "Enviar",*/
            icono = Icons.Default.Email,
            color = Color(0xFF7C3AED),
            modifier = Modifier.weight(1f)
        )

        CardContactoCliente(
            titulo = "Dirección",
            valor = cliente.direccion,
           /* accion = "Ver en mapa",*/
            icono = Icons.Default.LocationOn,
            color = Color(0xFF16A34A),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CardContactoCliente(
    titulo: String,
    valor: String,
  /*  accion: String,*/
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
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

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valor,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.weight(1f))
            /*
            Text(
                text = accion,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )*/
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TituloSeccionCliente(
                    titulo = "Historial / Actividad",
                    icono = Icons.Default.History,
                    color = Color(0xFF2563EB)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Ver todo",
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {



                CardResumenActividad(
                    titulo = "Cotizaciones",
                    valor = cliente.totalCotizaciones.toString(),
                    icono = Icons.Default.Description,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )

                CardResumenActividad(
                    titulo = "Ingresos",
                    valor = cliente.totalIngresos.toString(),
                    icono = Icons.Default.AttachMoney,
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )

                CardResumenActividad(
                    titulo = "Proyectos",
                    valor = cliente.totalProyectos.toString(),
                    icono = Icons.Default.Work,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
                /*
                CardResumenActividad(
                    titulo = "Total facturado",
                    valor = "$ ${String.format("%,.2f", cliente.totalFacturado)}",
                    icono = Icons.Default.InsertChart,
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1.2f)
                )*/
            }
        }
    }
}

@Composable
fun CardResumenActividad(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(78.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = valor,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
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

              /*  BotonAccionCliente(
                    texto = "WhatsApp",
                    icono = Icons.Default.Whatsapp,
                    color = Color(0xFF16A34A),
                    onClick = onWhatsappClick,
                    modifier = Modifier.weight(1f)
                )*/

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

