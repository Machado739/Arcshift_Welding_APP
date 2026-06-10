package com.example.arcshiftwelding.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.arcshiftwelding.TextoAutoAjustable
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import com.example.arcshiftwelding.navigation.AppRoutes
import kotlin.collections.take
import com.example.arcshiftwelding.navigation.navigateBottomBar


data class ProductoBajoStockDashboard(
    val nombre: String,
    val stock: String,
    val estado: String
)
data class ClienteDashboard(
    val nombre: String,
    val detalle: String,
    val color: Color
)
@Composable
fun DashboardScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color(0xFFF8FAFC))
        ) {
        HeaderDashboard(navController = navController)

        Spacer(modifier = Modifier.height(8.dp))

        TarjetaBienvenida()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                TituloSeccion(
                    titulo = "Resumen general",
                    accion = "19 May - 26 May 2026"
                )
            }

            item {
                ResumenGeneral()
            }

            item {
                Text(
                    text = "Acciones rápidas",
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                AccionesRapidas(
                    onNuevoIngreso = {
                        navController.navigateBottomBar(AppRoutes.INGRESOS)
                    },
                    onNuevoGasto = {
                        navController.navigateBottomBar(AppRoutes.NUEVO_GASTO)
                    },
                    onNuevaCotizacion = {
                        navController.navigateBottomBar(AppRoutes.COTIZACIONES)
                    },
                    onNuevoCliente = {
                        navController.navigateBottomBar(AppRoutes.NUEVO_CLIENTE)
                    },
                    onVerInventario = {
                        navController.navigateBottomBar(AppRoutes.INVENTARIO)
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GraficaIngresosGastos(
                        modifier = Modifier.weight(1f)
                    )

                    CotizacionesEstado(
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ClientesRecientes(
                        modifier = Modifier.weight(1f)
                    )

                    InventarioBajoStock(
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                ProximosCobros()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderDashboard(
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
            text = "Dashboard",
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
fun TarjetaBienvenida() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F3D73)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ARCSHIFT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "WELDING",
                    color = Color(0xFF60A5FA),
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "¡Buenos días, Jaime!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bienvenido a Arcshift Welding",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
@Composable
fun ResumenGeneral() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TarjetaResumen(
            titulo = "Ingresos",
            monto = "$24,000",
            porcentaje = "15% vs mes anterior",
            icono = Icons.Default.AttachMoney,
            color = Color(0xFF16A34A),
            datosGrafica = listOf(8f, 12f, 10f, 18f, 15f, 22f, 28f),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumen(
            titulo = "Gastos",
            monto = "$13,500",
            porcentaje = "8% vs mes anterior",
            icono = Icons.Default.ShoppingBag,
            color = Color(0xFFDC2626),
            datosGrafica = listOf(6f, 8f, 9f, 12f, 15f, 17f, 21f),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumen(
            titulo = "Utilidad",
            monto = "$10,500",
            porcentaje = "22% vs mes anterior",
            icono = Icons.Default.BarChart,
            color = Color(0xFF2563EB),
            datosGrafica = listOf(5f, 9f, 7f, 13f, 11f, 18f, 20f),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumen(
            titulo = "Cotizaciones",
            monto = "15",
            porcentaje = "7 pendientes",
            icono = Icons.Default.Description,
            color = Color(0xFF7C3AED),
            datosGrafica = listOf(2f, 4f, 3f, 6f, 5f, 8f, 9f),
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
fun TarjetaResumen(
    titulo: String,
    monto: String,
    porcentaje: String,
    icono: ImageVector,
    color: Color,
    datosGrafica: List<Float>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Text(
                text = monto,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            TextoAutoAjustable(
                text = porcentaje,
                color = Color.Gray,
                maxFontSize = 8.sp,
                minFontSize = 5.sp,
                modifier = Modifier.fillMaxWidth()
            )

            MiniGraficaResumen(
                datos = datosGrafica,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            )
        }
    }
}

@Composable
fun MiniGraficaResumen(
    datos: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
            .padding(4.dp)
    ) {
        if (datos.size < 2) return@Canvas

        val maxDato = datos.maxOrNull() ?: 1f
        val minDato = datos.minOrNull() ?: 0f
        val rango = (maxDato - minDato).takeIf { it != 0f } ?: 1f

        val espacioX = size.width / (datos.size - 1)

        val path = Path()

        datos.forEachIndexed { index, valor ->
            val x = index * espacioX
            val y = size.height - ((valor - minDato) / rango * size.height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        datos.forEachIndexed { index, valor ->
            val x = index * espacioX
            val y = size.height - ((valor - minDato) / rango * size.height)

            drawCircle(
                color = color,
                radius = 2.5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
@Composable
fun AccionesRapidas(
    onNuevoIngreso: () -> Unit,
    onNuevoGasto: () -> Unit,
    onNuevaCotizacion: () -> Unit,
    onNuevoCliente: () -> Unit,
    onVerInventario: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BotonAccionRapida(
            texto = "Nuevo ingreso",
            icono = Icons.Default.AttachMoney,
            color = Color(0xFF16A34A),
            onClick = onNuevoIngreso,
            modifier = Modifier.weight(1f)
        )

        BotonAccionRapida(
            texto = "Nuevo gasto",
            icono = Icons.Default.ShoppingBag,
            color = Color(0xFFEF4444),
            onClick = onNuevoGasto,
            modifier = Modifier.weight(1f)
        )

        BotonAccionRapida(
            texto = "Nueva cotización",
            icono = Icons.Default.Description,
            color = Color(0xFF2563EB),
            onClick = onNuevaCotizacion,
            modifier = Modifier.weight(1f)
        )

        BotonAccionRapida(
            texto = "Nuevo cliente",
            icono = Icons.Default.PersonAdd,
            color = Color(0xFF16A34A),
            onClick = onNuevoCliente,
            modifier = Modifier.weight(1f)
        )

        BotonAccionRapida(
            texto = "Ver inventario",
            icono = Icons.Default.Inventory,
            color = Color(0xFF7C3AED),
            onClick = onVerInventario,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BotonAccionRapida(
    texto: String,
    icono: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(82.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
@Composable
fun GraficaIngresosGastos(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(210.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Ingresos vs Gastos",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            GraficaLinealIngresosGastos(
                ingresos = listOf(3000f, 8000f, 14000f, 18000f, 17000f, 21000f, 24000f),
                gastos = listOf(2000f, 6000f, 9000f, 11000f, 12000f, 13500f, 15000f),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Total ingresos",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "$24,000",
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total gastos",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        text = "$13,500",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
fun GraficaLinealIngresosGastos(
    ingresos: List<Float>,
    gastos: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        val todosLosDatos = ingresos + gastos

        if (ingresos.size < 2 || gastos.size < 2) return@Canvas

        val maxDato = todosLosDatos.maxOrNull() ?: 1f
        val minDato = todosLosDatos.minOrNull() ?: 0f
        val rango = (maxDato - minDato).takeIf { it != 0f } ?: 1f

        val ancho = size.width
        val alto = size.height

        fun crearPath(datos: List<Float>): Path {
            val path = Path()
            val espacioX = ancho / (datos.size - 1)

            datos.forEachIndexed { index, valor ->
                val x = index * espacioX
                val y = alto - ((valor - minDato) / rango * alto)

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            return path
        }

        drawPath(
            path = crearPath(ingresos),
            color = Color(0xFF16A34A),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawPath(
            path = crearPath(gastos),
            color = Color(0xFFDC2626),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        ingresos.forEachIndexed { index, valor ->
            val espacioX = ancho / (ingresos.size - 1)
            val x = index * espacioX
            val y = alto - ((valor - minDato) / rango * alto)

            drawCircle(
                color = Color(0xFF16A34A),
                radius = 3.dp.toPx(),
                center = Offset(x, y)
            )
        }

        gastos.forEachIndexed { index, valor ->
            val espacioX = ancho / (gastos.size - 1)
            val x = index * espacioX
            val y = alto - ((valor - minDato) / rango * alto)

            drawCircle(
                color = Color(0xFFDC2626),
                radius = 3.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}@Composable
fun CotizacionesEstado(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(210.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Cotizaciones por estado",
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            GraficaDonaCotizaciones(
                pendientes = 7,
                aceptadas = 5,
                rechazadas = 3,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            EstadoCotizacion("Pendientes", "7", Color(0xFFF59E0B))
            EstadoCotizacion("Aceptadas", "5", Color(0xFF16A34A))
            EstadoCotizacion("Rechazadas", "3", Color(0xFFDC2626))
        }
    }
}
@Composable
fun GraficaDonaCotizaciones(
    pendientes: Int,
    aceptadas: Int,
    rechazadas: Int,
    modifier: Modifier = Modifier
) {
    val total = pendientes + aceptadas + rechazadas

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            if (total <= 0) return@Canvas

            val strokeWidth = 16.dp.toPx()

            val rect = Rect(
                left = strokeWidth / 2,
                top = strokeWidth / 2,
                right = size.width - strokeWidth / 2,
                bottom = size.height - strokeWidth / 2
            )

            var anguloInicio = -90f

            val anguloPendientes = 360f * pendientes / total
            val anguloAceptadas = 360f * aceptadas / total
            val anguloRechazadas = 360f * rechazadas / total

            drawArc(
                color = Color(0xFFF59E0B),
                startAngle = anguloInicio,
                sweepAngle = anguloPendientes,
                useCenter = false,
                topLeft = rect.topLeft,
                size = rect.size,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Butt
                )
            )

            anguloInicio += anguloPendientes

            drawArc(
                color = Color(0xFF16A34A),
                startAngle = anguloInicio,
                sweepAngle = anguloAceptadas,
                useCenter = false,
                topLeft = rect.topLeft,
                size = rect.size,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Butt
                )
            )

            anguloInicio += anguloAceptadas

            drawArc(
                color = Color(0xFFDC2626),
                startAngle = anguloInicio,
                sweepAngle = anguloRechazadas,
                useCenter = false,
                topLeft = rect.topLeft,
                size = rect.size,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Butt
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = total.toString(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Total",
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
@Composable
fun EstadoCotizacion(
    texto: String,
    cantidad: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = cantidad,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun ClientesRecientes(
    modifier: Modifier = Modifier,
    clientes: List<ClienteDashboard> = listOf(
        ClienteDashboard("Eduardo Barrios", "2 cotizaciones", Color(0xFF16A34A)),
        ClienteDashboard("José Vera", "1 cotización", Color(0xFF2563EB)),
        ClienteDashboard("María López", "1 cotización", Color(0xFF7C3AED)),
        ClienteDashboard("Carlos Ruiz", "3 cotizaciones", Color(0xFFF59E0B))
    ),
    onVerTodos: () -> Unit = {}
) {
    Card(
        modifier = modifier.height(230.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Clientes recientes",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Ver todos",
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            clientes.take(3).forEach { cliente ->
                ClienteItem(
                    nombre = cliente.nombre,
                    detalle = cliente.detalle,
                    color = cliente.color
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onVerTodos,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEAF2FF),
                    contentColor = Color(0xFF2563EB)
                )
            ) {
                Text(
                    text = "Ver todos los clientes",
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ClienteItem(
    nombre: String,
    detalle: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = nombre,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                text = detalle,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun InventarioBajoStock(
    modifier: Modifier = Modifier,
    productos: List<ProductoBajoStockDashboard> = listOf(
        ProductoBajoStockDashboard("PTR 2\" x 2\"", "Stock: 10", "Bajo"),
        ProductoBajoStockDashboard("Tubo 1 1/2\"", "Stock: 8", "Bajo"),
        ProductoBajoStockDashboard("Placa 1/4\"", "Stock: 5", "Bajo"),
        ProductoBajoStockDashboard("Soldadura 6013", "Stock: 3", "Bajo")
    ),
    onVerInventario: () -> Unit = {}
) {
    Card(
        modifier = modifier.height(230.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Inventario con bajo stock",
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Ver todos",
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            productos.take(3).forEach { producto ->
                ProductoStockItem(
                    nombre = producto.nombre,
                    stock = producto.stock,
                    estado = producto.estado
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onVerInventario,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEAF2FF),
                    contentColor = Color(0xFF2563EB)
                )
            ) {
                Text(
                    text = "Ver inventario completo",
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
@Composable
fun ProductoStockItem(
    nombre: String,
    stock: String,
    estado: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = nombre,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stock,
                color = Color(0xFFF59E0B),
                fontSize = 9.sp,
                maxLines = 1
            )
        }

        Text(
            text = estado,
            color = Color(0xFFDC2626),
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            maxLines = 1
        )
    }
}

@Composable
fun ProximosCobros() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEAF2FF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Próximos cobros",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Tienes 3 cobros pendientes por cobrar",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
fun TituloSeccion(
    titulo: String,
    accion: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        AssistChip(
            onClick = { },
            label = {
                Text(
                    text = accion,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}

