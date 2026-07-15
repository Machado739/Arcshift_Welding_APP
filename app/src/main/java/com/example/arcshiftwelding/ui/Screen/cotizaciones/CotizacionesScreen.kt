package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import com.example.arcshiftwelding.ui.theme.arcshiftColors

data class CotizacionUI(
    val id: Int,
    val cliente: String,
    val trabajo: String,
    val folio: String,
    val total: String,
    val estado: String,
    val fecha: String,
    val vence: String,
    val clienteId: Int = 0
)

@Composable
fun CotizacionesScreen(
    navController: NavController,
    viewModel: CotizacionesViewModel,
    clienteIdInicial: Int? = null
) {
    val cotizaciones by viewModel.cotizaciones.collectAsState()
    var estadoSeleccionado by rememberSaveable { mutableStateOf("Todos") }
    var periodoSeleccionado by rememberSaveable { mutableStateOf("Todo") }
    var textoBusqueda by rememberSaveable { mutableStateOf("") }
    var resumenExpandido by rememberSaveable { mutableStateOf(false) }

    val cotizacionesCliente = remember(cotizaciones, clienteIdInicial) {
        if (clienteIdInicial == null) cotizaciones
        else cotizaciones.filter { it.clienteId == clienteIdInicial }
    }

    val cotizacionesPeriodo = remember(cotizacionesCliente, periodoSeleccionado) {
        cotizacionesCliente.filter {
            fechaCotizacionEnPeriodo(it.fecha, periodoSeleccionado)
        }
    }

    val cotizacionesFiltradas = remember(
        cotizacionesPeriodo,
        estadoSeleccionado,
        textoBusqueda
    ) {
        val busqueda = textoBusqueda.trim()
        cotizacionesPeriodo.filter { cotizacion ->
            val coincideEstado = estadoSeleccionado == "Todos" ||
                cotizacion.estado.equals(estadoSeleccionado, ignoreCase = true)
            val coincideBusqueda = busqueda.isBlank() ||
                cotizacion.folio.contains(busqueda, ignoreCase = true) ||
                cotizacion.cliente.contains(busqueda, ignoreCase = true) ||
                cotizacion.trabajo.contains(busqueda, ignoreCase = true)
            coincideEstado && coincideBusqueda
        }
    }

    val nombreCliente = cotizacionesCliente.firstOrNull()?.cliente ?: "Cliente seleccionado"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        HeaderCotizaciones(
            navController = navController,
            mostrarRegresar = clienteIdInicial != null
        )

        SelectorPeriodoCotizaciones(
            seleccionado = periodoSeleccionado,
            onSeleccionar = { periodoSeleccionado = it }
        )

        ResumenCotizacionesCompacto(
            cotizaciones = cotizacionesPeriodo,
            expandido = resumenExpandido,
            onCambiar = { resumenExpandido = !resumenExpandido }
        )

        if (clienteIdInicial != null) {
            Spacer(modifier = Modifier.height(6.dp))
            FiltroClienteCotizaciones(nombreCliente)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BarraBusquedaCotizaciones(
                valor = textoBusqueda,
                onValorChange = { textoBusqueda = it },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (clienteIdInicial != null) {
                        navController.navigate(AppRoutes.nuevaCotizacion(clienteIdInicial))
                    } else {
                        navController.navigate(AppRoutes.NUEVA_COTIZACION)
                    }
                },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva cotización")
            }
        }

        FiltrosCategoriaCotizaciones(
            seleccionada = estadoSeleccionado,
            onSeleccionar = { estadoSeleccionado = it }
        )

        Text(
            text = "Cotizaciones (${cotizacionesFiltradas.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
        )

        ListadoCotizaciones(
            cotizaciones = cotizacionesFiltradas,
            mensajeVacio = if (clienteIdInicial != null) {
                "Este cliente no tiene cotizaciones para los filtros seleccionados."
            } else {
                "No hay cotizaciones para los filtros seleccionados."
            },
            onClickCotizacion = {
                navController.navigate(AppRoutes.detalleCotizacion(it.id))
            }
        )
    }
}

@Composable
fun HeaderCotizaciones(
    navController: NavController,
    mostrarRegresar: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = if (mostrarRegresar) 0.dp else 20.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (mostrarRegresar) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
            }
        }
        Text(
            text = "Cotizaciones",
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
            Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
        }
    }
}

