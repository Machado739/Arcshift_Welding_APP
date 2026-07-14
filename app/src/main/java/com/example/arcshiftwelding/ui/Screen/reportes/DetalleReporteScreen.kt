package com.example.arcshiftwelding.ui.Screen.reportes

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.viewmodel.PeriodoReporte
import com.example.arcshiftwelding.ui.viewmodel.RegistroReporteUi
import com.example.arcshiftwelding.ui.viewmodel.ReporteDetalleUi
import com.example.arcshiftwelding.ui.viewmodel.ReportesViewModel
import com.example.arcshiftwelding.utils.compartirArchivoReporte
import com.example.arcshiftwelding.utils.generarReporteCsv
import com.example.arcshiftwelding.utils.generarReportePdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DetalleReporteScreen(
    navController: NavController,
    tipoReporte: String,
    viewModel: ReportesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val reporte = uiState.reportePorTipo(tipoReporte)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        HeaderDetalleReporte(
            titulo = reporte?.titulo ?: "Reporte",
            navController = navController
        )

        if (reporte == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = if (uiState.cargando) {
                        "Calculando información..."
                    } else {
                        "No se encontró información para este reporte."
                    },
                    modifier = Modifier.padding(20.dp),
                    color = Color(0xFF64748B)
                )
            }
        } else LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 6.dp, bottom = 14.dp)
        ) {
            item {
                SelectorPeriodoDetalle(
                    periodo = uiState.periodo,
                    onSeleccionar = viewModel::seleccionarPeriodo
                )
            }

            item {
                CabeceraReporteDetalle(
                    reporte = reporte,
                    periodo = uiState.textoPeriodo
                )
            }

            item {
                ResumenReporteDetalle(reporte)
            }

            item {
                MetricasReporteDetalle(reporte)
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registros",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = reporte.registros.size.toString(),
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            if (reporte.registros.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "No hay registros en el periodo seleccionado.",
                            modifier = Modifier.padding(18.dp),
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(
                    items = reporte.registros,
                    key = { "${reporte.tipo}-${it.id}" }
                ) { registro ->
                    RegistroReporteCard(
                        registro = registro,
                        color = colorReporteDetalle(reporte.tipo),
                        onClick = {
                            rutaRegistroReporte(reporte.tipo, registro.id)?.let { ruta ->
                                navController.navigate(ruta)
                            }
                        }
                    )
                }
            }

            item {
                AccionesDetalleReporte(
                    onPdf = {
                        scope.launch {
                            val resultado = withContext(Dispatchers.IO) {
                                generarReportePdf(
                                    context = context,
                                    titulo = "Reporte de ${reporte.titulo}",
                                    periodo = uiState.textoPeriodo,
                                    reportes = listOf(reporte)
                                )
                            }
                            resultado.onSuccess { archivo ->
                                compartirArchivoReporte(
                                    context = context,
                                    archivo = archivo,
                                    mimeType = "application/pdf",
                                    asunto = "Reporte de ${reporte.titulo}"
                                )
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    it.message ?: "No fue posible generar el PDF.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    onCsv = {
                        scope.launch {
                            val resultado = withContext(Dispatchers.IO) {
                                generarReporteCsv(
                                    context = context,
                                    titulo = "Reporte de ${reporte.titulo}",
                                    periodo = uiState.textoPeriodo,
                                    reportes = listOf(reporte)
                                )
                            }
                            resultado.onSuccess { archivo ->
                                compartirArchivoReporte(
                                    context = context,
                                    archivo = archivo,
                                    mimeType = "text/csv",
                                    asunto = "Reporte de ${reporte.titulo}"
                                )
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    it.message ?: "No fue posible generar el archivo para Excel.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderDetalleReporte(
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
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
        }
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SelectorPeriodoDetalle(
    periodo: PeriodoReporte,
    onSeleccionar: (PeriodoReporte) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(PeriodoReporte.entries) { opcion ->
            val seleccionado = opcion == periodo
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSeleccionar(opcion) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = opcion.etiqueta,
                    color = if (seleccionado) Color(0xFF2563EB) else Color(0xFF64748B),
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(
                            if (seleccionado) Color(0xFF2563EB) else Color.Transparent,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun CabeceraReporteDetalle(
    reporte: ReporteDetalleUi,
    periodo: String
) {
    val color = colorReporteDetalle(reporte.tipo)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    .size(46.dp)
                    .background(color.copy(alpha = 0.13f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoReporteDetalle(reporte.tipo),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(25.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reporte.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = reporte.descripcion,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = periodo,
                    color = color,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = reporte.estado,
                color = color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(color.copy(alpha = 0.10f), RoundedCornerShape(7.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ResumenReporteDetalle(reporte: ReporteDetalleUi) {
    val color = colorReporteDetalle(reporte.tipo)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DatoPrincipalReporte(
            titulo = reporte.etiquetaPrincipal,
            valor = reporte.valorPrincipal,
            color = color,
            modifier = Modifier.weight(1f)
        )
        DatoPrincipalReporte(
            titulo = reporte.etiquetaSecundaria,
            valor = reporte.valorSecundario,
            color = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DatoPrincipalReporte(
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titulo,
                color = Color(0xFF64748B),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = valor,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = when {
                    valor.length >= 16 -> 12.sp
                    valor.length >= 12 -> 15.sp
                    else -> 19.sp
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetricasReporteDetalle(reporte: ReporteDetalleUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            reporte.metricas.forEachIndexed { index, metrica ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = metrica.titulo,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = metrica.valor,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (index < reporte.metricas.lastIndex) {
                    Divider(color = Color(0xFFE2E8F0))
                }
            }
        }
    }
}

@Composable
private fun RegistroReporteCard(
    registro: RegistroReporteUi,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(modifier = Modifier.width(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = registro.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = registro.descripcion,
                    color = Color(0xFF64748B),
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listOf(registro.fecha, registro.estado)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    color = Color(0xFF94A3B8),
                    fontSize = 8.sp,
                    maxLines = 1
                )
            }
            Text(
                text = registro.valor,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = when {
                    registro.valor.length >= 14 -> 9.sp
                    registro.valor.length >= 10 -> 10.sp
                    else -> 12.sp
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Abrir registro",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AccionesDetalleReporte(
    onPdf: () -> Unit,
    onCsv: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Exportar reporte", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPdf,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("PDF", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onCsv,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Excel CSV", fontSize = 12.sp)
                }
            }
        }
    }
}

private fun iconoReporteDetalle(tipo: String): ImageVector {
    return when (tipo.lowercase()) {
        "ingresos" -> Icons.Default.AttachMoney
        "gastos" -> Icons.Default.ShoppingBag
        "inventario" -> Icons.Default.Inventory
        "cotizaciones" -> Icons.Default.RequestQuote
        "clientes" -> Icons.Default.People
        "proyectos" -> Icons.Default.Work
        "empleados" -> Icons.Default.Groups
        else -> Icons.Default.Assessment
    }
}

private fun colorReporteDetalle(tipo: String): Color {
    return when (tipo.lowercase()) {
        "ingresos" -> Color(0xFF16A34A)
        "gastos" -> Color(0xFFDC2626)
        "inventario" -> Color(0xFF2563EB)
        "cotizaciones" -> Color(0xFFF59E0B)
        "clientes" -> Color(0xFF7C3AED)
        "proyectos" -> Color(0xFF0891B2)
        "empleados" -> Color(0xFF4F46E5)
        else -> Color(0xFF2563EB)
    }
}

private fun rutaRegistroReporte(tipo: String, id: Int): String? {
    return when (tipo.lowercase()) {
        "ingresos" -> AppRoutes.detalleIngreso(id)
        "gastos" -> AppRoutes.detalleGasto(id)
        "inventario" -> AppRoutes.detalleProducto(id)
        "cotizaciones" -> AppRoutes.detalleCotizacion(id)
        "clientes" -> AppRoutes.detalleCliente(id)
        "proyectos" -> AppRoutes.detalleProyecto(id)
        "empleados" -> AppRoutes.detalleEmpleado(id)
        else -> null
    }
}
