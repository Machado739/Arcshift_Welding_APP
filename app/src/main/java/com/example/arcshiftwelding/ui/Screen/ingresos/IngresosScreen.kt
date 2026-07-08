package com.example.arcshiftwelding.ui.Screen.ingresos

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


@Composable
fun IngresosScreen(
    navController: NavController,
    viewModel: IngresosViewModel
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var busqueda by remember { mutableStateOf("") }

    val ingresos by viewModel.ingresos.collectAsState()
    val pagosPorCobrar by viewModel.pagosPorCobrar.collectAsState()

    val ingresosFiltrados = ingresos.filter { ingreso ->
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

    val pagosPorCobrarFiltrados = pagosPorCobrar.filter { pago ->
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
        HeaderIngresos(navController = navController)

        Spacer(modifier = Modifier.height(8.dp))

        ResumenIngresos(
            ingresos = ingresos,
            pagosPorCobrar = pagosPorCobrar
        )

        Spacer(modifier = Modifier.height(12.dp))

        BarraBusquedaIngresos(
            busqueda = busqueda,
            onBusquedaChange = { busqueda = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate(AppRoutes.NUEVO_INGRESO)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nuevo Ingreso")
        }

        Spacer(modifier = Modifier.height(10.dp))

        FiltrosCategoriaIngresos(
            seleccionada = categoriaSeleccionada,
            onSeleccionar = {
                categoriaSeleccionada = it
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

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
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(onClick = { }) {
            Icon(Icons.Default.Menu, contentDescription = "Menú")
        }

        Text(
            text = "Ingreso",
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
fun ResumenIngresos(
    ingresos: List<IngresoUI>,
    pagosPorCobrar: List<PagoPorCobrarUI>
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Recibido",
            monto = totalRecibido.formatoDinero(),
            subtitulo = "Total",
            icono = Icons.Default.AttachMoney,
            color = Color(0xFF2563EB),
            fondo = Color(0xFFEFF6FF)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Pagos",
            monto = totalPagos.formatoDinero(),
            subtitulo = "Directos",
            icono = Icons.Default.CheckCircle,
            color = Color(0xFF16A34A),
            fondo = Color(0xFFF0FDF4)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Anticipos",
            monto = totalAnticipos.formatoDinero(),
            subtitulo = "Iniciales",
            icono = Icons.Default.Savings,
            color = Color(0xFFF59E0B),
            fondo = Color(0xFFFFFBEB)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Por cobrar",
            monto = totalPorCobrar.formatoDinero(),
            subtitulo = "Programado",
            icono = Icons.Default.Schedule,
            color = Color(0xFF7C3AED),
            fondo = Color(0xFFF5F3FF)
        )
    }
}

@Composable
fun CardResumenIngreso(
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
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
fun BarraBusquedaIngresos(
    busqueda: String,
    onBusquedaChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    text = "Buscar ingreso..."
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
                    color = Color.DarkGray,
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
                    color = Color.DarkGray,
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
                        color = Color.Black,
                        maxLines = 1
                    )

                    Text(
                        text = "Cliente: ${ingreso.cliente}",
                        fontSize = 10.sp,
                        color = Color.DarkGray,
                        maxLines = 1
                    )

                    if (ingreso.concepto.isNotBlank()) {
                        Text(
                            text = ingreso.concepto,
                            fontSize = 9.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }

                EstadoChipIngreso(
                    texto = ingreso.categoria
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DatosIngreso(
                    titulo = if (esAnticipo) "Anticipo" else "Recibido",
                    valor = ingreso.total,
                    color = if (esAnticipo) Color(0xFFF59E0B) else Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )

                if (tieneProyecto) {
                    DatosIngreso(
                        titulo = "Total proyecto",
                        valor = ingreso.montoTotalProyecto,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )

                    DatosIngreso(
                        titulo = "Pendiente",
                        valor = ingreso.pendiente,
                        color = if (tienePendiente) Color(0xFFF97316) else Color(0xFF16A34A),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    DatosIngreso(
                        titulo = "Método",
                        valor = ingreso.metodoPago.ifBlank { "N/A" },
                        color = Color(0xFF374151),
                        modifier = Modifier.weight(1f)
                    )

                    DatosIngreso(
                        titulo = "Fecha",
                        valor = ingreso.fecha,
                        color = Color(0xFF374151),
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
                        color = Color(0xFF2563EB),
                        modifier = Modifier
                            .background(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                }

                Text(
                    text = ingreso.metodoPago.ifBlank { "Sin método" },
                    fontSize = 8.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = ingreso.fecha,
                    fontSize = 8.sp,
                    color = Color.Gray
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
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = Color(0xFFF5F3FF),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF7C3AED),
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
                        color = Color.Black,
                        maxLines = 1
                    )

                    Text(
                        text = "Cliente: ${pago.cliente}",
                        fontSize = 9.sp,
                        color = Color.DarkGray,
                        maxLines = 1
                    )

                    Text(
                        text = "Proyecto: ${pago.proyecto}",
                        fontSize = 8.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Próximo pago",
                        fontSize = 8.sp,
                        color = Color(0xFF7C3AED),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color = Color(0xFFF5F3FF),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = pago.monto,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )

                    Text(
                        text = pago.fechaProgramada,
                        fontSize = 8.sp,
                        color = Color.Gray
                    )
                }
            }

            if (tieneMasPagos) {
                Spacer(modifier = Modifier.height(8.dp))

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
                            .padding(horizontal = 9.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(15.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${pago.cantidadPagosPosteriores} pagos posteriores programados",
                                fontSize = 8.sp,
                                color = Color(0xFF475569),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )

                            Text(
                                text = "Total posterior: ${pago.totalPagosPosteriores}",
                                fontSize = 8.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }

                        Text(
                            text = "Total: ${pago.totalPendienteProgramado}",
                            fontSize = 8.sp,
                            color = Color(0xFF7C3AED),
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
        "Anticipos" -> Color(0xFF16A34A)
        "Por cobrar" -> Color(0xFF7C3AED)
        "Pagos" -> Color(0xFF15803D)
        "Cobros" -> Color(0xFF2563EB)
        "Transferencias" -> Color(0xFF7C3AED)
        "Efectivos" -> Color(0xFF0891B2)
        "Tarjetas" -> Color(0xFFDB2777)
        else -> Color(0xFF64748B)
    }

    val fondo = when (categoria) {
        "Anticipos" -> Color(0xFFDCFCE7)
        "Por cobrar" -> Color(0xFFF5F3FF)
        "Pagos" -> Color(0xFFD1FAE5)
        "Cobros" -> Color(0xFFDBEAFE)
        "Transferencias" -> Color(0xFFEDE9FE)
        "Efectivos" -> Color(0xFFCFFAFE)
        "Tarjetas" -> Color(0xFFFCE7F3)
        else -> Color(0xFFF1F5F9)
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
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            fontSize = 8.sp,
            color = Color.Gray,
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
        "Anticipos" -> Color(0xFFF59E0B)
        "Pendientes" -> Color(0xFFF97316)
        "Pagos" -> Color(0xFF16A34A)
        else -> Color(0xFF64748B)
    }

    val fondo = when (texto) {
        "Anticipos" -> Color(0xFFFFF7E6)
        "Pendientes" -> Color(0xFFFFEDD5)
        "Pagos" -> Color(0xFFEAF7EE)
        else -> Color(0xFFF1F5F9)
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