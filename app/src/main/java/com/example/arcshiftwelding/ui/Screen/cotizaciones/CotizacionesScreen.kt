package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes

data class CotizacionUI(
    val id: Int,
    val cliente: String,
    val trabajo: String,
    val folio: String,
    val total: String,
    val estado: String,
    val fecha: String,
    val vence: String
)

@Composable
fun CotizacionesScreen(
    navController: NavController,
    viewModel: CotizacionesViewModel
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    val cotizaciones by viewModel.cotizaciones.collectAsState()

    val cotizacionesFiltradas = cotizaciones.filter { cotizacion ->
        categoriaSeleccionada == "Todos" || cotizacion.estado == categoriaSeleccionada
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(
                start = 8.dp,
                top = 0.dp,
                end = 8.dp,
                bottom = 8.dp
            )
    ) {
        HeaderCotizaciones(navController = navController)

        Spacer(modifier = Modifier.height(8.dp))

        ResumenCotizaciones(
            cotizaciones = cotizaciones
        )

        Spacer(modifier = Modifier.height(12.dp))

        BarraBusquedaCotizaciones()

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate(AppRoutes.NUEVA_COTIZACION)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nueva Cotización")
        }

        Spacer(modifier = Modifier.height(10.dp))

        FiltrosCategoriaCotizaciones(
            seleccionada = categoriaSeleccionada,
            onSeleccionar = {
                categoriaSeleccionada = it
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ListadoCotizaciones(
            cotizaciones = cotizacionesFiltradas,
            onClickCotizacion = { cotizacion ->
                navController.navigate(AppRoutes.detalleCotizacion(cotizacion.id))
            }
        )
    }
}

@Composable
fun HeaderCotizaciones(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(Icons.Default.Menu, contentDescription = "Menú")
        }

        Text(
            text = "Cotizaciones",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
        }

        IconButton(
            onClick = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Salir"
            )
        }
    }
}

@Composable
fun ResumenCotizaciones(
    cotizaciones: List<CotizacionUI>
) {
    val total = cotizaciones.size
    val pendientes = cotizaciones.count { it.estado == "Pendiente" }
    val aprobadas = cotizaciones.count { it.estado == "Aprobada" }
    val rechazadas = cotizaciones.count { it.estado == "Rechazada" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardResumenCotizacion(
            modifier = Modifier.weight(1f),
            titulo = "Total cotizaciones",
            monto = total.toString(),
            subtitulo = "Registradas",
            icono = Icons.Default.Description,
            color = Color(0xFF2563EB),
            fondo = Color(0xFFEFF6FF)
        )

        CardResumenCotizacion(
            modifier = Modifier.weight(1f),
            titulo = "Pendientes",
            monto = pendientes.toString(),
            subtitulo = "Activas",
            icono = Icons.Default.Schedule,
            color = Color(0xFFF59E0B),
            fondo = Color(0xFFFFFBEB)
        )

        CardResumenCotizacion(
            modifier = Modifier.weight(1f),
            titulo = "Aprobadas",
            monto = aprobadas.toString(),
            subtitulo = "Aceptadas",
            icono = Icons.Default.CheckCircle,
            color = Color(0xFF16A34A),
            fondo = Color(0xFFF0FDF4)
        )

        CardResumenCotizacion(
            modifier = Modifier.weight(1f),
            titulo = "Rechazadas",
            monto = rechazadas.toString(),
            subtitulo = "No aceptadas",
            icono = Icons.Default.Cancel,
            color = Color(0xFFDC2626),
            fondo = Color(0xFFFEF2F2)
        )
    }
}

