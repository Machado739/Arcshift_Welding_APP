package com.example.arcshiftwelding.ui.Screen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.navigation.navigateBottomBar
import com.example.arcshiftwelding.security.SesionUsuarioStore
import com.example.arcshiftwelding.ui.Screen.notificaciones.CampanaNotificacionesPrincipal
import com.example.arcshiftwelding.ui.viewmodel.ClienteRecienteDashboardUi
import com.example.arcshiftwelding.ui.viewmodel.DashboardUiState
import com.example.arcshiftwelding.ui.viewmodel.DashboardViewModel
import com.example.arcshiftwelding.ui.viewmodel.DashboardViewModelFactory
import com.example.arcshiftwelding.ui.viewmodel.ProductoBajoStockDashboardUi
import java.text.NumberFormat
import java.time.LocalTime
import java.util.Locale
import kotlin.math.absoluteValue

@Composable
fun DashboardScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = remember {
        ArcshiftWeldingDatabase.getDatabase(context.applicationContext)
    }
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(database)
    )
    val estado by dashboardViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                .background(Color(0xFFF8FAFC))
        ) {
            HeaderDashboard(
                navController = navController
            )

            if (estado.cargando) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2563EB)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(9.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 14.dp)
            ) {
                item {
                    TarjetaBienvenida()
                }

                item {
                    TituloSeccion(
                        titulo = "Resumen del mes",
                        accion = estado.periodoTexto
                    )
                }

                item {
                    ResumenGeneral(estado)
                }

                item {
                    Text(
                        text = "Acciones rápidas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                item {
                    AccionesRapidas(
                        onNuevoIngreso = { navController.navigate(AppRoutes.NUEVO_INGRESO) },
                        onNuevoGasto = { navController.navigate(AppRoutes.NUEVO_GASTO) },
                        onNuevaCotizacion = { navController.navigate(AppRoutes.NUEVA_COTIZACION) },
                        onNuevoCliente = { navController.navigate(AppRoutes.NUEVO_CLIENTE) },
                        onVerInventario = {
                            navController.navigateBottomBar(AppRoutes.INVENTARIO)
                        }
                    )
                }

                item {
                    GraficaIngresosGastos(
                        ingresos = estado.ingresosUltimos7Dias,
                        gastos = estado.gastosUltimos7Dias,
                        etiquetas = estado.etiquetasUltimos7Dias,
                        totalIngresos = estado.ingresos,
                        totalGastos = estado.gastos
                    )
                }

                item {
                    CotizacionesEstado(
                        pendientes = estado.cotizacionesPendientes,
                        aprobadas = estado.cotizacionesAprobadas,
                        rechazadas = estado.cotizacionesRechazadas,
                        onClick = {
                            navController.navigateBottomBar(AppRoutes.COTIZACIONES)
                        }
                    )
                }

                item {
                    SeccionClientesInventarioDashboard(
                        clientes = estado.clientesRecientes,
                        productos = estado.productosBajoStock,
                        totalBajoStock = estado.cantidadProductosBajoStock,
                        onVerClientes = {
                            navController.navigateBottomBar(AppRoutes.CLIENTES)
                        },
                        onVerInventario = {
                            navController.navigateBottomBar(AppRoutes.INVENTARIO)
                        }
                    )
                }

                item {
                    ProximosCobros(
                        estado = estado,
                        onClick = {
                            navController.navigateBottomBar(AppRoutes.INGRESOS)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderDashboard(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        CampanaNotificacionesPrincipal(navController)

        val context = LocalContext.current
        IconButton(
            onClick = {
                SesionUsuarioStore(context.applicationContext).cerrarSesion()
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
private fun TarjetaBienvenida() {
    val saludo = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Buenos días"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F3D73))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(27.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ARCSHIFT WELDING",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "$saludo. Este es el estado actual del taller.",
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ResumenGeneral(estado: DashboardUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TarjetaResumen(
                titulo = "Ingresos",
                valor = moneda(estado.ingresos),
                detalle = textoVariacion(estado.variacionIngresos),
                icono = Icons.Default.AttachMoney,
                color = Color(0xFF16A34A),
                modifier = Modifier.weight(1f)
            )
            TarjetaResumen(
                titulo = "Gastos",
                valor = moneda(estado.gastos),
                detalle = textoVariacion(estado.variacionGastos),
                icono = Icons.Default.ShoppingBag,
                color = Color(0xFFDC2626),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TarjetaResumen(
                titulo = "Utilidad",
                valor = moneda(estado.utilidad),
                detalle = if (estado.utilidad >= 0) "Resultado positivo" else "Resultado negativo",
                icono = Icons.Default.BarChart,
                color = if (estado.utilidad >= 0) Color(0xFF2563EB) else Color(0xFFDC2626),
                modifier = Modifier.weight(1f)
            )
            TarjetaResumen(
                titulo = "Cotizaciones",
                valor = estado.cotizaciones.toString(),
                detalle = "${estado.cotizacionesPendientes} pendientes",
                icono = Icons.Default.Description,
                color = Color(0xFF7C3AED),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TarjetaResumen(
    titulo: String,
    valor: String,
    detalle: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(88.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    maxLines = 1
                )
                Text(
                    text = valor,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = tamanoMonto(valor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = detalle,
                    color = color,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AccionesRapidas(
    onNuevoIngreso: () -> Unit,
    onNuevoGasto: () -> Unit,
    onNuevaCotizacion: () -> Unit,
    onNuevoCliente: () -> Unit,
    onVerInventario: () -> Unit
) {
    val acciones = listOf(
        AccionRapida("Ingreso", Icons.Default.AttachMoney, Color(0xFF16A34A), onNuevoIngreso),
        AccionRapida("Gasto", Icons.Default.ShoppingBag, Color(0xFFDC2626), onNuevoGasto),
        AccionRapida("Cotizar", Icons.Default.Description, Color(0xFF2563EB), onNuevaCotizacion),
        AccionRapida("Cliente", Icons.Default.PersonAdd, Color(0xFF7C3AED), onNuevoCliente),
        AccionRapida("Inventario", Icons.Default.Inventory, Color(0xFFF59E0B), onVerInventario)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 340.dp) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 1.dp)
            ) {
                items(acciones, key = { it.texto }) { accion ->
                    TarjetaAccionRapidaCompacta(
                        accion = accion,
                        modifier = Modifier.width(78.dp)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                acciones.forEach { accion ->
                    TarjetaAccionRapidaCompacta(
                        accion = accion,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaAccionRapidaCompacta(
    accion: AccionRapida,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clickable { accion.onClick() },
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 3.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(27.dp)
                    .background(accion.color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = accion.icono,
                    contentDescription = null,
                    tint = accion.color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = accion.texto,
                fontWeight = FontWeight.SemiBold,
                fontSize = 8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private data class AccionRapida(
    val texto: String,
    val icono: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun GraficaIngresosGastos(
    ingresos: List<Float>,
    gastos: List<Float>,
    etiquetas: List<String>,
    totalIngresos: Double,
    totalGastos: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(196.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ingresos vs. gastos",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Últimos 7 días",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            GraficaLinealIngresosGastos(
                ingresos = ingresos,
                gastos = gastos,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (etiquetas.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    etiquetas.forEach { etiqueta ->
                        Text(
                            text = etiqueta,
                            color = Color(0xFF94A3B8),
                            fontSize = 8.sp,
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                IndicadorGrafica(
                    titulo = "Ingresos del mes",
                    valor = moneda(totalIngresos),
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
                IndicadorGrafica(
                    titulo = "Gastos del mes",
                    valor = moneda(totalGastos),
                    color = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun IndicadorGrafica(
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = titulo, color = Color(0xFF64748B), fontSize = 9.sp)
        Text(
            text = valor,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun GraficaLinealIngresosGastos(
    ingresos: List<Float>,
    gastos: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        val cantidad = minOf(ingresos.size, gastos.size)
        if (cantidad < 2) return@Canvas

        val datos = ingresos.take(cantidad) + gastos.take(cantidad)
        val maximo = (datos.maxOrNull() ?: 0f).coerceAtLeast(1f)
        val espacioX = size.width / (cantidad - 1)

        fun path(lista: List<Float>): Path {
            return Path().apply {
                lista.take(cantidad).forEachIndexed { index, valor ->
                    val x = index * espacioX
                    val y = size.height - (valor.coerceAtLeast(0f) / maximo * size.height)
                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                }
            }
        }

        drawPath(
            path = path(ingresos),
            color = Color(0xFF16A34A),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = path(gastos),
            color = Color(0xFFDC2626),
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun CotizacionesEstado(
    pendientes: Int,
    aprobadas: Int,
    rechazadas: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "Cotizaciones por estado",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val tamanoDona = if (maxWidth < 340.dp) 88.dp else 104.dp

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        EstadoCotizacion("Pendientes", pendientes, Color(0xFFF59E0B))
                        EstadoCotizacion("Aprobadas", aprobadas, Color(0xFF16A34A))
                        EstadoCotizacion("Rechazadas", rechazadas, Color(0xFFDC2626))
                    }

                    GraficaDonaCotizaciones(
                        pendientes = pendientes,
                        aprobadas = aprobadas,
                        rechazadas = rechazadas,
                        modifier = Modifier.size(tamanoDona)
                    )
                }
            }
        }
    }
}

@Composable
private fun GraficaDonaCotizaciones(
    pendientes: Int,
    aprobadas: Int,
    rechazadas: Int,
    modifier: Modifier = Modifier
) {
    val total = pendientes + aprobadas + rechazadas

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val grosor = 14.dp.toPx()
            val rect = Rect(
                left = grosor / 2,
                top = grosor / 2,
                right = size.width - grosor / 2,
                bottom = size.height - grosor / 2
            )

            if (total <= 0) {
                drawArc(
                    color = Color(0xFFE2E8F0),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = rect.topLeft,
                    size = rect.size,
                    style = Stroke(width = grosor)
                )
                return@Canvas
            }

            var inicio = -90f
            listOf(
                pendientes to Color(0xFFF59E0B),
                aprobadas to Color(0xFF16A34A),
                rechazadas to Color(0xFFDC2626)
            ).forEach { (cantidad, color) ->
                val angulo = 360f * cantidad / total
                if (angulo > 0f) {
                    drawArc(
                        color = color,
                        startAngle = inicio,
                        sweepAngle = angulo,
                        useCenter = false,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        style = Stroke(width = grosor, cap = StrokeCap.Butt)
                    )
                }
                inicio += angulo
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = total.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(text = "Mes", color = Color(0xFF64748B), fontSize = 9.sp)
        }
    }
}

@Composable
private fun EstadoCotizacion(texto: String, cantidad: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(7.dp))
        Text(text = texto, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text(
            text = cantidad.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun SeccionClientesInventarioDashboard(
    clientes: List<ClienteRecienteDashboardUi>,
    productos: List<ProductoBajoStockDashboardUi>,
    totalBajoStock: Int,
    onVerClientes: () -> Unit,
    onVerInventario: () -> Unit
) {
    val cantidadVisible = maxOf(clientes.size.coerceAtMost(4), productos.size.coerceAtMost(4))
    val altura = when (cantidadVisible) {
        0 -> 112.dp
        1 -> 128.dp
        2 -> 158.dp
        3 -> 188.dp
        else -> 218.dp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(altura),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ClientesRecientes(
            clientes = clientes,
            onVerTodos = onVerClientes,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
        InventarioBajoStock(
            productos = productos,
            totalBajoStock = totalBajoStock,
            onVerInventario = onVerInventario,
            modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
}

@Composable
private fun ClientesRecientes(
    clientes: List<ClienteRecienteDashboardUi>,
    onVerTodos: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            EncabezadoTarjetaLista(
                titulo = "Clientes recientes",
                onVerTodos = onVerTodos
            )
            Spacer(modifier = Modifier.height(7.dp))

            if (clientes.isEmpty()) {
                MensajeSinDatos("No hay clientes registrados.")
            } else {
                clientes.take(4).forEach { cliente ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(27.dp)
                                .background(Color(0xFFDBEAFE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cliente.nombre,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = cliente.detalle,
                                color = Color(0xFF64748B),
                                fontSize = 8.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InventarioBajoStock(
    productos: List<ProductoBajoStockDashboardUi>,
    totalBajoStock: Int,
    onVerInventario: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            EncabezadoTarjetaLista(
                titulo = "Stock bajo ($totalBajoStock)",
                onVerTodos = onVerInventario
            )
            Spacer(modifier = Modifier.height(7.dp))

            if (productos.isEmpty()) {
                MensajeSinDatos("No hay productos con stock bajo.")
            } else {
                productos.take(4).forEach { producto ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(27.dp)
                                .background(
                                    if (producto.agotado) Color(0xFFFEE2E2) else Color(0xFFFEF3C7),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (producto.agotado) Icons.Default.Warning else Icons.Default.Inventory,
                                contentDescription = null,
                                tint = if (producto.agotado) Color(0xFFDC2626) else Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = producto.nombre,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${producto.stock} ${producto.unidad} · mín. ${producto.stockMinimo}",
                                color = if (producto.agotado) Color(0xFFDC2626) else Color(0xFFF59E0B),
                                fontSize = 8.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EncabezadoTarjetaLista(
    titulo: String,
    onVerTodos: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Ver",
            color = Color(0xFF2563EB),
            fontSize = 9.sp,
            modifier = Modifier.clickable(onClick = onVerTodos)
        )
    }
}

@Composable
private fun MensajeSinDatos(texto: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = texto,
            color = Color(0xFF94A3B8),
            fontSize = 9.sp
        )
    }
}

@Composable
private fun ProximosCobros(
    estado: DashboardUiState,
    onClick: () -> Unit
) {
    val proximo = estado.proximoCobro
    val fondo = if (proximo?.vencido == true) Color(0xFFFFF1F2) else Color(0xFFEAF2FF)
    val color = if (proximo?.vencido == true) Color(0xFFDC2626) else Color(0xFF2563EB)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(color.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(21.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (proximo?.vencido == true) "Cobros vencidos" else "Próximos cobros",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = when {
                        estado.cantidadPagosPendientes == 0 -> "No hay pagos pendientes."
                        proximo == null -> "${estado.cantidadPagosPendientes} pagos por cobrar."
                        else -> "${proximo.descripcion} · ${proximo.fecha} · ${moneda(proximo.monto)}"
                    },
                    color = Color(0xFF64748B),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (estado.cantidadPagosPendientes > 0) {
                    Text(
                        text = "${estado.cantidadPagosPendientes} pendientes · Total ${moneda(estado.totalPorCobrar)}",
                        color = color,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = color
            )
        }
    }
}

@Composable
private fun TituloSeccion(titulo: String, accion: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFEFF6FF)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = accion,
                    color = Color(0xFF2563EB),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun moneda(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(valor)

private fun textoVariacion(valor: Double?): String {
    if (valor == null || valor.isNaN() || valor.isInfinite()) {
        return "Sin comparación anterior"
    }
    val signo = if (valor >= 0) "+" else "-"
    return "$signo${String.format(Locale("es", "MX"), "%.1f", valor.absoluteValue)}% vs. mes anterior"
}

private fun tamanoMonto(valor: String) = when {
    valor.length >= 15 -> 13.sp
    valor.length >= 12 -> 15.sp
    else -> 17.sp
}
