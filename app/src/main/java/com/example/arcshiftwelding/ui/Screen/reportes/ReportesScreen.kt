package com.example.arcshiftwelding.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

data class ReporteUI(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val color: Color,
    val estado: String
)

@Composable
fun ReportesScreen(
    navController: NavController
) {
    var periodoSeleccionado by remember {
        mutableStateOf("Mes")
    }

    val reportes = listOf(
        ReporteUI(
            titulo = "Ingresos",
            descripcion = "Entradas registradas por fecha y método de pago.",
            icono = Icons.Default.AttachMoney,
            color = Color(0xFF16A34A),
            estado = "Actualizado"
        ),
        ReporteUI(
            titulo = "Gastos",
            descripcion = "Gastos registrados por proveedor, concepto y categoría.",
            icono = Icons.Default.ShoppingBag,
            color = Color(0xFFDC2626),
            estado = "Actualizado"
        ),
        ReporteUI(
            titulo = "Inventario",
            descripcion = "Existencias, bajo stock y movimientos de productos.",
            icono = Icons.Default.Inventory,
            color = Color(0xFF2563EB),
            estado = "Pendiente"
        ),
        ReporteUI(
            titulo = "Cotizaciones",
            descripcion = "Cotizaciones creadas, aceptadas y rechazadas.",
            icono = Icons.Default.RequestQuote,
            color = Color(0xFFF59E0B),
            estado = "Revisión"
        ),
        ReporteUI(
            titulo = "Clientes",
            descripcion = "Clientes registrados y actividad relacionada.",
            icono = Icons.Default.People,
            color = Color(0xFF7C3AED),
            estado = "Actualizado"
        )
    )

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
            HeaderReportes(navController = navController)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    EncabezadoModuloReportes()
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Periodo de consulta",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = obtenerTextoPeriodo(periodoSeleccionado),
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                item {
                    FiltrosRapidosReportes(
                        periodoSeleccionado = periodoSeleccionado,
                        onPeriodoClick = { nuevoPeriodo ->
                            periodoSeleccionado = nuevoPeriodo
                        }
                    )
                }

                item {
                    ResumenReportesCompacto(
                        periodoSeleccionado = periodoSeleccionado
                    )
                }

                item {
                    Text(
                        text = "Reportes disponibles",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(reportes.size) { index ->
                    ItemReporte(
                        reporte = reportes[index],
                        onClick = {
                            navController.navigate(
                                AppRoutes.detalleReporte(reportes[index].titulo)
                            )
                        }
                    )
                }

                item {
                    SeccionExportarReportes()
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}


@Composable
fun HeaderReportes(
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
            text = "Reportes",
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
fun EncabezadoModuloReportes() {
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
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFEAF2FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Centro de reportes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Consulta información general del negocio",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
@Composable
fun FiltrosRapidosReportes(
    periodoSeleccionado: String,
    onPeriodoClick: (String) -> Unit
) {
    val periodos = listOf("Hoy", "Semana", "Mes", "Año")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        periodos.forEach { periodo ->
            FiltroReporte(
                texto = periodo,
                seleccionado = periodoSeleccionado == periodo,
                onClick = {
                    onPeriodoClick(periodo)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
fun FiltroReporte(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(38.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) {
                Color(0xFFEAF2FF)
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                color = if (seleccionado) Color(0xFF2563EB) else Color.DarkGray,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}
@Composable
fun ResumenReportesCompacto(
    periodoSeleccionado: String
) {
    val datos = obtenerResumenPorPeriodo(periodoSeleccionado)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TarjetaDatoReporte(
            titulo = "Generados",
            valor = datos.generados.toString(),
            icono = Icons.Default.Description,
            color = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )

        TarjetaDatoReporte(
            titulo = "Pendientes",
            valor = datos.pendientes.toString(),
            icono = Icons.Default.CalendarMonth,
            color = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )

        TarjetaDatoReporte(
            titulo = "Exportados",
            valor = datos.exportados.toString(),
            icono = Icons.Default.Download,
            color = Color(0xFF16A34A),
            modifier = Modifier.weight(1f)
        )
    }
}
data class ResumenPeriodoReporte(
    val generados: Int,
    val pendientes: Int,
    val exportados: Int
)

fun obtenerResumenPorPeriodo(periodo: String): ResumenPeriodoReporte {
    return when (periodo) {
        "Hoy" -> ResumenPeriodoReporte(
            generados = 2,
            pendientes = 1,
            exportados = 1
        )

        "Semana" -> ResumenPeriodoReporte(
            generados = 5,
            pendientes = 2,
            exportados = 3
        )

        "Mes" -> ResumenPeriodoReporte(
            generados = 8,
            pendientes = 3,
            exportados = 5
        )

        "Año" -> ResumenPeriodoReporte(
            generados = 36,
            pendientes = 9,
            exportados = 24
        )

        else -> ResumenPeriodoReporte(
            generados = 0,
            pendientes = 0,
            exportados = 0
        )
    }
}

fun obtenerTextoPeriodo(periodo: String): String {
    return when (periodo) {
        "Hoy" -> "Información de hoy"
        "Semana" -> "Últimos 7 días"
        "Mes" -> "Mes actual"
        "Año" -> "Año actual"
        else -> "Sin periodo"
    }
}

@Composable
fun TarjetaDatoReporte(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 9.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
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
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(7.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = titulo,
                    color = Color.Gray,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = valor,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ItemReporte(
    reporte: ReporteUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(reporte.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = reporte.icono,
                    contentDescription = null,
                    tint = reporte.color,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reporte.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = reporte.descripcion,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = reporte.estado,
                    color = reporte.color,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SeccionExportarReportes() {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = "Exportación",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Genera un archivo con la información seleccionada.",
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