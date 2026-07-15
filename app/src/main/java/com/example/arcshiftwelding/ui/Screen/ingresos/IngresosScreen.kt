package com.example.arcshiftwelding.ui.Screen.ingresos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.arcshiftwelding.ui.viewmodel.IngresoUI
import com.example.arcshiftwelding.ui.viewmodel.IngresosViewModel
import com.example.arcshiftwelding.ui.viewmodel.PagoPorCobrarUI
import com.example.arcshiftwelding.ui.viewmodel.formatoDinero
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import com.example.arcshiftwelding.ui.theme.arcshiftColors


enum class PeriodoIngresos(val etiqueta: String) {
    TODO("Todo"),
    HOY("Hoy"),
    SEMANA("Semana"),
    MES("Mes"),
    ANIO("Año")
}

private val formatosFechaIngresos = listOf(
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ISO_LOCAL_DATE
)

private fun convertirFechaIngreso(valor: String): LocalDate? {
    val fechaLimpia = valor
        .trim()
        .substringBefore(" ")
        .substringBefore("T")

    if (fechaLimpia.isBlank() || fechaLimpia.equals("Sin fecha", ignoreCase = true)) {
        return null
    }

    formatosFechaIngresos.forEach { formato ->
        runCatching {
            LocalDate.parse(fechaLimpia, formato)
        }.getOrNull()?.let { fecha ->
            return fecha
        }
    }

    return null
}

private fun fechaPerteneceAlPeriodo(
    fechaTexto: String,
    periodo: PeriodoIngresos,
    hoy: LocalDate = LocalDate.now()
): Boolean {
    if (periodo == PeriodoIngresos.TODO) return true

    val fecha = convertirFechaIngreso(fechaTexto) ?: return false

    return when (periodo) {
        PeriodoIngresos.TODO -> true
        PeriodoIngresos.HOY -> fecha == hoy
        PeriodoIngresos.SEMANA -> {
            val inicioSemana = hoy.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
            )
            val finSemana = hoy.with(
                TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)
            )

            !fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana)
        }

        PeriodoIngresos.MES ->
            fecha.year == hoy.year && fecha.month == hoy.month

        PeriodoIngresos.ANIO -> fecha.year == hoy.year
    }
}