@Composable
private fun SelectorPeriodoCotizaciones(
    seleccionado: String,
    onSeleccionar: (String) -> Unit
) {
    val periodos = listOf("Todo", "Hoy", "Semana", "Mes", "Año")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        periodos.forEach { periodo ->
            val activo = seleccionado == periodo
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .clickable { onSeleccionar(periodo) }
                    .padding(horizontal = 9.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = periodo,
                    fontSize = 9.sp,
                    color = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(2.dp)
                        .background(
                            if (activo) MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun ResumenCotizacionesCompacto(
    cotizaciones: List<CotizacionUI>,
    expandido: Boolean,
    onCambiar: () -> Unit
) {
    val total = cotizaciones.size
    val pendientes = cotizaciones.count { it.estado.equals("Pendiente", true) }
    val aprobadas = cotizaciones.count { it.estado.equals("Aprobada", true) }
    val rechazadas = cotizaciones.count { it.estado.equals("Rechazada", true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onCambiar),
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ResumenCotizacionDato(
                    titulo = "Total",
                    valor = total.toString(),
                    icono = Icons.Default.Description,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                ResumenCotizacionDato(
                    titulo = "Pendientes",
                    valor = pendientes.toString(),
                    icono = Icons.Default.Schedule,
                    color = MaterialTheme.arcshiftColors.warning,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expandido) "Contraer" else "Desplegar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            AnimatedVisibility(visible = expandido) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResumenCotizacionDato(
                        titulo = "Aprobadas",
                        valor = aprobadas.toString(),
                        icono = Icons.Default.CheckCircle,
                        color = MaterialTheme.arcshiftColors.success,
                        modifier = Modifier.weight(1f)
                    )
                    ResumenCotizacionDato(
                        titulo = "Rechazadas",
                        valor = rechazadas.toString(),
                        icono = Icons.Default.Cancel,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ResumenCotizacionDato(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(7.dp))
        Column {
            Text(text = titulo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
            Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun FiltroClienteCotizaciones(nombreCliente: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(9.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(17.dp))
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = "Cliente: $nombreCliente",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BarraBusquedaCotizaciones(
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier.height(48.dp),
        placeholder = { Text("Buscar folio, cliente o trabajo...", fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            if (valor.isNotBlank()) {
                IconButton(onClick = { onValorChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar", modifier = Modifier.size(18.dp))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(9.dp)
    )
}

@Composable
fun FiltrosCategoriaCotizaciones(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf("Todos", "Pendiente", "Aprobada", "Rechazada")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias) { categoria ->
            AssistChip(
                onClick = { onSeleccionar(categoria) },
                label = { Text(categoria, maxLines = 1, fontSize = 10.sp) },
                leadingIcon = {
                    if (seleccionada == categoria) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                    }
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (seleccionada == categoria) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    labelColor = if (seleccionada == categoria) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun ListadoCotizaciones(
    cotizaciones: List<CotizacionUI>,
    mensajeVacio: String,
    onClickCotizacion: (CotizacionUI) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        if (cotizaciones.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = mensajeVacio,
                        modifier = Modifier.padding(18.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            items(items = cotizaciones, key = { it.id }) { cotizacion ->
                ItemCotizacion(cotizacion) { onClickCotizacion(cotizacion) }
            }
        }
    }
}

@Composable
fun ItemCotizacion(
    cotizacion: CotizacionUI,
    onClick: () -> Unit
) {
    val colorEstado = ColorEstadoCotizacion(cotizacion.estado)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoEstadoCotizacion(cotizacion.estado)
            Spacer(modifier = Modifier.width(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cotizacion.cliente,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = cotizacion.estado,
                        color = colorEstado,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(FondoEstadoCotizacion(cotizacion.estado), RoundedCornerShape(5.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = cotizacion.trabajo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cotizacion.folio,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "${cotizacion.fecha} · vence ${cotizacion.vence}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 7.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = cotizacion.total,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = when {
                    cotizacion.total.length >= 13 -> 9.sp
                    cotizacion.total.length >= 10 -> 10.sp
                    else -> 12.sp
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun IconoEstadoCotizacion(estado: String) {
    val icono = when {
        estado.equals("Pendiente", true) -> Icons.Default.Schedule
        estado.equals("Aprobada", true) -> Icons.Default.CheckCircle
        estado.equals("Rechazada", true) -> Icons.Default.Cancel
        else -> Icons.Default.Description
    }
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(FondoEstadoCotizacion(estado), RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = estado, tint = ColorEstadoCotizacion(estado), modifier = Modifier.size(21.dp))
    }
}

@Composable

fun ColorEstadoCotizacion(estado: String): Color = when {
    estado.equals("Pendiente", true) -> MaterialTheme.arcshiftColors.warning
    estado.equals("Aprobada", true) -> MaterialTheme.arcshiftColors.success
    estado.equals("Rechazada", true) -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.primary
}

@Composable

fun FondoEstadoCotizacion(estado: String): Color = when {
    estado.equals("Pendiente", true) -> MaterialTheme.arcshiftColors.warningContainer
    estado.equals("Aprobada", true) -> MaterialTheme.arcshiftColors.successContainer
    estado.equals("Rechazada", true) -> MaterialTheme.colorScheme.errorContainer
    else -> MaterialTheme.colorScheme.primaryContainer
}

private val formatosFechaCotizacion = listOf(
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ofPattern("d/M/yyyy"),
    DateTimeFormatter.ISO_LOCAL_DATE
)

private fun fechaCotizacionEnPeriodo(fechaTexto: String, periodo: String): Boolean {
    if (periodo == "Todo") return true
    val fecha = formatosFechaCotizacion.firstNotNullOfOrNull { formato ->
        runCatching {
            LocalDate.parse(
                fechaTexto.trim().substringBefore(' ').substringBefore('T'),
                formato
            )
        }.getOrNull()
    } ?: return false

    val hoy = LocalDate.now()
    return when (periodo) {
        "Hoy" -> fecha == hoy
        "Semana" -> {
            val inicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            !fecha.isBefore(inicio) && !fecha.isAfter(inicio.plusDays(6))
        }
        "Mes" -> fecha.year == hoy.year && fecha.month == hoy.month
        "Año" -> fecha.year == hoy.year
        else -> true
    }
}
