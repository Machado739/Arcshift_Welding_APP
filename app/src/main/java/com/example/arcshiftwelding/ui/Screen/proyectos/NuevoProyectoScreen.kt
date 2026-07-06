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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoProyectoScreen(
    navController: NavController,
    viewModel: ProyectosViewModel,
    cotizacionId: Int? = null
){
    val cotizacionOrigen by remember(cotizacionId) {
        if (cotizacionId != null) {
            viewModel.obtenerCotizacionPorId(cotizacionId)
        } else {
            kotlinx.coroutines.flow.flowOf(null)
        }
    }.collectAsState(initial = null)
    val clientes by viewModel.clientes.collectAsState()
    val cotizaciones by viewModel.cotizaciones.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var clienteSeleccionado by remember { mutableStateOf<ClienteEntity?>(null) }
    var cotizacionSeleccionada by remember { mutableStateOf<CotizacionProyectoUI?>(null) }
    var fechaInicio by remember { mutableStateOf(fechaActualProyecto()) }
    var fechaEstimadaFin by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Pendiente") }
    var avance by remember { mutableStateOf(10f) }
    var presupuestoEstimado by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }


    var clienteId by remember { mutableStateOf<Int?>(null) }
    var fechaFin by remember { mutableStateOf("") }

    LaunchedEffect(cotizacionOrigen) {
        cotizacionOrigen?.let { cotizacion ->
            nombre = if (cotizacion.proyecto.isNotBlank()) {
                cotizacion.proyecto
            } else {
                cotizacion.descripcionTrabajo
            }

            clienteId = cotizacion.clienteId
            descripcion = cotizacion.descripcionTrabajo
            presupuestoEstimado = cotizacion.total.toString()
            fechaFin = cotizacion.vigencia
            observaciones = "Proyecto creado desde la cotización ${cotizacion.folio}"
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
                        text = "Nuevo proyecto",
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
                    CampoTextoProyecto(
                        valor = nombre,
                        onValorChange = { nombre = it },
                        titulo = "Nombre del proyecto",
                        placeholder = "Ej. Portón industrial",
                        requerido = true,
                        icono = Icons.Default.Work
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CampoTextoProyecto(
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
                    SelectorClienteProyectoNuevo(
                        clientes = clientes,
                        clienteSeleccionado = clienteSeleccionado,
                        onClienteSeleccionado = {
                            clienteSeleccionado = it
                            cotizacionSeleccionada = null
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectorCotizacionProyectoNuevo(
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
                        CampoFechaProyecto(
                            valor = fechaInicio,
                            onFechaSeleccionada = { fechaInicio = it },
                            titulo = "Inicio",
                            modifier = Modifier.weight(1f)
                        )

                        CampoFechaProyecto(
                            valor = fechaEstimadaFin,
                            onFechaSeleccionada = { fechaEstimadaFin = it },
                            titulo = "Entrega estimada",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SelectorEstadoProyectoNuevo(
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
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Avance inicial: ${avance.toInt()}%",
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
                    titulo = "Presupuesto",
                    icono = Icons.Default.AttachMoney
                ) {
                    CampoTextoProyecto(
                        valor = presupuestoEstimado,
                        onValorChange = {
                            presupuestoEstimado = it
                                .replace("$", "")
                                .replace(",", "")
                        },
                        titulo = "Presupuesto estimado",
                        placeholder = "Ej. ${'$'}18500",
                        requerido = false,
                        icono = Icons.Default.AttachMoney,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CampoTextoProyecto(
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

                            viewModel.registrarProyecto(
                                nombre = nombre,
                                clienteId = clienteId,
                                cotizacionId = cotizacionSeleccionada?.id,
                                descripcion = descripcion,
                                fechaInicio = fechaInicio,
                                fechaEstimadaFin = fechaEstimadaFin,
                                estado = estado,
                                avance = avance.toInt(),
                                presupuestoEstimado = presupuestoEstimado.toDoubleOrNull() ?: 0.0,
                                observaciones = observaciones
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

                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun CardSeccionProyecto(
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
            Row {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF2563EB)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            contenido()
        }
    }
}

@Composable
fun CampoTextoProyecto(
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
            placeholder = { Text(placeholder) },
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
fun SelectorClienteProyectoNuevo(
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
fun SelectorCotizacionProyectoNuevo(
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
fun SelectorEstadoProyectoNuevo(
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
fun CampoFechaProyecto(
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
                                formatearFechaProyecto(fechaSeleccionada)
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

fun formatearFechaProyecto(millis: Long): String {
    val calendario = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
    }

    val dia = calendario.get(Calendar.DAY_OF_MONTH)
    val mes = calendario.get(Calendar.MONTH) + 1
    val anio = calendario.get(Calendar.YEAR)

    return "%02d/%02d/%04d".format(dia, mes, anio)
}

fun fechaActualProyecto(): String {
    return SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    ).format(Date())
}