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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.arcshiftwelding.ui.components.DialogoExportarArchivo
import com.example.arcshiftwelding.ui.Screen.notificaciones.CampanaNotificacionesPrincipal
import com.example.arcshiftwelding.ui.viewmodel.PeriodoReporte
import com.example.arcshiftwelding.ui.viewmodel.ReporteDetalleUi
import com.example.arcshiftwelding.ui.viewmodel.ReportesUiState
import com.example.arcshiftwelding.ui.viewmodel.ReportesViewModel
import com.example.arcshiftwelding.utils.compartirArchivoReporte
import com.example.arcshiftwelding.utils.generarReporteCsv
import com.example.arcshiftwelding.utils.generarReportePdf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.example.arcshiftwelding.ui.theme.arcshiftColors

private data class ArchivoExportacionGeneralPendiente(
    val archivo: File,
    val mimeType: String,
    val asunto: String,
    val tituloDialogo: String
)

@Composable
fun ReportesScreen(
    navController: NavController,
    viewModel: ReportesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var exportacionPendiente by remember {
        mutableStateOf<ArchivoExportacionGeneralPendiente?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        HeaderReportes(navController)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 6.dp, bottom = 12.dp)
        ) {
            item {
                SelectorPeriodoReportes(
                    periodo = uiState.periodo,
                    onSeleccionar = viewModel::seleccionarPeriodo
                )
            }

            item {
                ResumenGeneralReportes(uiState)
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reportes disponibles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = uiState.textoPeriodo,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "${uiState.reportes.size} módulos",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (uiState.cargando) {
                item {
                    TarjetaSinDatos("Calculando información...")
                }
            } else {
                items(
                    items = uiState.reportes,
                    key = { it.tipo }
                ) { reporte ->
                    TarjetaModuloReporte(
                        reporte = reporte,
                        onClick = {
                            navController.navigate(AppRoutes.detalleReporte(reporte.tipo))
                        }
                    )
                }
            }

            item {
                AccionesExportacionReportes(
                    habilitado = uiState.reportes.isNotEmpty(),
                    onPdf = {
                        scope.launch {
                            val resultado = withContext(Dispatchers.IO) {
                                generarReportePdf(
                                    context = context,
                                    titulo = "Reporte general",
                                    periodo = uiState.textoPeriodo,
                                    reportes = uiState.reportes
                                )
                            }
                            resultado.onSuccess { archivo ->
                                exportacionPendiente = ArchivoExportacionGeneralPendiente(
                                    archivo = archivo,
                                    mimeType = "application/pdf",
                                    asunto = "Reporte general",
                                    tituloDialogo = "PDF generado"
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
                                    titulo = "Reporte general",
                                    periodo = uiState.textoPeriodo,
                                    reportes = uiState.reportes
                                )
                            }
                            resultado.onSuccess { archivo ->
                                exportacionPendiente = ArchivoExportacionGeneralPendiente(
                                    archivo = archivo,
                                    mimeType = "text/csv",
                                    asunto = "Reporte general",
                                    tituloDialogo = "Archivo de Excel generado"
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

    exportacionPendiente?.let { exportacion ->
        DialogoExportarArchivo(
            archivo = exportacion.archivo,
            mimeType = exportacion.mimeType,
            titulo = exportacion.tituloDialogo,
            onDismiss = { exportacionPendiente = null },
            onCompartir = {
                compartirArchivoReporte(
                    context = context,
                    archivo = exportacion.archivo,
                    mimeType = exportacion.mimeType,
                    asunto = exportacion.asunto
                )
            }
        )
    }
}

@Composable
fun HeaderReportes(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 20.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Reportes",
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
private fun SelectorPeriodoReportes(
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
                    color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(
                            if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun ResumenGeneralReportes(uiState: ReportesUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResumenReporteDato(
                    titulo = "Ingresos",
                    valor = uiState.resumen.ingresos.formatoMonedaReporte(),
                    color = MaterialTheme.arcshiftColors.success,
                    modifier = Modifier.weight(1f)
                )
                ResumenReporteDato(
                    titulo = "Gastos",
                    valor = uiState.resumen.gastos.formatoMonedaReporte(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                ResumenReporteDato(
                    titulo = "Utilidad",
                    valor = uiState.resumen.utilidad.formatoMonedaReporte(),
                    color = if (uiState.resumen.utilidad >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AlertaReporteCompacta(
                    texto = "Por cobrar ${uiState.resumen.porCobrar.formatoMonedaReporte()}",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
                AlertaReporteCompacta(
                    texto = "${uiState.resumen.cotizacionesPendientes} cotizaciones pendientes",
                    color = MaterialTheme.arcshiftColors.warning,
                    modifier = Modifier.weight(1f)
                )
                AlertaReporteCompacta(
                    texto = "${uiState.resumen.productosBajoStock} bajo stock",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ResumenReporteDato(
    titulo: String,
    valor: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = titulo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
        Text(
            text = valor,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = tamanoMontoReporte(valor),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AlertaReporteCompacta(
    texto: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.09f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = texto,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TarjetaModuloReporte(
    reporte: ReporteDetalleUi,
    onClick: () -> Unit
) {
    val apariencia = aparienciaReporte(reporte.tipo)
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(apariencia.color.copy(alpha = 0.13f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = apariencia.icono,
                    contentDescription = null,
                    tint = apariencia.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reporte.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = reporte.descripcion,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = reporte.valorPrincipal,
                    color = apariencia.color,
                    fontWeight = FontWeight.Bold,
                    fontSize = tamanoMontoReporte(reporte.valorPrincipal),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${reporte.valorSecundario} ${reporte.etiquetaSecundaria.lowercase()}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 8.sp
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver reporte",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun AccionesExportacionReportes(
    habilitado: Boolean,
    onPdf: () -> Unit,
    onCsv: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Exportar resumen general", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(
                "Genera un PDF o un archivo CSV compatible con Excel.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
            )
            Spacer(modifier = Modifier.height(9.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPdf,
                    enabled = habilitado,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(17.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("PDF", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onCsv,
                    enabled = habilitado,
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

@Composable
private fun TarjetaSinDatos(texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(18.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class AparienciaReporte(
    val icono: ImageVector,
    val color: Color
)

@Composable

private fun aparienciaReporte(tipo: String): AparienciaReporte {
    return when (tipo.lowercase()) {
        "ingresos" -> AparienciaReporte(Icons.Default.AttachMoney, MaterialTheme.arcshiftColors.success)
        "gastos" -> AparienciaReporte(Icons.Default.ShoppingBag, MaterialTheme.colorScheme.error)
        "inventario" -> AparienciaReporte(Icons.Default.Inventory, MaterialTheme.colorScheme.primary)
        "cotizaciones" -> AparienciaReporte(Icons.Default.RequestQuote, MaterialTheme.arcshiftColors.warning)
        "clientes" -> AparienciaReporte(Icons.Default.People, MaterialTheme.colorScheme.secondary)
        "proyectos" -> AparienciaReporte(Icons.Default.Work, MaterialTheme.arcshiftColors.info)
        "empleados" -> AparienciaReporte(Icons.Default.Groups, MaterialTheme.colorScheme.secondary)
        else -> AparienciaReporte(Icons.Default.Assessment, MaterialTheme.colorScheme.primary)
    }
}

private fun Double.formatoMonedaReporte(): String {
    return java.text.NumberFormat
        .getCurrencyInstance(java.util.Locale("es", "MX"))
        .format(this)
}

private fun tamanoMontoReporte(valor: String) = when {
    valor.length >= 16 -> 9.sp
    valor.length >= 13 -> 10.sp
    valor.length >= 10 -> 11.sp
    else -> 13.sp
}
