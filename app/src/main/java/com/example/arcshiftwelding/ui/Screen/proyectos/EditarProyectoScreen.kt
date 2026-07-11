package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProyectoScreen(
    navController: NavController,
    viewModel: ProyectosViewModel,
    proyectoId: Int
) {
    val proyectos by viewModel.proyectos.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val cotizaciones by viewModel.cotizaciones.collectAsState()

    val proyectoActual = proyectos.find { it.id == proyectoId }

    var datosCargados by remember(proyectoId) { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var clienteSeleccionado by remember { mutableStateOf<ClienteEntity?>(null) }
    var cotizacionSeleccionada by remember { mutableStateOf<CotizacionProyectoUI?>(null) }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaEstimadaFin by remember { mutableStateOf("") }
    var fechaFinReal by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Pendiente") }
    var avance by remember { mutableStateOf(0f) }
    var presupuestoEstimado by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var imagenesProyecto by remember { mutableStateOf(emptyList<ImagenProyectoSeleccionada>()) }

    LaunchedEffect(proyectoActual, clientes, cotizaciones) {
        if (proyectoActual != null && !datosCargados) {
            nombre = proyectoActual.nombre
            descripcion = proyectoActual.descripcion
            clienteSeleccionado = clientes.find { it.id == proyectoActual.clienteId }
            cotizacionSeleccionada = cotizaciones.find { it.id == proyectoActual.cotizacionId }
            fechaInicio = proyectoActual.fechaInicio
            fechaEstimadaFin = proyectoActual.fechaEstimadaFin
            fechaFinReal = proyectoActual.fechaFinReal
            estado = proyectoActual.estado
            avance = proyectoActual.avance.toFloat()
            presupuestoEstimado = if (proyectoActual.presupuestoEstimado == 0.0) {
                ""
            } else {
                proyectoActual.presupuestoEstimado.toString()
            }
            observaciones = proyectoActual.observaciones
            imagenesProyecto = deserializarImagenesProyecto(proyectoActual.imagenesJson)
            datosCargados = true
        }
    }

    val cotizacionesFiltradas = if (clienteSeleccionado == null) {
        cotizaciones
    } else {
        cotizaciones.filter { it.clienteId == clienteSeleccionado?.id }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar proyecto",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        if (proyectoActual == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Proyecto no encontrado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("Regresar")
                }
            }

            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .navigationBarsPadding()
                .imePadding(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CardSeccionProyecto(
                    titulo = "Información general",
                    icono = Icons.Default.Work
                ) {
                    CampoTextoEditarProyecto(
                        valor = nombre,
                        onValorChange = { nombre = it },
                        titulo = "Nombre del proyecto",
                        placeholder = "Ej. Portón industrial",
                        requerido = true,
                        icono = Icons.Default.Work
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CampoTextoEditarProyecto(
                        valor = descripcion,
                        onValorChange = { descripcion = it },
                        titulo = "Descripción",
                        placeholder = "Describe el trabajo a realizar",
                        requerido = false,
                        icono = Icons.Default.Description,
                        maxLines = 3
                    )
                }
            }

            item {
                CardSeccionProyecto(
                    titulo = "Cliente y cotización",
                    icono = Icons.Default.Person
                ) {
                    SelectorClienteEditarProyecto(
                        clientes = clientes,
                        clienteSeleccionado = clienteSeleccionado,
                        onClienteSeleccionado = {
                            clienteSeleccionado = it
                            cotizacionSeleccionada = null
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectorCotizacionEditarProyecto(
                        cotizaciones = cotizacionesFiltradas,
                        cotizacionSeleccionada = cotizacionSeleccionada,
                        onCotizacionSeleccionada = { cotizacion ->
                            cotizacionSeleccionada = cotizacion

                            val clienteCotizacion = clientes.find {
                                it.id == cotizacion.clienteId
                            }

                            if (clienteCotizacion != null) {
                                clienteSeleccionado = clienteCotizacion
                            }

                            if (presupuestoEstimado.isBlank()) {
                                presupuestoEstimado = cotizacion.total.toString()
                            }
                        },
                        onSinCotizacion = {
                            cotizacionSeleccionada = null
                        }
                    )
                }
            }

            item {
                CardSeccionProyecto(
                    titulo = "Programación y avance",
                    icono = Icons.Default.DateRange
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CampoFechaEditarProyecto(
                            valor = fechaInicio,
                            onFechaSeleccionada = { fechaInicio = it },
                            titulo = "Inicio",
                            modifier = Modifier.weight(1f)
                        )

                        CampoFechaEditarProyecto(
                            valor = fechaEstimadaFin,
                            onFechaSeleccionada = { fechaEstimadaFin = it },
                            titulo = "Entrega",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    CampoFechaEditarProyecto(
                        valor = fechaFinReal,
                        onFechaSeleccionada = { fechaFinReal = it },
                        titulo = "Fecha final real",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectorEstadoEditarProyecto(
                        estado = estado,
                        onEstadoChange = { nuevoEstado ->
                            estado = nuevoEstado

                            avance = when (nuevoEstado) {
                                "Pendiente" -> 10f
                                "En trabajo" -> 55f
                                "Terminado" -> 100f
                                "Cancelado" -> 0f
                                else -> avance
                            }

                            if (nuevoEstado == "Terminado" && fechaFinReal.isBlank()) {
                                fechaFinReal = obtenerFechaActualProyectoSistema()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Avance: ${avance.toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Slider(
                        value = avance,
                        onValueChange = { avance = it },
                        valueRange = 0f..100f
                    )

                    LinearProgressIndicator(
                        progress = { avance / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp),
                        color = colorEstadoProyecto(estado),
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }

            item {
                CardSeccionProyecto(
                    titulo = "Presupuesto y observaciones",
                    icono = Icons.Default.AttachMoney
                ) {
                    CampoTextoEditarProyecto(
                        valor = presupuestoEstimado,
                        onValorChange = {
                            presupuestoEstimado = it
                                .replace("${'$'}", "")
                                .replace(",", "")
                        },
                        titulo = "Presupuesto estimado",
                        placeholder = "Ej. ${'$'}18500",
                        requerido = false,
                        icono = Icons.Default.AttachMoney,
                        keyboardType = KeyboardType.Decimal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CampoTextoEditarProyecto(
                        valor = observaciones,
                        onValorChange = { observaciones = it },
                        titulo = "Observaciones",
                        placeholder = "Notas internas del proyecto",
                        requerido = false,
                        icono = Icons.Default.Description,
                        maxLines = 4
                    )
                }
            }

            item {
                CardSeccionProyecto(
                    titulo = "Imágenes del proyecto",
                    icono = Icons.Default.Image
                ) {
                    Text(
                        text = "Puedes agregar, reemplazar o quitar fotografías del proyecto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectorImagenesProyecto(
                        imagenes = imagenesProyecto,
                        onImagenesChange = { imagenesProyecto = it }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val clienteId = clienteSeleccionado?.id ?: return@Button

                            viewModel.actualizarProyecto(
                                id = proyectoActual.id,
                                nombre = nombre,
                                clienteId = clienteId,
                                cotizacionId = cotizacionSeleccionada?.id,
                                descripcion = descripcion,
                                fechaInicio = fechaInicio,
                                fechaEstimadaFin = fechaEstimadaFin,
                                fechaFinReal = fechaFinReal,
                                estado = estado,
                                avance = avance.toInt(),
                                presupuestoEstimado = presupuestoEstimado.toDoubleOrNull() ?: 0.0,
                                costoMaterial = proyectoActual.costoMaterial,
                                costoManoObra = proyectoActual.costoManoObra,
                                costoTotal = proyectoActual.costoTotal,
                                observaciones = observaciones,
                                imagenesJson = serializarImagenesProyecto(imagenesProyecto)
                            )

                            navController.popBackStack()
                        },
                        enabled = nombre.trim().isNotEmpty() && clienteSeleccionado != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Actualizar")
                    }
                }
            }
        }
    }
}

@Composable
fun CampoTextoEditarProyecto(
    valor: String,
    onValorChange: (String) -> Unit,
    titulo: String,
    placeholder: String,
    requerido: Boolean,
    icono: ImageVector,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Row {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (requerido) {
                Text(
                    text = " *",
                    color = Color.Red,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder)
            },
            leadingIcon = {
                Icon(
                    imageVector = icono,
                    contentDescription = null
                )
            },
            maxLines = maxLines,
            singleLine = maxLines == 1,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            )
        )
    }
}

@Composable
fun SelectorClienteEditarProyecto(
    clientes: List<ClienteEntity>,
    clienteSeleccionado: ClienteEntity?,
    onClienteSeleccionado: (ClienteEntity) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Cliente *",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { expandido = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = clienteSeleccionado?.nombre ?: "Seleccionar cliente"
            )
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            clientes.forEach { cliente ->
                DropdownMenuItem(
                    text = { Text(cliente.nombre) },
                    onClick = {
                        onClienteSeleccionado(cliente)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
fun SelectorCotizacionEditarProyecto(
    cotizaciones: List<CotizacionProyectoUI>,
    cotizacionSeleccionada: CotizacionProyectoUI?,
    onCotizacionSeleccionada: (CotizacionProyectoUI) -> Unit,
    onSinCotizacion: () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Cotización relacionada",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { expandido = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = cotizacionSeleccionada?.texto ?: "Sin cotización"
            )
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin cotización") },
                onClick = {
                    onSinCotizacion()
                    expandido = false
                }
            )

            cotizaciones.forEach { cotizacion ->
                DropdownMenuItem(
                    text = { Text(cotizacion.texto) },
                    onClick = {
                        onCotizacionSeleccionada(cotizacion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
fun SelectorEstadoEditarProyecto(
    estado: String,
    onEstadoChange: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    val estados = listOf(
        "Pendiente",
        "En trabajo",
        "Terminado",
        "Cancelado"
    )

    Column {
        Text(
            text = "Estado",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { expandido = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(estado)
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            estados.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onEstadoChange(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaEditarProyecto(
    valor: String,
    onFechaSeleccionada: (String) -> Unit,
    titulo: String,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = modifier
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { mostrarCalendario = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null
            )

            Text(
                text = valor.ifBlank { "Seleccionar" },
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val fechaSeleccionada = datePickerState.selectedDateMillis

                        if (fechaSeleccionada != null) {
                            onFechaSeleccionada(
                                formatearFechaEditarProyecto(fechaSeleccionada)
                            )
                        }

                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarCalendario = false }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

fun formatearFechaEditarProyecto(millis: Long): String {
    val calendario = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    val dia = calendario.get(Calendar.DAY_OF_MONTH)
    val mes = calendario.get(Calendar.MONTH) + 1
    val anio = calendario.get(Calendar.YEAR)

    return "%02d/%02d/%04d".format(dia, mes, anio)
}

fun obtenerFechaActualProyectoSistema(): String {
    return java.text.SimpleDateFormat(
        "dd/MM/yyyy",
        java.util.Locale.getDefault()
    ).format(java.util.Date())
}