@Composable
fun FiltroPeriodoIngresos(
    seleccionado: PeriodoIngresos,
    onSeleccionar: (PeriodoIngresos) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(29.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PeriodoIngresos.values().forEach { periodo ->
            val estaSeleccionado = seleccionado == periodo

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSeleccionar(periodo) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = periodo.etiqueta,
                    fontSize = 10.sp,
                    fontWeight = if (estaSeleccionado) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Normal
                    },
                    color = if (estaSeleccionado) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(3.dp))

                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(2.dp)
                        .background(
                            color = if (estaSeleccionado) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun IngresosScreen(
    navController: NavController,
    viewModel: IngresosViewModel
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var periodoSeleccionado by remember { mutableStateOf(PeriodoIngresos.TODO) }
    var resumenExpandido by rememberSaveable { mutableStateOf(false) }
    var busqueda by remember { mutableStateOf("") }

    val ingresos by viewModel.ingresos.collectAsState()
    val pagosPorCobrar by viewModel.pagosPorCobrar.collectAsState()
    val hoy = LocalDate.now()

    val ingresosPorPeriodo = remember(ingresos, periodoSeleccionado, hoy) {
        ingresos.filter { ingreso ->
            fechaPerteneceAlPeriodo(
                fechaTexto = ingreso.fecha,
                periodo = periodoSeleccionado,
                hoy = hoy
            )
        }
    }

    val pagosPorCobrarPorPeriodo = remember(
        pagosPorCobrar,
        periodoSeleccionado,
        hoy
    ) {
        pagosPorCobrar.filter { pago ->
            fechaPerteneceAlPeriodo(
                fechaTexto = pago.fechaProgramada,
                periodo = periodoSeleccionado,
                hoy = hoy
            )
        }
    }

    val ingresosFiltrados = remember(
        ingresosPorPeriodo,
        categoriaSeleccionada,
        busqueda
    ) {
        ingresosPorPeriodo.filter { ingreso ->
            val coincideCategoria =
                categoriaSeleccionada == "Todos" ||
                        categoriaSeleccionada == "Pagos" && ingreso.categoria == "Pagos" ||
                        categoriaSeleccionada == "Anticipos" && ingreso.categoria == "Anticipos"

            val coincideBusqueda =
                ingreso.cliente.contains(busqueda, ignoreCase = true) ||
                        ingreso.trabajo.contains(busqueda, ignoreCase = true) ||
                        ingreso.proyecto.contains(busqueda, ignoreCase = true) ||
                        ingreso.concepto.contains(busqueda, ignoreCase = true) ||
                        ingreso.folio.contains(busqueda, ignoreCase = true) ||
                        ingreso.metodoPago.contains(busqueda, ignoreCase = true)

            coincideCategoria && coincideBusqueda
        }
    }

    val pagosPorCobrarFiltrados = remember(
        pagosPorCobrarPorPeriodo,
        categoriaSeleccionada,
        busqueda
    ) {
        pagosPorCobrarPorPeriodo.filter { pago ->
            val coincideCategoria =
                categoriaSeleccionada == "Todos" ||
                        categoriaSeleccionada == "Por cobrar"

            val coincideBusqueda =
                pago.cliente.contains(busqueda, ignoreCase = true) ||
                        pago.trabajo.contains(busqueda, ignoreCase = true) ||
                        pago.proyecto.contains(busqueda, ignoreCase = true) ||
                        pago.fechaProgramada.contains(busqueda, ignoreCase = true) ||
                        pago.observaciones.contains(busqueda, ignoreCase = true)

            coincideCategoria && coincideBusqueda
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = 8.dp,
                top = 0.dp,
                end = 8.dp,
                bottom = 8.dp
            )
    ) {
        HeaderIngresos(navController = navController)

        Spacer(modifier = Modifier.height(4.dp))

        FiltroPeriodoIngresos(
            seleccionado = periodoSeleccionado,
            onSeleccionar = { periodoSeleccionado = it }
        )

        Spacer(modifier = Modifier.height(5.dp))

        ResumenIngresos(
            ingresos = ingresosPorPeriodo,
            pagosPorCobrar = pagosPorCobrarPorPeriodo,
            expandido = resumenExpandido,
            onCambiarExpandido = { resumenExpandido = !resumenExpandido }
        )

        Spacer(modifier = Modifier.height(6.dp))

        BarraBusquedaIngresos(
            busqueda = busqueda,
            onBusquedaChange = { busqueda = it },
            onNuevoIngreso = {
                navController.navigate(AppRoutes.NUEVO_INGRESO)
            }
        )

        Spacer(modifier = Modifier.height(5.dp))

        FiltrosCategoriaIngresos(
            seleccionada = categoriaSeleccionada,
            onSeleccionar = {
                categoriaSeleccionada = it
            }
        )

        Spacer(modifier = Modifier.height(5.dp))

        when (categoriaSeleccionada) {
            "Por cobrar" -> {
                ListadoPagosPorCobrar(
                    pagos = pagosPorCobrarFiltrados,
                    onClickPago = { pago ->
                        pago.ingresoAnticipoId?.let { ingresoId ->
                            navController.navigate(AppRoutes.detalleIngreso(ingresoId))
                        }
                    }
                )
            }

            "Todos" -> {
                ListadoIngresosYPagosPorCobrar(
                    ingresos = ingresosFiltrados,
                    pagos = pagosPorCobrarFiltrados,
                    onClickIngreso = { ingreso ->
                        navController.navigate(AppRoutes.detalleIngreso(ingreso.id))
                    },
                    onClickPago = { pago ->
                        pago.ingresoAnticipoId?.let { ingresoId ->
                            navController.navigate(AppRoutes.detalleIngreso(ingresoId))
                        }
                    }
                )
            }

            else -> {
                ListadoIngresos(
                    ingresos = ingresosFiltrados,
                    onClickIngreso = { ingreso ->
                        navController.navigate(AppRoutes.detalleIngreso(ingreso.id))
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderIngresos(
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
    ){


        Text(
            text = "Ingreso",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        CampanaNotificacionesPrincipal(navController)
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
fun ResumenIngresos(
    ingresos: List<IngresoUI>,
    pagosPorCobrar: List<PagoPorCobrarUI>,
    expandido: Boolean,
    onCambiarExpandido: () -> Unit
) {
    val totalRecibido = ingresos.sumOf { it.totalNumero }
    val totalPagos = ingresos
        .filter { it.categoria == "Pagos" }
        .sumOf { it.totalNumero }
    val totalAnticipos = ingresos
        .filter { it.categoria == "Anticipos" }
        .sumOf { it.totalNumero }
    val totalPorCobrar = pagosPorCobrar.sumOf {
        it.totalPendienteProgramadoNumero
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onCambiarExpandido() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 11.dp, vertical = 9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResumenPrincipalIngreso(
                    titulo = "Recibido",
                    monto = totalRecibido.formatoDinero(),
                    icono = Icons.Default.AttachMoney,
                    color = MaterialTheme.colorScheme.primary,
                    fondo = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(1.dp)
                        .height(34.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                ResumenPrincipalIngreso(
                    titulo = "Por cobrar",
                    monto = totalPorCobrar.formatoDinero(),
                    icono = Icons.Default.Schedule,
                    color = MaterialTheme.colorScheme.secondary,
                    fondo = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expandido) {
                        Icons.Default.ExpandLess
                    } else {
                        Icons.Default.ExpandMore
                    },
                    contentDescription = if (expandido) {
                        "Contraer resumen"
                    } else {
                        "Desplegar resumen"
                    },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(visible = expandido) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(top = 9.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ResumenSecundarioIngreso(
                            titulo = "Pagos",
                            monto = totalPagos.formatoDinero(),
                            subtitulo = "Directos",
                            icono = Icons.Default.CheckCircle,
                            color = MaterialTheme.arcshiftColors.success,
                            fondo = MaterialTheme.arcshiftColors.successContainer,
                            modifier = Modifier.weight(1f)
                        )

                        ResumenSecundarioIngreso(
                            titulo = "Anticipos",
                            monto = totalAnticipos.formatoDinero(),
                            subtitulo = "Iniciales",
                            icono = Icons.Default.Savings,
                            color = MaterialTheme.arcshiftColors.warning,
                            fondo = MaterialTheme.arcshiftColors.warningContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenPrincipalIngreso(
    titulo: String,
    monto: String,
    icono: ImageVector,
    color: Color,
    fondo: Color,
    modifier: Modifier = Modifier
) {
    val tamanoMonto = when {
        monto.length >= 17 -> 11.sp
        monto.length >= 14 -> 12.sp
        monto.length >= 11 -> 13.sp
        else -> 15.sp
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(fondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = monto,
                fontSize = tamanoMonto,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ResumenSecundarioIngreso(
    titulo: String,
    monto: String,
    subtitulo: String,
    icono: ImageVector,
    color: Color,
    fondo: Color,
    modifier: Modifier = Modifier
) {
    val tamanoMonto = when {
        monto.length >= 17 -> 10.sp
        monto.length >= 14 -> 11.sp
        else -> 12.sp
    }

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(9.dp))
            .padding(horizontal = 9.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(fondo, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(7.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = monto,
                fontSize = tamanoMonto,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitulo,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun BarraBusquedaIngresos(
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    onNuevoIngreso: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            placeholder = {
                Text(
                    text = "Buscar ingreso...",
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        FilledIconButton(
            onClick = onNuevoIngreso,
            modifier = Modifier.size(46.dp),
            shape = RoundedCornerShape(10.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Nuevo ingreso",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun FiltrosCategoriaIngresos(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf(
        "Todos",
        "Pagos",
        "Anticipos",
        "Por cobrar",
        "Más"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias){ categoria ->
            CategoriaChip(
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
fun CategoriaChip(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(32.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (seleccionado) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = texto,
                fontSize = 11.sp,
                fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal,
                color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ListadoIngresos(
    ingresos: List<IngresoUI>,
    onClickIngreso: (IngresoUI) -> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
            .padding(
                start = 0.dp,
                top = 0.dp,
                end = 0.dp,
                bottom = 8.dp
            )
    ) {
        items(ingresos) { ingreso ->
            ItemIngreso(
                ingreso = ingreso,
                onClick = {
                    onClickIngreso(ingreso)
                }
            )

        }
    }

}

@Composable
fun ListadoIngresosYPagosPorCobrar(
    ingresos: List<IngresoUI>,
    pagos: List<PagoPorCobrarUI>,
    onClickIngreso: (IngresoUI) -> Unit,
    onClickPago: (PagoPorCobrarUI) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        if (ingresos.isNotEmpty()) {
            item {
                Text(
                    text = "Ingresos recibidos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            items(ingresos) { ingreso ->
                ItemIngreso(
                    ingreso = ingreso,
                    onClick = { onClickIngreso(ingreso) }
                )
            }
        }

        if (pagos.isNotEmpty()) {
            item {
                Text(
                    text = "Por cobrar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                )
            }

            items(pagos) { pago ->
                ItemPagoPorCobrar(
                    pago = pago,
                    onClick = { onClickPago(pago) }
                )
            }
        }
    }
}

@Composable
fun ListadoPagosPorCobrar(
    pagos: List<PagoPorCobrarUI>,
    onClickPago: (PagoPorCobrarUI) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        items(pagos) { pago ->
            ItemPagoPorCobrar(
                pago = pago,
                onClick = { onClickPago(pago) }
            )
        }
    }
}

@Composable
fun ItemIngreso(
    ingreso: IngresoUI,
    onClick: () -> Unit
) {
    val esAnticipo = ingreso.formaPago == "Anticipo"
    val tieneProyecto = ingreso.proyectoId != null || ingreso.proyecto.isNotBlank()
    val tienePendiente = ingreso.pendienteNumero > 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconoCategoriaIngreso(ingreso.categoria)

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = ingreso.trabajo.ifBlank { "Sin trabajo" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )

                    Text(
                        text = "Cliente: ${ingreso.cliente}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )

                    if (ingreso.concepto.isNotBlank()) {
                        Text(
                            text = ingreso.concepto,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }

                EstadoChipIngreso(
                    texto = ingreso.categoria
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DatosIngreso(
                    titulo = if (esAnticipo) "Anticipo" else "Recibido",
                    valor = ingreso.total,
                    color = if (esAnticipo) MaterialTheme.arcshiftColors.warning else MaterialTheme.arcshiftColors.success,
                    modifier = Modifier.weight(1f)
                )

                if (tieneProyecto) {
                    DatosIngreso(
                        titulo = "Total proyecto",
                        valor = ingreso.montoTotalProyecto,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    DatosIngreso(
                        titulo = "Pendiente",
                        valor = ingreso.pendiente,
                        color = if (tienePendiente) MaterialTheme.arcshiftColors.warning else MaterialTheme.arcshiftColors.success,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    DatosIngreso(
                        titulo = "Método",
                        valor = ingreso.metodoPago.ifBlank { "N/A" },
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    DatosIngreso(
                        titulo = "Fecha",
                        valor = ingreso.fecha,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val referenciaCotizacion = ingreso.cotizacion
                    .takeIf {
                        it.isNotBlank() && it != "Sin cotización"
                    }

                if (referenciaCotizacion != null) {
                    Text(
                        text = referenciaCotizacion,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = ingreso.metodoPago.ifBlank { "Sin método" },
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = ingreso.fecha,
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun ItemPagoPorCobrar(
    pago: PagoPorCobrarUI,
    onClick: () -> Unit
) {
    val tieneMasPagos = pago.cantidadPagosPosteriores > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = pago.trabajo,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )

                    Text(
                        text = "Cliente: ${pago.cliente}",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )

                    Text(
                        text = "Proyecto: ${pago.proyecto}",
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Próximo pago",
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = pago.monto,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = pago.fechaProgramada,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (tieneMasPagos) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 9.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${pago.cantidadPagosPosteriores} pagos posteriores programados",
                                fontSize = 8.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )

                            Text(
                                text = "Total posterior: ${pago.totalPagosPosteriores}",
                                fontSize = 8.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }

                        Text(
                            text = "Total: ${pago.totalPendienteProgramado}",
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconoCategoriaIngreso(
    categoria: String
) {
    val icono = when (categoria) {
        "Anticipos" -> Icons.Default.Savings
        "Por cobrar" -> Icons.Default.Schedule
        "Pagos" -> Icons.Default.CheckCircle
        "Cobros" -> Icons.Default.AttachMoney
        "Transferencias" -> Icons.Default.AccountBalance
        "Efectivos" -> Icons.Default.Payments
        "Tarjetas" -> Icons.Default.CreditCard
        else -> Icons.Default.ReceiptLong
    }

    val color = when (categoria) {
        "Anticipos" -> MaterialTheme.arcshiftColors.success
        "Por cobrar" -> MaterialTheme.colorScheme.secondary
        "Pagos" -> MaterialTheme.arcshiftColors.success
        "Cobros" -> MaterialTheme.colorScheme.primary
        "Transferencias" -> MaterialTheme.colorScheme.secondary
        "Efectivos" -> MaterialTheme.arcshiftColors.info
        "Tarjetas" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val fondo = when (categoria) {
        "Anticipos" -> MaterialTheme.arcshiftColors.successContainer
        "Por cobrar" -> MaterialTheme.colorScheme.secondaryContainer
        "Pagos" -> MaterialTheme.arcshiftColors.successContainer
        "Cobros" -> MaterialTheme.colorScheme.primaryContainer
        "Transferencias" -> MaterialTheme.colorScheme.secondaryContainer
        "Efectivos" -> MaterialTheme.arcshiftColors.infoContainer
        "Tarjetas" -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
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
            contentDescription = categoria,
            tint = color,
            modifier = Modifier.size(23.dp)
        )
    }
}

@Composable
fun DatosIngreso(
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )

        Text(
            text = valor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
fun EstadoChipIngreso(
    texto: String
) {
    val color = when (texto) {
        "Anticipos" -> MaterialTheme.arcshiftColors.warning
        "Pendientes" -> MaterialTheme.arcshiftColors.warning
        "Pagos" -> MaterialTheme.arcshiftColors.success
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val fondo = when (texto) {
        "Anticipos" -> MaterialTheme.arcshiftColors.warningContainer
        "Pendientes" -> MaterialTheme.arcshiftColors.warningContainer
        "Pagos" -> MaterialTheme.arcshiftColors.successContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Text(
        text = texto,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier
            .background(
                color = fondo,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 7.dp, vertical = 4.dp)
    )
}