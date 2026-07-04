package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProyectosScreen(
    navController: NavController,
    viewModel: ProyectosViewModel
) {
    val proyectos by viewModel.proyectos.collectAsState()
    val resumen by viewModel.resumen.collectAsState()

    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    val proyectosFiltrados = when (filtroSeleccionado) {
        "Pendiente" -> proyectos.filter { it.estado == "Pendiente" }
        "En trabajo" -> proyectos.filter { it.estado == "En trabajo" }
        "Terminado" -> proyectos.filter { it.estado == "Terminado" }
        "Cancelado" -> proyectos.filter { it.estado == "Cancelado" }
        else -> proyectos
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Proyectos",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .padding(14.dp)
        ) {
            HeaderProyectos(
                total = resumen.total,
                onNuevoProyecto = {
                    navController.navigate(AppRoutes.NUEVO_PROYECTO)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ResumenProyectos(resumen = resumen)

            Spacer(modifier = Modifier.height(12.dp))

            FiltrosProyectos(
                filtroSeleccionado = filtroSeleccionado,
                onFiltroSeleccionado = { filtroSeleccionado = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (proyectosFiltrados.isEmpty()) {
                EmptyProyectos()
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(proyectosFiltrados.size) { index ->
                        ProyectoCard(
                            proyecto = proyectosFiltrados[index],
                            onClick = {
                                // Después conectamos DetalleProyectoScreen
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderProyectos(
    total: Int,
    onNuevoProyecto: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Control de proyectos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Text(
                text = "$total proyectos registrados",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }

        Button(
            onClick = onNuevoProyecto,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text("Nuevo")
        }
    }
}

@Composable
fun ResumenProyectos(
    resumen: ResumenProyectosUI
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CardResumenProyecto(
            titulo = "Pendientes",
            cantidad = resumen.pendientes,
            color = Color(0xFFF59E0B)
        )

        CardResumenProyecto(
            titulo = "En trabajo",
            cantidad = resumen.enTrabajo,
            color = Color(0xFF2563EB)
        )

        CardResumenProyecto(
            titulo = "Terminados",
            cantidad = resumen.terminados,
            color = Color(0xFF16A34A)
        )

        CardResumenProyecto(
            titulo = "Cancelados",
            cantidad = resumen.cancelados,
            color = Color(0xFFDC2626)
        )
    }
}

@Composable
fun CardResumenProyecto(
    titulo: String,
    cantidad: Int,
    color: Color
) {
    Card(
        modifier = Modifier.width(135.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun FiltrosProyectos(
    filtroSeleccionado: String,
    onFiltroSeleccionado: (String) -> Unit
) {
    val filtros = listOf(
        "Todos",
        "Pendiente",
        "En trabajo",
        "Terminado",
        "Cancelado"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filtros.forEach { filtro ->
            val seleccionado = filtroSeleccionado == filtro

            if (seleccionado) {
                Button(
                    onClick = { onFiltroSeleccionado(filtro) },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    )
                ) {
                    Text(filtro)
                }
            } else {
                OutlinedButton(
                    onClick = { onFiltroSeleccionado(filtro) },
                    shape = RoundedCornerShape(50)
                ) {
                    Text(filtro)
                }
            }
        }
    }
}

@Composable
fun ProyectoCard(
    proyecto: ProyectoUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = proyecto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    InfoProyectoLinea(
                        icono = Icons.Default.Person,
                        texto = proyecto.cliente
                    )
                }

                BadgeEstadoProyecto(estado = proyecto.estado)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoProyectoChip(
                    texto = "Inicio: ${proyecto.fechaInicio}",
                    icono = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f)
                )

                InfoProyectoChip(
                    texto = "Fin: ${proyecto.fechaEstimadaFin}",
                    icono = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f)
                )
                InfoProyectoChip(
                    texto = "Fin: ${proyecto.fechaFinReal}",
                    icono = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f)
                )

            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { avancePorEstado(proyecto.estado) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = colorEstadoProyecto(proyecto.estado),
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun InfoProyectoLinea(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun InfoProyectoChip(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF334155),
            maxLines = 1
        )
    }
}

@Composable
fun BadgeEstadoProyecto(
    estado: String
) {
    val color = colorEstadoProyecto(estado)

    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = estado,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyProyectos() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No hay proyectos registrados",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun colorEstadoProyecto(estado: String): Color {
    return when (estado) {
        "Pendiente" -> Color(0xFFF59E0B)
        "En trabajo" -> Color(0xFF2563EB)
        "Terminado" -> Color(0xFF16A34A)
        "Cancelado" -> Color(0xFFDC2626)
        else -> Color(0xFF64748B)
    }
}

fun avancePorEstado(estado: String): Float {
    return when (estado) {
        "Pendiente" -> 0.15f
        "En trabajo" -> 0.55f
        "Terminado" -> 1f
        "Cancelado" -> 0f
        else -> 0f
    }
}