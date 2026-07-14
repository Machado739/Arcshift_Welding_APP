package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.Screen.notificaciones.CampanaNotificacionesPrincipal

@Composable
fun ProyectosScreen(
    navController: NavController,
    viewModel: ProyectosViewModel
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    val proyectos by viewModel.proyectos.collectAsState()
    val resumen by viewModel.resumen.collectAsState()

    val proyectosFiltrados = proyectos.filter { proyecto ->
        val coincideEstado = when (filtroSeleccionado) {
            "Todos" -> true
            "Pendiente" -> proyecto.estado == "Pendiente"
            "En trabajo" -> proyecto.estado == "En trabajo"
            "Terminado" -> proyecto.estado == "Terminado"
            "Cancelado" -> proyecto.estado == "Cancelado"
            else -> true
        }

        val coincideBusqueda =
            textoBusqueda.isBlank() ||
                    proyecto.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    proyecto.cliente.contains(textoBusqueda, ignoreCase = true) ||
                    proyecto.estado.contains(textoBusqueda, ignoreCase = true) ||
                    proyecto.cotizacion.contains(textoBusqueda, ignoreCase = true)

        coincideEstado && coincideBusqueda
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
        HeaderProyectos(navController = navController)

        Spacer(modifier = Modifier.height(8.dp))

        ResumenProyectosCompleto(
            resumen = resumen
        )

        Spacer(modifier = Modifier.height(8.dp))

        BuscadorProyectos(
            textoBusqueda = textoBusqueda,
            onTextoBusquedaChange = { textoBusqueda = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                navController.navigate(AppRoutes.NUEVO_PROYECTO)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Nuevo proyecto")
        }


        FiltrosEstadoProyectos(
            seleccionada = filtroSeleccionado,
            onSeleccionar = { filtroSeleccionado = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (proyectosFiltrados.isEmpty()) {
            EmptyProyectos()
        } else {
            ListaProyectosCompleta(
                proyectos = proyectosFiltrados,
                onClickProyecto = { proyecto ->
                    navController.navigate(AppRoutes.detalleProyecto(proyecto.id))
                }
            )
        }
    }
}

@Composable
fun HeaderProyectos(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = 20.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Text(
            text = "Proyectos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        CampanaNotificacionesPrincipal(navController)

        IconButton(
            onClick = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
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
fun ResumenProyectosCompleto(
    resumen: ResumenProyectosUI
) {
    val tarjetas = listOf(
        ResumenProyectoCardData(
            titulo = "Total",
            valor = resumen.total.toString(),
            subtitulo = "Registrados",
            icono = Icons.Default.Work,
            color = Color(0xFF2563EB)
        ),
        ResumenProyectoCardData(
            titulo = "En trabajo",
            valor = resumen.enTrabajo.toString(),
            subtitulo = "Activos",
            icono = Icons.Default.Build,
            color = Color(0xFF16A34A)
        ),
        ResumenProyectoCardData(
            titulo = "Pendientes",
            valor = resumen.pendientes.toString(),
            subtitulo = "Por iniciar",
            icono = Icons.Default.AccessTime,
            color = Color(0xFFF59E0B)
        ),
        ResumenProyectoCardData(
            titulo = "Terminados",
            valor = resumen.terminados.toString(),
            subtitulo = "Finalizados",
            icono = Icons.Default.Check,
            color = Color(0xFF7C3AED)
        ),
        ResumenProyectoCardData(
            titulo = "Cancelados",
            valor = resumen.cancelados.toString(),
            subtitulo = "Detenidos",
            icono = Icons.Default.Close,
            color = Color(0xFFDC2626)
        )
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(tarjetas) { tarjeta ->
            TarjetaResumenProyecto(
                tarjeta = tarjeta
            )
        }
    }
}

data class ResumenProyectoCardData(
    val titulo: String,
    val valor: String,
    val subtitulo: String,
    val icono: ImageVector,
    val color: Color
)

@Composable
fun TarjetaResumenProyecto(
    tarjeta: ResumenProyectoCardData
) {
    Card(
        modifier = Modifier
            .height(105.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    .background(
                        tarjeta.color.copy(alpha = 0.15f), CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tarjeta.icono,
                    contentDescription = null,
                    tint = tarjeta.color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tarjeta.titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = tarjeta.valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = tarjeta.subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BuscadorProyectos(
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onTextoBusquedaChange,
            placeholder = {
                Text("Buscar proyecto...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
/*
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
        }*/
    }
}

@Composable
fun FiltrosEstadoProyectos(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val filtros = listOf(
        "Todos",
        "Pendiente",
        "En trabajo",
        "Terminado",
        "Cancelado"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(filtros) { filtro ->
            ChipEstadoProyecto(
                texto = filtro,
                seleccionado = seleccionada == filtro,
                onClick = {
                    onSeleccionar(filtro)
                }
            )
        }
    }
}

@Composable
fun ChipEstadoProyecto(
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
                    imageVector = Icons.Default.Check,
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
fun ListaProyectosCompleta(
    proyectos: List<ProyectoUI>,
    onClickProyecto: (ProyectoUI) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(proyectos) { proyecto ->
            ItemProyectoCompleto(
                proyecto = proyecto,
                onClick = {
                    onClickProyecto(proyecto)
                }
            )
        }
    }
}

@Composable
fun ItemProyectoCompleto(
    proyecto: ProyectoUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                IconoProyectoEstado(
                    estado = proyecto.estado
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = proyecto.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    InfoProyectoLinea(
                        icono = Icons.Default.Person,
                        texto = proyecto.cliente
                    )

                    InfoProyectoLinea(
                        icono = Icons.Default.Description,
                        texto = proyecto.cotizacion
                    )
                }

                EstadoProyectoBadge(
                    estado = proyecto.estado
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoProyectoCaja(
                    titulo = "Inicio",
                    valor = proyecto.fechaInicio.ifBlank { "Sin fecha" },
                    icono = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f)
                )

                InfoProyectoCaja(
                    titulo = "Entrega",
                    valor = proyecto.fechaEstimadaFin.ifBlank { "Sin fecha" },
                    icono = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoProyectoCaja(
                    titulo = "Empleados",
                    valor = proyecto.empleadosAsignados.toString(),
                    icono = Icons.Default.Groups,
                    modifier = Modifier.weight(1f)
                )

                InfoProyectoCaja(
                    titulo = "Materiales",
                    valor = proyecto.materialesUsados.toString(),
                    icono = Icons.Default.ShoppingCart,
                    modifier = Modifier.weight(1f)
                )

                InfoProyectoCaja(
                    titulo = "Material",
                    valor = formatoMonedaProyecto(proyecto.costoMaterial),
                    icono = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            AvanceProyecto(
                avance = proyecto.avance,
                estado = proyecto.estado
            )
        }
    }
}

@Composable
fun IconoProyectoEstado(
    estado: String
) {
    val color = colorEstadoProyecto(estado)

    val icono = when (estado) {
        "Pendiente" -> Icons.Default.AccessTime
        "En trabajo" -> Icons.Default.Build
        "Terminado" -> Icons.Default.Check
        "Cancelado" -> Icons.Default.Close
        else -> Icons.Default.MoreHoriz
    }

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(27.dp)
        )
    }
}

@Composable
fun EstadoProyectoBadge(
    estado: String
) {
    val color = colorEstadoProyecto(estado)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = estado,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun InfoProyectoLinea(
    icono: ImageVector,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun InfoProyectoCaja(
    titulo: String,
    valor: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Column(
            modifier = Modifier.widthIn(min = 0.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AvanceProyecto(
    avance: Int,
    estado: String
) {
    val color = colorEstadoProyecto(estado)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Avance del proyecto",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Text(
                text = "$avance%",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = avance / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

@Composable
fun EmptyProyectos() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No hay proyectos registrados",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Agrega un proyecto para comenzar a controlar avances, empleados y materiales.",
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

fun colorEstadoProyecto(estado: String): Color {
    return when (estado) {
        "Pendiente" -> Color(0xFFF59E0B)
        "En trabajo" -> Color(0xFF2563EB)
        "Terminado" -> Color(0xFF16A34A)
        "Cancelado" -> Color(0xFFDC2626)
        else -> Color(0xFF64748B)
    }
}

fun formatoMonedaProyecto(valor: Double): String {
    return "${'$'}${String.format("%.2f", valor)}"
}

