package com.example.arcshiftwelding.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class DetalleReporteUI(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val color: Color,
    val totalPrincipal: String,
    val tituloTotalPrincipal: String,
    val totalSecundario: String,
    val tituloTotalSecundario: String
)

data class RegistroReporteUI(
    val titulo: String,
    val descripcion: String,
    val monto: String,
    val fecha: String,
    val color: Color
)

@Composable
fun DetalleReporteScreen(
    navController: NavController,
    tipoReporte: String
) {
    val reporte = obtenerDetalleReporte(tipoReporte)
    val registros = obtenerRegistrosReporte(tipoReporte, reporte.color)

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    start = 8.dp,
                    top = 0.dp,
                    end = 8.dp,
                    bottom = 8.dp
                )
                .background(Color(0xFFF8FAFC))
        ) {
            HeaderDetalleReporte(
                titulo = reporte.titulo,
                navController = navController
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    TarjetaPrincipalDetalleReporte(
                        reporte = reporte,
                        tipoReporte =  tipoReporte
                    )
                }

                item {
                    ResumenDetalleReporte(
                        reporte = reporte
                    )
                }

                item {
                    SeccionPeriodoReporte()
                }

                item {
                    Text(
                        text = obtenerTituloResumenReporte(tipoReporte),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    when (tipoReporte.lowercase()) {
                        "ingresos" -> {
                            SeccionResumenFinancieroIngresos()
                        }

                        "inventario" -> {
                            SeccionResumenInventario()
                        }

                        else -> {
                            DatosReporte(
                                tipoReporte = tipoReporte
                            )
                        }
                    }
                }

                if (tipoReporte.lowercase() == "ingresos") {
                    item {
                        Text(
                            text = "Métodos de pago",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    item {
                        SeccionMetodosPagoReporteIngresos()
                    }
                }

                if (tipoReporte.lowercase() == "inventario") {
                    item {
                        Text(
                            text = "Estado del inventario",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    item {
                        SeccionEstadoInventarioReporte()
                    }
                }

                item {
                    Text(
                        text = "Registros recientes",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(registros.size) { index ->
                    ItemRegistroReporte(
                        registro = registros[index]
                    )
                }

                item {
                    SeccionAccionesDetalleReporte()
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun HeaderDetalleReporte(
    titulo: String,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
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
            text = titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones"
            )
        }
    }
}

@Composable
fun TarjetaPrincipalDetalleReporte(
    reporte: DetalleReporteUI,
    tipoReporte: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = reporte.color.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = reporte.icono,
                        contentDescription = null,
                        tint = reporte.color,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = reporte.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = reporte.descripcion,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MiniEstadoReporte(
                    titulo = "Estado",
                    valor = if (tipoReporte.lowercase() == "inventario") "Revisado" else "Actualizado",
                    color = Color(0xFF16A34A)
                )

                MiniEstadoReporte(
                    titulo = "Periodo",
                    valor = "Mes actual",
                    color = Color(0xFF2563EB)
                )

                MiniEstadoReporte(
                    titulo = if (tipoReporte.lowercase() == "inventario") "Alertas" else "Registros",
                    valor = if (tipoReporte.lowercase() == "inventario") "4" else reporte.totalSecundario,
                    color = if (tipoReporte.lowercase() == "inventario") Color(0xFFF59E0B) else Color(0xFF7C3AED)
                )
            }
        }
    }
}
@Composable
fun MiniEstadoReporte(
    titulo: String,
    valor: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            color = Color.Gray,
            fontSize = 10.sp
        )

        Text(
            text = valor,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
@Composable
fun ResumenDetalleReporte(
    reporte: DetalleReporteUI
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardDatoDetalleReporte(
            titulo = reporte.tituloTotalPrincipal,
            valor = reporte.totalPrincipal,
            icono = reporte.icono,
            color = reporte.color,
            modifier = Modifier.weight(1f)
        )

        CardDatoDetalleReporte(
            titulo = reporte.tituloTotalSecundario,
            valor = reporte.totalSecundario,
            icono = Icons.Default.Description,
            color = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
fun CardDatoDetalleReporte(
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
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = color.copy(alpha = 0.13f),
                        shape = RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = titulo,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = valor,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun SeccionPeriodoReporte() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Periodo del reporte",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Text(
                    text = "Mes actual · 01 May - 31 May 2026",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun DatosReporte(
    tipoReporte: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            when (tipoReporte.lowercase()) {
                "ingresos" -> {
                    LineaDatoReporte("Mayor ingreso", "$8,500")
                    LineaDatoReporte("Método más usado", "Transferencia")
                    LineaDatoReporte("Categoría principal", "Trabajos de soldadura")
                }

                "gastos" -> {
                    LineaDatoReporte("Mayor gasto", "$3,200")
                    LineaDatoReporte("Proveedor principal", "Aceros del Norte")
                    LineaDatoReporte("Categoría principal", "Materiales")
                }

                "inventario" -> {
                    LineaDatoReporte("Productos registrados", "28")
                    LineaDatoReporte("Productos bajo stock", "4")
                    LineaDatoReporte("Último movimiento", "Salida de material")
                }

                "cotizaciones" -> {
                    LineaDatoReporte("Cotizaciones aceptadas", "5")
                    LineaDatoReporte("Cotizaciones pendientes", "7")
                    LineaDatoReporte("Cotizaciones rechazadas", "3")
                }

                "clientes" -> {
                    LineaDatoReporte("Clientes activos", "18")
                    LineaDatoReporte("Clientes inactivos", "3")
                    LineaDatoReporte("Cliente con más cotizaciones", "Carlos Ruiz")
                }

                else -> {
                    LineaDatoReporte("Estado", "Sin información")
                    LineaDatoReporte("Registros encontrados", "0")
                    LineaDatoReporte("Última actualización", "No disponible")
                }
            }
        }
    }
}

@Composable
fun LineaDatoReporte(
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    Divider(
        color = Color(0xFFE5E7EB)
    )
}

@Composable
fun ItemRegistroReporte(
    registro: RegistroReporteUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        registro.color.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = registro.color,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(9.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = registro.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = registro.descripcion,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = registro.fecha,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }

            Text(
                text = registro.monto,
                color = registro.color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun SeccionAccionesDetalleReporte() {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                text = "Acciones",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Puedes visualizar o exportar la información del reporte.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "PDF",
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "Excel",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

fun obtenerDetalleReporte(tipoReporte: String): DetalleReporteUI {
    return when (tipoReporte.lowercase()) {
        "ingresos" -> DetalleReporteUI(
            titulo = "Reporte de ingresos",
            descripcion = "Resumen de entradas registradas en el periodo.",
            icono = Icons.Default.AttachMoney,
            color = Color(0xFF16A34A),
            totalPrincipal = "$24,000",
            tituloTotalPrincipal = "Total ingresos",
            totalSecundario = "12",
            tituloTotalSecundario = "Registros"
        )

        "gastos" -> DetalleReporteUI(
            titulo = "Reporte de gastos",
            descripcion = "Resumen de salidas registradas en el periodo.",
            icono = Icons.Default.ShoppingBag,
            color = Color(0xFFDC2626),
            totalPrincipal = "$13,500",
            tituloTotalPrincipal = "Total gastos",
            totalSecundario = "9",
            tituloTotalSecundario = "Registros"
        )

        "inventario" -> DetalleReporteUI(
            titulo = "Reporte de inventario",
            descripcion = "Resumen de productos, existencias y movimientos.",
            icono = Icons.Default.Inventory,
            color = Color(0xFF2563EB),
            totalPrincipal = "28",
            tituloTotalPrincipal = "Productos",
            totalSecundario = "4",
            tituloTotalSecundario = "Bajo stock"
        )

        "cotizaciones" -> DetalleReporteUI(
            titulo = "Reporte de cotizaciones",
            descripcion = "Estado general de cotizaciones registradas.",
            icono = Icons.Default.RequestQuote,
            color = Color(0xFFF59E0B),
            totalPrincipal = "15",
            tituloTotalPrincipal = "Cotizaciones",
            totalSecundario = "7",
            tituloTotalSecundario = "Pendientes"
        )

        "clientes" -> DetalleReporteUI(
            titulo = "Reporte de clientes",
            descripcion = "Resumen de clientes registrados y actividad reciente.",
            icono = Icons.Default.People,
            color = Color(0xFF7C3AED),
            totalPrincipal = "21",
            tituloTotalPrincipal = "Clientes",
            totalSecundario = "18",
            tituloTotalSecundario = "Activos"
        )

        else -> DetalleReporteUI(
            titulo = "Reporte",
            descripcion = "Información general del reporte.",
            icono = Icons.Default.Assessment,
            color = Color(0xFF2563EB),
            totalPrincipal = "0",
            tituloTotalPrincipal = "Total",
            totalSecundario = "0",
            tituloTotalSecundario = "Registros"
        )
    }
}

fun obtenerRegistrosReporte(
    tipoReporte: String,
    color: Color
): List<RegistroReporteUI> {
    return when (tipoReporte.lowercase()) {
        "ingresos" -> listOf(
            RegistroReporteUI("Pago de estructura metálica", "Cliente: Eduardo Barrios", "$8,500", "20/05/2026", color),
            RegistroReporteUI("Anticipo de portón", "Cliente: José Vera", "$5,000", "18/05/2026", color),
            RegistroReporteUI("Trabajo de reparación", "Cliente: María López", "$2,800", "15/05/2026", color)
        )

        "gastos" -> listOf(
            RegistroReporteUI("Compra de material", "Proveedor: Aceros del Norte", "$3,200", "19/05/2026", color),
            RegistroReporteUI("Combustible", "Proveedor: PEMEX", "$850", "18/05/2026", color),
            RegistroReporteUI("Servicio eléctrico", "Proveedor: CFE", "$2,100", "16/05/2026", color)
        )

        "inventario" -> listOf(
            RegistroReporteUI(
                titulo = "PTR 2x2 Cal.14",
                descripcion = "Stock actual: 10 piezas",
                monto = "Bajo",
                fecha = "20/05/2026",
                color = Color(0xFFF59E0B)
            ),
            RegistroReporteUI(
                titulo = "Soldadura 6013",
                descripcion = "Stock actual: 25 cajas",
                monto = "OK",
                fecha = "18/05/2026",
                color = Color(0xFF16A34A)
            ),
            RegistroReporteUI(
                titulo = "Disco de corte",
                descripcion = "Stock actual: 0 piezas",
                monto = "Agotado",
                fecha = "17/05/2026",
                color = Color(0xFFDC2626)
            )
        )

        "cotizaciones" -> listOf(
            RegistroReporteUI("Cotización #001", "Cliente: Eduardo Barrios", "$12,000", "20/05/2026", color),
            RegistroReporteUI("Cotización #002", "Cliente: Carlos Ruiz", "$8,700", "18/05/2026", color),
            RegistroReporteUI("Cotización #003", "Cliente: María López", "$4,500", "16/05/2026", color)
        )

        "clientes" -> listOf(
            RegistroReporteUI("Eduardo Barrios", "2 cotizaciones registradas", "Activo", "20/05/2026", color),
            RegistroReporteUI("José Vera", "1 cotización registrada", "Activo", "18/05/2026", color),
            RegistroReporteUI("María López", "1 cotización registrada", "Inactivo", "16/05/2026", color)
        )

        else -> emptyList()
    }
}

@Composable
fun SeccionResumenFinancieroIngresos() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            LineaDatoReporteMejorada(
                titulo = "Total cobrado",
                valor = "$24,000",
                icono = Icons.Default.AttachMoney,
                color = Color(0xFF16A34A)
            )

            LineaDatoReporteMejorada(
                titulo = "Ingreso promedio",
                valor = "$2,000",
                icono = Icons.Default.TrendingUp,
                color = Color(0xFF2563EB)
            )

            LineaDatoReporteMejorada(
                titulo = "Método más usado",
                valor = "Transferencia",
                icono = Icons.Default.Description,
                color = Color(0xFF7C3AED)
            )

            LineaDatoReporteMejorada(
                titulo = "Mayor ingreso",
                valor = "$8,500",
                icono = Icons.Default.Assessment,
                color = Color(0xFFF59E0B),
                mostrarDivider = false
            )
        }
    }
}

@Composable
fun LineaDatoReporteMejorada(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    mostrarDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(9.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(9.dp))

        Text(
            text = titulo,
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    if (mostrarDivider) {
        Divider(
            color = Color(0xFFE5E7EB)
        )
    }
}

@Composable
fun SeccionMetodosPagoReporteIngresos() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            MetodoPagoReporteItem(
                metodo = "Transferencia",
                monto = "$13,500",
                porcentaje = "56%",
                color = Color(0xFF2563EB)
            )

            MetodoPagoReporteItem(
                metodo = "Efectivo",
                monto = "$7,200",
                porcentaje = "30%",
                color = Color(0xFF16A34A)
            )

            MetodoPagoReporteItem(
                metodo = "Tarjeta",
                monto = "$3,300",
                porcentaje = "14%",
                color = Color(0xFFF59E0B),
                mostrarDivider = false
            )
        }
    }
}

@Composable
fun MetodoPagoReporteItem(
    metodo: String,
    monto: String,
    porcentaje: String,
    color: Color,
    mostrarDivider: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(color, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = metodo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = monto,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = color
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = porcentaje,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        when (porcentaje) {
                            "56%" -> 0.56f
                            "30%" -> 0.30f
                            "14%" -> 0.14f
                            else -> 0.20f
                        }
                    )
                    .height(6.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }

    if (mostrarDivider) {
        Divider(
            color = Color(0xFFE5E7EB)
        )
    }
}

fun obtenerTituloResumenReporte(tipoReporte: String): String {
    return when (tipoReporte.lowercase()) {
        "ingresos" -> "Resumen financiero"
        "gastos" -> "Resumen de gastos"
        "inventario" -> "Resumen de inventario"
        "cotizaciones" -> "Resumen de cotizaciones"
        "clientes" -> "Resumen de clientes"
        else -> "Información del reporte"
    }
}

@Composable
fun SeccionResumenInventario() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            LineaDatoReporteMejorada(
                titulo = "Productos registrados",
                valor = "28",
                icono = Icons.Default.Inventory,
                color = Color(0xFF2563EB)
            )

            LineaDatoReporteMejorada(
                titulo = "Productos bajo stock",
                valor = "4",
                icono = Icons.Default.Assessment,
                color = Color(0xFFF59E0B)
            )

            LineaDatoReporteMejorada(
                titulo = "Productos agotados",
                valor = "1",
                icono = Icons.Default.Description,
                color = Color(0xFFDC2626)
            )

            LineaDatoReporteMejorada(
                titulo = "Último movimiento",
                valor = "Salida",
                icono = Icons.Default.TrendingUp,
                color = Color(0xFF7C3AED),
                mostrarDivider = false
            )
        }
    }
}

@Composable
fun SeccionEstadoInventarioReporte() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            EstadoInventarioReporteItem(
                estado = "Stock correcto",
                cantidad = "23 productos",
                porcentaje = 0.82f,
                color = Color(0xFF16A34A)
            )

            EstadoInventarioReporteItem(
                estado = "Bajo stock",
                cantidad = "4 productos",
                porcentaje = 0.14f,
                color = Color(0xFFF59E0B)
            )

            EstadoInventarioReporteItem(
                estado = "Agotados",
                cantidad = "1 producto",
                porcentaje = 0.04f,
                color = Color(0xFFDC2626),
                mostrarDivider = false
            )
        }
    }
}

@Composable
fun EstadoInventarioReporteItem(
    estado: String,
    cantidad: String,
    porcentaje: Float,
    color: Color,
    mostrarDivider: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(color, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = estado,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = cantidad,
                color = Color.Gray,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(porcentaje)
                    .height(6.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }

    if (mostrarDivider) {
        Divider(
            color = Color(0xFFE5E7EB)
        )
    }
}