@Composable
fun CardResumenCotizacion(
    modifier: Modifier = Modifier,
    titulo: String,
    monto: String,
    subtitulo: String,
    icono: ImageVector,
    color: Color,
    fondo: Color
) {
    Card(
        modifier = modifier.height(105.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(fondo.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Text(
                text = monto,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun BarraBusquedaCotizaciones() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    text = "Buscar cotización...",
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedButton(
            onClick = { },
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Filtros",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun FiltrosCategoriaCotizaciones(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf(
        "Todos",
        "Pendiente",
        "Aprobada",
        "Rechazada"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias) { categoria ->
            CategoriaChipCotizacion(
                texto = categoria,
                seleccionado = seleccionada == categoria,
                onClick = {
                    onSeleccionar(categoria)
                }
            )
        }
    }
}

@Composable
fun CategoriaChipCotizacion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = texto,
                maxLines = 1
            )
        },
        leadingIcon = {
            if (seleccionado) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (seleccionado) Color(0xFFE0ECFF) else Color.White,
            labelColor = if (seleccionado) Color(0xFF1D4ED8) else Color.DarkGray
        )
    )
}

@Composable
fun ListadoCotizaciones(
    cotizaciones: List<CotizacionUI>,
    onClickCotizacion: (CotizacionUI) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 0.dp,
                top = 0.dp,
                end = 0.dp,
                bottom = 8.dp
            )
    ) {
        items(cotizaciones) { cotizacion ->
            ItemCotizacion(
                cotizacion = cotizacion,
                onClick = {
                    onClickCotizacion(cotizacion)
                }
            )
        }
    }
}

@Composable
fun ItemCotizacion(
    cotizacion: CotizacionUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoEstadoCotizacion(cotizacion.estado)

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cotizacion.cliente,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )

                Text(
                    text = cotizacion.trabajo,
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Folio: ${cotizacion.folio}",
                    fontSize = 8.sp,
                    color = Color(0xFF2563EB),
                    modifier = Modifier
                        .background(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }

            DatosCotizacion(
                titulo = "Fecha",
                valor = cotizacion.fecha,
                color = Color.Black
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(68.dp)
            ) {
                Text(
                    text = "Estado",
                    fontSize = 8.sp,
                    color = Color.Gray
                )

                Text(
                    text = cotizacion.estado,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorEstadoCotizacion(cotizacion.estado),
                    modifier = Modifier
                        .background(
                            color = FondoEstadoCotizacion(cotizacion.estado),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Vence: ${cotizacion.vence}",
                    fontSize = 7.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(58.dp)
            ) {
                Text(
                    text = "Total",
                    fontSize = 8.sp,
                    color = Color.Gray
                )

                Text(
                    text = cotizacion.total,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB)
                )

                IconButton(
                    onClick = { },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun IconoEstadoCotizacion(
    estado: String
) {
    val icono = when (estado) {
        "Pendiente" -> Icons.Default.Schedule
        "Aprobada" -> Icons.Default.CheckCircle
        "Rechazada" -> Icons.Default.Cancel
        else -> Icons.Default.Description
    }

    val color = when (estado) {
        "Pendiente" -> Color(0xFFF59E0B)
        "Aprobada" -> Color(0xFF16A34A)
        "Rechazada" -> Color(0xFFDC2626)
        else -> Color(0xFF2563EB)
    }

    val fondo = when (estado) {
        "Pendiente" -> Color(0xFFFEF3C7)
        "Aprobada" -> Color(0xFFDCFCE7)
        "Rechazada" -> Color(0xFFFEE2E2)
        else -> Color(0xFFEFF6FF)
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .background(
                color = fondo,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = estado,
            tint = color,
            modifier = Modifier.size(23.dp)
        )
    }
}

@Composable
fun DatosCotizacion(
    titulo: String,
    valor: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(58.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 8.sp,
            color = Color.Gray
        )

        Text(
            text = valor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1
        )
    }
}

fun ColorEstadoCotizacion(estado: String): Color {
    return when (estado) {
        "Pendiente" -> Color(0xFFF59E0B)
        "Aprobada" -> Color(0xFF16A34A)
        "Rechazada" -> Color(0xFFDC2626)
        else -> Color(0xFF2563EB)
    }
}

fun FondoEstadoCotizacion(estado: String): Color {
    return when (estado) {
        "Pendiente" -> Color(0xFFFFF7E6)
        "Aprobada" -> Color(0xFFEAF7EE)
        "Rechazada" -> Color(0xFFFFEEEE)
        else -> Color(0xFFEFF6FF)
    }
}