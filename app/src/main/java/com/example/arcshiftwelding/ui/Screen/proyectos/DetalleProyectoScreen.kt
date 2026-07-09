package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.TextButton
import com.example.arcshiftwelding.data.local.entity.ProyectoEmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoMaterialEntity
import com.example.arcshiftwelding.data.repository.ResumenCostosProyecto
import com.example.arcshiftwelding.ui.Screen.clientes.TituloSeccionCliente
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProyectoScreen(
    navController: NavController,
    viewModel: ProyectosViewModel,
    proyectoId: Int
) {
    val proyectos by viewModel.proyectos.collectAsState()
    val proyecto = proyectos.find { it.id == proyectoId }
    var mostrarDialogoTerminar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }


    val empleados by proyectosViewModel
        .obtenerEmpleadosProyecto(proyecto.id)
        .collectAsState(initial = emptyList())

    val materiales by proyectosViewModel
        .obtenerMaterialesProyecto(proyecto.id)
        .collectAsState(initial = emptyList())

    val costos by proyectoViewModel
        .obtenerCostosProyecto(proyecto.id)
        .collectAsState(initial = emptyList())

    val resumenCostos by ProyectosViewModel
        .obtenerResumenCostosProyecto(
            proyectoId = proyecto.id,
            precioCotizado = proyecto.precioCotizado
        )
        .collectAsState(
            initial = ResumenCostosProyecto(
                precioCotizado = proyecto.precioCotizado
            )
        )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(
                        start = 17.dp,
                        top = 8.dp,
                        end = 14.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }

                Text(
                    text = "Detalle proyecto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        // Cuando tengas EditarProyectoScreen:
                        navController.navigate(AppRoutes.editarProyecto(proyectoId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Proyecto"
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0)
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


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

            SeccionResumenCostosProyecto(
                resumen = resumenCostos
            )

            Spacer(modifier = Modifier.height(12.dp))

            SeccionEmpleadosProyecto(
                empleados = empleados,
                onAgregarEmpleado = {
                    navController.navigate(AppRoutes.asignarEmpleadoProyecto(proyecto.id))
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SeccionMaterialesProyecto(
                materiales = materiales,
                onRegistrarMaterial = {
                    navController.navigate(AppRoutes.registrarMaterialProyecto(proyecto.id))
                },
                onEliminarMaterial = { material ->
                    proyectoViewModel.eliminarMaterialUsado(material.id)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SeccionCostosProyecto(
                costos = costos,
                onAgregarCosto = {
                    navController.navigate(AppRoutes.agregarCostoProyecto(proyecto.id))
                }
            )

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

            BotonesInferioresDetalleProyecto(
                proyecto = proyecto,
                onEditar = {
                    navController.navigate(AppRoutes.editarProyecto(proyecto.id))
                },
                onTerminar = {
                    mostrarDialogoTerminar = true
                },
                onEliminar = {
                    mostrarDialogoEliminar = true
                }
            )
        }
    }


    if (mostrarDialogoTerminar && proyecto != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoTerminar = false },
            title = {
                Text("Terminar proyecto")
            },
            text = {
                Text("¿Deseas marcar este proyecto como terminado? El avance se colocará en 100%.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.terminarProyecto(proyecto)
                        mostrarDialogoTerminar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF16A34A)
                    )
                ) {
                    Text("Terminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoTerminar = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoEliminar && proyecto != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = {
                Text("Eliminar proyecto")
            },
            text = {
                Text("¿Seguro que deseas eliminar este proyecto? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarProyecto(proyecto)
                        mostrarDialogoEliminar = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC2626)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoEliminar = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun SeccionResumenCostosProyecto(
    resumen: ResumenCostosProyecto
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen de costos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            FilaCostoProyecto("Cotizado", resumen.precioCotizado)
            FilaCostoProyecto("Material usado", resumen.costoMateriales)
            FilaCostoProyecto("Mano de obra", resumen.costoManoObra)
            FilaCostoProyecto("Costos adicionales", resumen.costosAdicionales)

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            FilaCostoProyecto(
                titulo = "Costo total",
                monto = resumen.costoTotal,
                resaltado = true
            )

            FilaCostoProyecto(
                titulo = "Utilidad estimada",
                monto = resumen.utilidad,
                resaltado = true
            )
        }
    }
}

@Composable
fun FilaCostoProyecto(
    titulo: String,
    monto: Double,
    resaltado: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = titulo,
            style = if (resaltado) {
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.bodyMedium
            }
        )

        Text(
            text = "$${String.format("%.2f", monto)}",
            style = if (resaltado) {
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                MaterialTheme.typography.bodyMedium
            }
        )
    }
}

@Composable
fun SeccionEmpleadosProyecto(
    empleados: List<ProyectoEmpleadoEntity>,
    onAgregarEmpleado: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Empleados asignados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onAgregarEmpleado) {
                    Text("Agregar")
                }
            }

            if (empleados.isEmpty()) {
                Text(
                    text = "No hay empleados asignados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                empleados.forEach { empleado ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = empleado.nombreEmpleado,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${empleado.puesto} - ${empleado.tipoPago}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Text(
                            text = "Costo: $${String.format("%.2f", empleado.costoCalculado)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Divider()
                }
            }
        }
    }
}
@Composable
fun SeccionMaterialesProyecto(
    materiales: List<ProyectoMaterialEntity>,
    onRegistrarMaterial: () -> Unit,
    onEliminarMaterial: (ProyectoMaterialEntity) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Material usado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onRegistrarMaterial) {
                    Text("Registrar")
                }
            }

            if (materiales.isEmpty()) {
                Text(
                    text = "No hay material registrado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                materiales.forEach { material ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = material.nombreProducto,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "${material.cantidadUsada} ${material.unidad} x $${String.format("%.2f", material.costoUnitario)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )

                            Text(
                                text = "Subtotal: $${String.format("%.2f", material.subtotal)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        IconButton(
                            onClick = {
                                onEliminarMaterial(material)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar material"
                            )
                        }
                    }

                    Divider()
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

@Composable
fun BotonesInferioresDetalleProyecto(
    proyecto: ProyectoUI,
    onEditar: () -> Unit,
    onTerminar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionCliente(
                titulo = "Acciones rápidas",
                icono = Icons.Default.Bolt,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonRapidoInferiorProyecto(
                    titulo = "Editar",
                    icono = Icons.Default.Edit,
                    color = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f),
                    onClick = onEditar
                )

                BotonRapidoInferiorProyecto(
                    titulo = "Terminar",
                    icono = Icons.Default.Check,
                    color = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f),
                    onClick = onTerminar
                )

                BotonRapidoInferiorProyecto(
                    titulo = "Eliminar",
                    icono = Icons.Default.Delete,
                    color = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f),
                    onClick = onEliminar
                )
            }
        }
    }
}
@Composable
fun BotonRapidoInferiorProyecto(
    titulo: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
