package com.example.arcshiftwelding.ui.Screen.gastos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.Screen.notificaciones.CampanaNotificacionesPrincipal
import com.example.arcshiftwelding.ui.viewmodel.GastosViewModel
import com.example.arcshiftwelding.utils.ComprobanteArchivoSeleccionado
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import com.example.arcshiftwelding.ui.theme.arcshiftColors


data class GastoUi(
    val id: Int,
    val concepto: String,
    val categoria: String,
    val fecha: String,
    val proveedor: String,
    val subtotal: Double,
    val ivaPorcentaje: Double,
    val iva: Double,
    val total: Double,
    val metodoPago: String,
    val formaPago: String,
    val telefonoProveedor: String,
    val correoProveedor: String,
    val rfcProveedor: String,
    val observaciones: String,
    val proyecto: String,
    val cotizacion: String,
    val cliente: String,
    val comprobanteUri: String = "",
    val tipoComprobante: String = "",
    val nombreComprobante: String = "",
    val comprobantes: List<ComprobanteArchivoSeleccionado> = emptyList()
)

private enum class PeriodoGastos(val etiqueta: String) {
    TODO("Todo"),
    HOY("Hoy"),
    SEMANA("Semana"),
    MES("Mes"),
    ANIO("Año")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    navController: NavController,
    viewModel: GastosViewModel
) {
    var categoriaSeleccionada by rememberSaveable { mutableStateOf("Todos") }
    var periodoSeleccionado by rememberSaveable { mutableStateOf(PeriodoGastos.TODO) }
    var textoBusqueda by rememberSaveable { mutableStateOf("") }
    var resumenExpandido by rememberSaveable { mutableStateOf(false) }

    val gastos by viewModel.gastos.collectAsState()
    val hoy = remember { LocalDate.now() }

    val gastosDelPeriodo = remember(gastos, periodoSeleccionado, hoy) {
        gastos.filter { gasto ->
            perteneceAlPeriodo(
                fechaTexto = gasto.fecha,
                periodo = periodoSeleccionado,
                hoy = hoy
            )
        }
    }

    val gastosFiltrados = remember(
        gastosDelPeriodo,
        categoriaSeleccionada,
        textoBusqueda
    ) {
        gastosDelPeriodo.filter { gasto ->
            val coincideCategoria = when (categoriaSeleccionada) {
                "Todos" -> true
                "Más" -> gasto.categoria !in categoriasPrincipalesGastos
                else -> gasto.categoria.equals(categoriaSeleccionada, ignoreCase = true)
            }

            val coincideBusqueda = textoBusqueda.isBlank() ||
                gasto.concepto.contains(textoBusqueda, ignoreCase = true) ||
                gasto.proveedor.contains(textoBusqueda, ignoreCase = true) ||
                gasto.categoria.contains(textoBusqueda, ignoreCase = true) ||
                gasto.proyecto.contains(textoBusqueda, ignoreCase = true) ||
                gasto.cliente.contains(textoBusqueda, ignoreCase = true)

            coincideCategoria && coincideBusqueda
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp)
    ) {
        HeaderGastos(navController = navController)

        SelectorPeriodoGastos(
            seleccionado = periodoSeleccionado,
            onSeleccionar = { periodoSeleccionado = it }
        )

        ResumenCompactoGastos(
            gastos = gastosDelPeriodo,
            periodo = periodoSeleccionado,
            expandido = resumenExpandido,
            onCambiarExpansion = { resumenExpandido = !resumenExpandido }
        )

        Spacer(modifier = Modifier.height(6.dp))

        BarraBusquedaGastosCompacta(
            textoBusqueda = textoBusqueda,
            onTextoBusquedaChange = { textoBusqueda = it },
            onNuevoGasto = { navController.navigate(AppRoutes.NUEVO_GASTO) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        FiltrosCategoriaGastos(
            seleccionada = categoriaSeleccionada,
            onSeleccionar = { categoriaSeleccionada = it }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (gastosFiltrados.size == 1) "1 gasto registrado" else "${gastosFiltrados.size} gastos registrados",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
        )

        if (gastosFiltrados.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay gastos para los filtros seleccionados",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            ListaGastos(
                gastos = gastosFiltrados,
                onClickGasto = { gasto ->
                    navController.navigate(AppRoutes.detalleGasto(gasto.id))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun HeaderGastos(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                start = 20.dp,
                top = 8.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Text(
            text = "Gastos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        CampanaNotificacionesPrincipal(navController)
        IconButton(
            onClick = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true
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
private fun SelectorPeriodoGastos(
    seleccionado: PeriodoGastos,
    onSeleccionar: (PeriodoGastos) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeriodoGastos.entries.forEach { periodo ->
            val activo = seleccionado == periodo

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSeleccionar(periodo) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = periodo.etiqueta,
                    fontSize = 11.sp,
                    color = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (activo) FontWeight.SemiBold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(if (activo) 24.dp else 0.dp)
                        .background(
                            color = if (activo) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun ResumenCompactoGastos(
    gastos: List<GastoUi>,
    periodo: PeriodoGastos,
    expandido: Boolean,
    onCambiarExpansion: () -> Unit
) {
    val total = remember(gastos) { gastos.sumOf { it.total } }
    val promedio = remember(gastos, total) {
        if (gastos.isEmpty()) 0.0 else total / gastos.size
    }
    val categorias = remember(gastos) {
        gastos.map { it.categoria.trim() }.filter { it.isNotEmpty() }.distinct().size
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCambiarExpansion),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 8.dp, end = 6.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DatoResumenGastoCompacto(
                    titulo = "Total gastos",
                    valor = formatearMonedaGasto(total),
                    subtitulo = periodo.etiqueta,
                    icono = Icons.Default.AttachMoney,
                    colorIcono = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                DatoResumenGastoCompacto(
                    titulo = "Promedio",
                    valor = formatearMonedaGasto(promedio),
                    subtitulo = "Por gasto",
                    icono = Icons.Default.Analytics,
                    colorIcono = MaterialTheme.arcshiftColors.warning,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expandido) "Contraer resumen" else "Desplegar resumen",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(
                visible = expandido,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DatoSecundarioResumenGasto(
                            titulo = "Registros",
                            valor = gastos.size.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        DatoSecundarioResumenGasto(
                            titulo = "Categorías",
                            valor = categorias.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatoResumenGastoCompacto(
    titulo: String,
    valor: String,
    subtitulo: String,
    icono: ImageVector,
    colorIcono: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(colorIcono.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = valor,
                fontSize = tamanoTextoMonto(valor),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitulo,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DatoSecundarioResumenGasto(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BarraBusquedaGastosCompacta(
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit,
    onNuevoGasto: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onTextoBusquedaChange,
            placeholder = { Text("Buscar gasto...", fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (textoBusqueda.isNotBlank()) {
                    IconButton(onClick = { onTextoBusquedaChange("") }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Limpiar búsqueda",
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            singleLine = true,
            shape = RoundedCornerShape(9.dp)
        )

        FilledIconButton(
            onClick = onNuevoGasto,
            modifier = Modifier.size(46.dp),
            shape = RoundedCornerShape(10.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Nuevo gasto",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private val categoriasPrincipalesGastos = setOf(
    "Materiales",
    "Servicios",
    "Transporte",
    "Nómina",
    "Herramientas"
)

@Composable
fun FiltrosCategoriaGastos(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf(
        "Todos",
        "Materiales",
        "Servicios",
        "Transporte",
        "Nómina",
        "Herramientas",
        "Más"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias) { categoria ->
            CategoriaChip(
                texto = categoria,
                seleccionado = seleccionada == categoria,
                onClick = { onSeleccionar(categoria) }
            )
        }
    }
}

@Composable
fun CategoriaChip(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = {
            Text(
                text = texto,
                maxLines = 1,
                fontSize = 11.sp
            )
        },
        leadingIcon = {
            if (seleccionado) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        },
        modifier = Modifier.height(32.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = seleccionado,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun ListaGastos(
    gastos: List<GastoUi>,
    onClickGasto: (GastoUi) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(7.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(
            items = gastos,
            key = { it.id }
        ) { gasto ->
            ItemGasto(
                gasto = gasto,
                onClick = { onClickGasto(gasto) }
            )
        }
    }
}

@Composable
fun ItemGasto(
    gasto: GastoUi,
    onClick: () -> Unit
) {
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
            IconoCategoriaGasto(gasto.categoria)

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gasto.concepto,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Proveedor: ${gasto.proveedor.ifBlank { "Sin proveedor" }}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = gasto.categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.widthIn(min = 84.dp, max = 112.dp)
            ) {
                Text(
                    text = formatearMonedaGasto(gasto.total),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = tamanoTextoMonto(formatearMonedaGasto(gasto.total)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = gasto.fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Text(
                    text = gasto.metodoPago,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun IconoCategoriaGasto(
    categoria: String
) {
    val icono = when (categoria) {
        "Materiales" -> Icons.Default.ShoppingCart
        "Transporte" -> Icons.Default.LocalGasStation
        "Servicios" -> Icons.Default.Build
        "Nómina" -> Icons.Default.Person
        "Herramientas" -> Icons.Default.Handyman
        else -> Icons.Default.MoreHoriz
    }

    val color = when (categoria) {
        "Materiales" -> MaterialTheme.colorScheme.primary
        "Transporte" -> MaterialTheme.arcshiftColors.success
        "Servicios" -> MaterialTheme.arcshiftColors.warning
        "Nómina" -> MaterialTheme.arcshiftColors.success
        "Herramientas" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(42.dp)
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
    }
}

private fun perteneceAlPeriodo(
    fechaTexto: String,
    periodo: PeriodoGastos,
    hoy: LocalDate
): Boolean {
    if (periodo == PeriodoGastos.TODO) return true

    val fecha = parsearFechaGasto(fechaTexto) ?: return false

    return when (periodo) {
        PeriodoGastos.TODO -> true
        PeriodoGastos.HOY -> fecha == hoy
        PeriodoGastos.SEMANA -> {
            val inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val finSemana = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            !fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana)
        }
        PeriodoGastos.MES -> fecha.year == hoy.year && fecha.month == hoy.month
        PeriodoGastos.ANIO -> fecha.year == hoy.year
    }
}

private fun parsearFechaGasto(fechaTexto: String): LocalDate? {
    val valor = fechaTexto.trim()
    if (valor.isBlank()) return null

    val formatos = listOf(
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d-M-yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ISO_LOCAL_DATE
    )

    formatos.forEach { formato ->
        try {
            return LocalDate.parse(valor, formato)
        } catch (_: DateTimeParseException) {
            // Se intenta con el siguiente formato.
        }
    }

    return null
}

private fun formatearMonedaGasto(valor: Double): String {
    return String.format(Locale.US, "$%,.2f", valor)
}

private fun tamanoTextoMonto(texto: String) = when {
    texto.length >= 15 -> 11.sp
    texto.length >= 12 -> 12.sp
    texto.length >= 10 -> 13.sp
    else -> 14.sp
}
