package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProyectoScreen(
    navController: NavController,
    viewModel: ProyectosViewModel,
    proyectoId: Int
) {
    val proyectos by viewModel.proyectos.collectAsState()
    val proyecto = proyectos.find { it.id == proyectoId }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle proyecto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Cuando tengas EditarProyectoScreen:
                            // navController.navigate(AppRoutes.editarProyecto(proyectoId))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        if (proyecto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Proyecto no encontrado")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HeaderDetalleProyecto(proyecto = proyecto)
            }

            item {
                CardSeccionDetalleProyecto(
                    titulo = "Información general",
                    icono = Icons.Default.Work
                ) {
                    ItemDetalleProyecto(
                        icono = Icons.Default.Person,
                        titulo = "Cliente",
                        valor = proyecto.cliente
                    )

                    ItemDetalleProyecto(
                        icono = Icons.Default.RequestQuote,
                        titulo = "Cotización",
                        valor = proyecto.cotizacion
                    )

                    ItemDetalleProyecto(
                        icono = Icons.Default.Description,
                        titulo = "Descripción",
                        valor = proyecto.descripcion.ifBlank { "Sin descripción" }
                    )
                }
            }

            item {
                CardSeccionDetalleProyecto(
                    titulo = "Fechas del proyecto",
                    icono = Icons.Default.CalendarMonth
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CajaDetalleProyecto(
                            titulo = "Inicio",
                            valor = proyecto.fechaInicio.ifBlank { "Sin fecha" },
                            modifier = Modifier.weight(1f)
                        )

                        CajaDetalleProyecto(
                            titulo = "Entrega",
                            valor = proyecto.fechaEstimadaFin.ifBlank { "Sin fecha" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    CajaDetalleProyecto(
                        titulo = "Fecha final real",
                        valor = proyecto.fechaFinReal.ifBlank { "Pendiente" },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                CardSeccionDetalleProyecto(
                    titulo = "Costos del proyecto",
                    icono = Icons.Default.AttachMoney
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CajaDetalleProyecto(
                            titulo = "Presupuesto",
                            valor = formatoMonedaProyecto(proyecto.presupuestoEstimado),
                            modifier = Modifier.weight(1f)
                        )

                        CajaDetalleProyecto(
                            titulo = "Costo real",
                            valor = formatoMonedaProyecto(proyecto.costoTotal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CajaDetalleProyecto(
                            titulo = "Material",
                            valor = formatoMonedaProyecto(proyecto.costoMaterial),
                            modifier = Modifier.weight(1f)
                        )

                        CajaDetalleProyecto(
                            titulo = "Mano de obra",
                            valor = formatoMonedaProyecto(proyecto.costoManoObra),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val utilidad = proyecto.presupuestoEstimado - proyecto.costoTotal

                    CajaDetalleProyecto(
                        titulo = "Utilidad estimada",
                        valor = formatoMonedaProyecto(utilidad),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                CardSeccionDetalleProyecto(
                    titulo = "Empleados y material",
                    icono = Icons.Default.Groups
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BotonAccionProyecto(
                            icono = Icons.Default.Groups,
                            titulo = "Empleados",
                            subtitulo = "Asignar personal",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                // Después conectamos AsignarEmpleadosProyectoScreen
                            }
                        )

                        BotonAccionProyecto(
                            icono = Icons.Default.Inventory,
                            titulo = "Material",
                            subtitulo = "Registrar uso",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                // Después conectamos AgregarMaterialProyectoScreen
                            }
                        )
                    }
                }
            }

            item {
                CardSeccionDetalleProyecto(
                    titulo = "Observaciones",
                    icono = Icons.Default.Description
                ) {
                    Text(
                        text = proyecto.observaciones.ifBlank { "Sin observaciones registradas." },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF334155)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // Pendiente eliminar proyecto
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Eliminar")
                    }

                    FilledTonalButton(
                        onClick = {
                            // Pendiente editar proyecto
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Editar")
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderDetalleProyecto(
    proyecto: ProyectoUI
) {
    val color = colorEstadoProyecto(proyecto.estado)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = proyecto.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = proyecto.cliente,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }

                BadgeDetalleProyecto(
                    texto = proyecto.estado,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Avance",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF64748B)
                )

                Text(
                    text = "${proyecto.avance}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            LinearProgressIndicator(
                progress = { proyecto.avance / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = color,
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun CardSeccionDetalleProyecto(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider(color = Color(0xFFE2E8F0))

            Spacer(modifier = Modifier.height(10.dp))

            contenido()
        }
    }
}

@Composable
fun ItemDetalleProyecto(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF64748B),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B)
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CajaDetalleProyecto(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B),
            maxLines = 1
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun BadgeDetalleProyecto(
    texto: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BotonAccionProyecto(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF64748B)
            )
        }
    }
}