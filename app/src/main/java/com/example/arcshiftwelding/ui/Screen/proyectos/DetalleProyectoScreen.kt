package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.History
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.TextButton
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoMaterialEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoAvanceEntity
import com.example.arcshiftwelding.ui.Screen.clientes.TituloSeccionCliente
import kotlin.collections.emptyList
import androidx.compose.material3.Slider
import com.example.arcshiftwelding.ui.theme.arcshiftColors
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProyectoScreen(
    navController: NavController,
    viewModel: ProyectosViewModel,
    proyectoId: Int
) {
    val context = LocalContext.current
    val proyectos by viewModel.proyectos.collectAsState()
    val proyecto = proyectos.find { it.id == proyectoId }

    var mostrarDialogoAvance by remember { mutableStateOf(false) }
    var avanceTemporal by remember { mutableStateOf(0) }
    var comentarioAvance by remember { mutableStateOf("") }
    var fotosAvance by remember { mutableStateOf(emptyList<ImagenProyectoSeleccionada>()) }

    var mostrarDialogoTerminar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    if (proyecto == null) {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
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
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0)
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Proyecto no encontrado")
            }
        }

        return
    }

    val empleados by viewModel
        .obtenerEmpleadosProyecto(proyecto.id)
        .collectAsState(initial = emptyList())

    val materiales by viewModel
        .obtenerMaterialesProyecto(proyecto.id)
        .collectAsState(initial = emptyList())



    val resumenCostos by viewModel
        .obtenerResumenCostosProyecto(
            proyectoId = proyecto.id,
            presupuestoEstimado = proyecto.presupuestoEstimado
        )
        .collectAsState(
            initial = ResumenCostosProyecto(
                precioCotizado = proyecto.presupuestoEstimado
            )
        )

    val gastosProyecto by viewModel
        .obtenerGastosProyecto(proyecto.id)
        .collectAsState(initial = emptyList())

    val historialAvances by viewModel
        .obtenerHistorialAvances(proyecto.id)
        .collectAsState(initial = emptyList())

    val imagenesProyecto = remember(proyecto.imagenesJson) {
        deserializarImagenesProyecto(proyecto.imagenesJson)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
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
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->



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
                titulo = "Imágenes del proyecto",
                icono = Icons.Default.Image
            ) {
                GaleriaImagenesProyecto(
                    imagenes = imagenesProyecto,
                    onAbrir = { abrirImagenProyecto(context, it) }
                )
            }

            CardSeccionDetalleProyecto(
                titulo = "Avance del proyecto",
                icono = Icons.Default.CalendarMonth
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Avance actual",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "${proyecto.avance}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorEstadoProyecto(proyecto.estado)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { proyecto.avance / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = colorEstadoProyecto(proyecto.estado),
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            avanceTemporal = proyecto.avance
                            comentarioAvance = ""
                            fotosAvance = emptyList()
                            mostrarDialogoAvance = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text("Modificar avance")
                    }
                }
            }



            SeccionHistorialAvancesProyecto(
                avances = historialAvances,
                onAbrirFoto = { abrirImagenProyecto(context, it) }
            )

            SeccionResumenCostosProyecto(
                resumen = resumenCostos
            )


            SeccionEmpleadosProyecto(
                empleados = empleados,
                onAgregarEmpleado = {
                    navController.navigate(AppRoutes.asignarEmpleadoProyecto(proyecto.id))
                },
                onEditarEmpleado = { empleadoProyecto ->
                    navController.navigate(
                        AppRoutes.editarEmpleadoProyecto(
                            proyectoId = proyecto.id,
                            empleadoProyectoId = empleadoProyecto.id
                        )
                    )
                },
                onEliminarEmpleado = { empleadoProyecto ->
                    viewModel.eliminarEmpleadoAsignadoProyecto(empleadoProyecto.id)
                }
            )


            SeccionMaterialesProyecto(
                materiales = materiales,
                onRegistrarMaterial = {
                    navController.navigate(AppRoutes.registrarMaterialProyecto(proyecto.id))
                },
                onEliminarMaterial = { material ->
                    viewModel.eliminarMaterialUsado(material.id)
                }
            )


            SeccionGastosProyecto(
                gastos = gastosProyecto,
                onAgregarGasto = {
                    navController.navigate(
                        AppRoutes.nuevoGastoProyecto(
                            proyectoId = proyecto.id,
                            proyectoNombre = proyecto.nombre
                        )
                    )
                },
                onEliminarGasto = { gasto ->
                    viewModel.eliminarGastoProyecto(gasto.id)
                }
            )

            CardSeccionDetalleProyecto(
                titulo = "Observaciones",
                icono = Icons.Default.Description
            ) {
                Text(
                    text = proyecto.observaciones.ifBlank { "Sin observaciones registradas." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
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
            if (mostrarDialogoAvance) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoAvance = false },
                    title = { Text("Registrar avance") },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 520.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Guarda el porcentaje, una nota y fotografías como evidencia del avance.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "$avanceTemporal%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Slider(
                                value = avanceTemporal.toFloat(),
                                onValueChange = { avanceTemporal = it.toInt() },
                                valueRange = 0f..100f,
                                steps = 99
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(0, 50, 100).forEach { porcentaje ->
                                    OutlinedButton(
                                        onClick = { avanceTemporal = porcentaje },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("$porcentaje%")
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = comentarioAvance,
                                onValueChange = { comentarioAvance = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Comentario del avance") },
                                placeholder = { Text("Ej. Se terminó la estructura principal") },
                                minLines = 2,
                                maxLines = 4
                            )

                            SelectorImagenesProyecto(
                                imagenes = fotosAvance,
                                onImagenesChange = { fotosAvance = it },
                                tituloBoton = "Agregar fotos del avance",
                                maximo = 8
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.registrarAvanceProyecto(
                                    proyectoId = proyecto.id,
                                    avance = avanceTemporal,
                                    comentario = comentarioAvance,
                                    fotos = fotosAvance
                                )
                                mostrarDialogoAvance = false
                                comentarioAvance = ""
                                fotosAvance = emptyList()
                            }
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { mostrarDialogoAvance = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
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
                        containerColor = MaterialTheme.arcshiftColors.success
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
                        containerColor = MaterialTheme.colorScheme.error
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
            containerColor = MaterialTheme.colorScheme.surface
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
fun EncabezadoSeccionContraibleProyecto(
    titulo: String,
    resumen: String,
    icono: ImageVector,
    expandido: Boolean,
    onCambiarEstado: () -> Unit,
    textoAccion: String? = null,
    onAccion: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onCambiarEstado)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(11.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = resumen,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (textoAccion != null && onAccion != null) {
            TextButton(onClick = onAccion) {
                Text(textoAccion)
            }
        }

        IconButton(onClick = onCambiarEstado) {
            Icon(
                imageVector = if (expandido) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = if (expandido) {
                    "Contraer sección"
                } else {
                    "Desplegar sección"
                },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SeccionHistorialAvancesProyecto(
    avances: List<ProyectoAvanceEntity>,
    onAbrirFoto: (ImagenProyectoSeleccionada) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    var mostrarTodos by remember { mutableStateOf(false) }
    val avancesVisibles = if (mostrarTodos) avances else avances.take(3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            EncabezadoSeccionContraibleProyecto(
                titulo = "Historial de avances",
                resumen = when {
                    avances.isEmpty() -> "Sin avances registrados"
                    avances.size == 1 -> "1 actualización registrada"
                    else -> "${avances.size} actualizaciones registradas"
                },
                icono = Icons.Default.History,
                expandido = expandido,
                onCambiarEstado = { expandido = !expandido }
            )

            if (expandido) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(10.dp))

                if (avances.isEmpty()) {
                    Text(
                        text = "Todavía no hay actualizaciones registradas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    avancesVisibles.forEachIndexed { index, avance ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Avance ${avance.porcentaje}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = avance.fecha,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = {
                                        avance.porcentaje.coerceIn(0, 100) / 100f
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.outlineVariant
                                )

                                if (avance.comentario.isNotBlank()) {
                                    Text(
                                        text = avance.comentario,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                val fotos = remember(avance.fotosJson) {
                                    deserializarImagenesProyecto(avance.fotosJson)
                                }

                                if (fotos.isNotEmpty()) {
                                    GaleriaImagenesProyecto(
                                        imagenes = fotos,
                                        onAbrir = onAbrirFoto
                                    )
                                }
                            }
                        }

                        if (index < avancesVisibles.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (avances.size > 3) {
                        TextButton(
                            onClick = { mostrarTodos = !mostrarTodos },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (mostrarTodos) {
                                    "Mostrar menos"
                                } else {
                                    "Ver historial completo (${avances.size})"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeccionEmpleadosProyecto(
    empleados: List<ProyectoEmpleadoEntity>,
    onAgregarEmpleado: () -> Unit,
    onEditarEmpleado: (ProyectoEmpleadoEntity) -> Unit,
    onEliminarEmpleado: (ProyectoEmpleadoEntity) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    var mostrarTodos by remember { mutableStateOf(false) }
    val empleadosVisibles = if (mostrarTodos) empleados else empleados.take(3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            EncabezadoSeccionContraibleProyecto(
                titulo = "Empleados asignados",
                resumen = when {
                    empleados.isEmpty() -> "Sin empleados asignados"
                    empleados.size == 1 -> "1 empleado asignado"
                    else -> "${empleados.size} empleados asignados"
                },
                icono = Icons.Default.Groups,
                expandido = expandido,
                onCambiarEstado = { expandido = !expandido },
                textoAccion = "Agregar",
                onAccion = onAgregarEmpleado
            )

            if (expandido) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                if (empleados.isEmpty()) {
                    Text(
                        text = "No hay empleados asignados.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    empleadosVisibles.forEach { empleado ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.background
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = empleado.nombreEmpleado,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Text(
                                            text = empleado.puesto.ifBlank { "Sin puesto" },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row {
                                        TextButton(
                                            onClick = { onEditarEmpleado(empleado) }
                                        ) {
                                            Text("Editar")
                                        }

                                        IconButton(
                                            onClick = { onEliminarEmpleado(empleado) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar empleado",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ChipDatoProyecto(
                                        titulo = "Pago",
                                        valor = empleado.tipoPago.ifBlank {
                                            "No definido"
                                        },
                                        modifier = Modifier.weight(1f)
                                    )

                                    ChipDatoProyecto(
                                        titulo = when (empleado.tipoPago) {
                                            "Semana" -> "Semanas"
                                            "Hora" -> "Horas"
                                            "Porcentaje" -> "Porcentaje"
                                            else -> "Días"
                                        },
                                        valor = when (empleado.tipoPago) {
                                            "Hora" -> empleado.horasTrabajadas.toString()
                                            "Porcentaje" -> "${empleado.porcentaje}%"
                                            else -> empleado.diasTrabajados.toString()
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Costo calculado",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Text(
                                        text = formatoMonedaProyecto(
                                            empleado.costoCalculado
                                        ),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    if (empleados.size > 3) {
                        TextButton(
                            onClick = { mostrarTodos = !mostrarTodos },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (mostrarTodos) {
                                    "Mostrar menos"
                                } else {
                                    "Ver todos (${empleados.size})"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChipDatoProyecto(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun SeccionMaterialesProyecto(
    materiales: List<ProyectoMaterialEntity>,
    onRegistrarMaterial: () -> Unit,
    onEliminarMaterial: (ProyectoMaterialEntity) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    var mostrarTodos by remember { mutableStateOf(false) }
    val materialesVisibles = if (mostrarTodos) materiales else materiales.take(3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            EncabezadoSeccionContraibleProyecto(
                titulo = "Material usado",
                resumen = when {
                    materiales.isEmpty() -> "Sin materiales registrados"
                    materiales.size == 1 -> "1 material registrado"
                    else -> "${materiales.size} materiales registrados"
                },
                icono = Icons.Default.Inventory,
                expandido = expandido,
                onCambiarEstado = { expandido = !expandido },
                textoAccion = "Registrar",
                onAccion = onRegistrarMaterial
            )

            if (expandido) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                if (materiales.isEmpty()) {
                    Text(
                        text = "No hay material registrado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    materialesVisibles.forEach { material ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            color = MaterialTheme.arcshiftColors.successContainer,
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Inventory,
                                        contentDescription = null,
                                        tint = MaterialTheme.arcshiftColors.success
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = material.nombreProducto,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = "${material.cantidadUsada} ${material.unidad} x ${formatoMonedaProyecto(material.costoUnitario)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = "Subtotal: ${formatoMonedaProyecto(material.subtotal)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.arcshiftColors.onSuccessContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(
                                    onClick = { onEliminarMaterial(material) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar material",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }

                    if (materiales.size > 3) {
                        TextButton(
                            onClick = { mostrarTodos = !mostrarTodos },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (mostrarTodos) {
                                    "Mostrar menos"
                                } else {
                                    "Ver todos (${materiales.size})"
                                }
                            )
                        }
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
            containerColor = MaterialTheme.colorScheme.surface
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
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = proyecto.cliente,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                trackColor = MaterialTheme.colorScheme.outlineVariant
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
            containerColor = MaterialTheme.colorScheme.surface
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

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
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
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
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
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
            containerColor = MaterialTheme.colorScheme.background
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionCliente(
                titulo = "Acciones rápidas",
                icono = Icons.Default.Bolt,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonRapidoInferiorProyecto(
                    titulo = "Editar",
                    icono = Icons.Default.Edit,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = onEditar
                )

                BotonRapidoInferiorProyecto(
                    titulo = "Terminar",
                    icono = Icons.Default.Check,
                    color = MaterialTheme.arcshiftColors.success,
                    modifier = Modifier.weight(1f),
                    onClick = onTerminar
                )

                BotonRapidoInferiorProyecto(
                    titulo = "Eliminar",
                    icono = Icons.Default.Delete,
                    color = MaterialTheme.colorScheme.error,
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun SeccionGastosProyecto(
    gastos: List<GastoEntity>,
    onAgregarGasto: () -> Unit,
    onEliminarGasto: (GastoEntity) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    var mostrarTodos by remember { mutableStateOf(false) }
    val gastosVisibles = if (mostrarTodos) gastos else gastos.take(3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            EncabezadoSeccionContraibleProyecto(
                titulo = "Gastos del proyecto",
                resumen = when {
                    gastos.isEmpty() -> "Sin gastos relacionados"
                    gastos.size == 1 -> "1 gasto relacionado"
                    else -> "${gastos.size} gastos relacionados"
                },
                icono = Icons.Default.AttachMoney,
                expandido = expandido,
                onCambiarEstado = { expandido = !expandido },
                textoAccion = "Agregar",
                onAccion = onAgregarGasto
            )

            if (expandido) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                if (gastos.isEmpty()) {
                    Text(
                        text = "No hay gastos relacionados con este proyecto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    gastosVisibles.forEach { gasto ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.arcshiftColors.warningContainer
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.arcshiftColors.warningContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = gasto.concepto,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Text(
                                        text = "${gasto.categoria} | ${gasto.fecha}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.arcshiftColors.onWarningContainer
                                    )

                                    if (!gasto.proveedor.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Proveedor: ${gasto.proveedor}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = formatoMonedaProyecto(gasto.total),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.arcshiftColors.onWarningContainer
                                    )
                                }

                                IconButton(
                                    onClick = { onEliminarGasto(gasto) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar gasto",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    if (gastos.size > 3) {
                        TextButton(
                            onClick = { mostrarTodos = !mostrarTodos },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (mostrarTodos) {
                                    "Mostrar menos"
                                } else {
                                    "Ver todos (${gastos.size})"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
