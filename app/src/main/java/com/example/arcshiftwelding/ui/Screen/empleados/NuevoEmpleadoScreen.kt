package com.example.arcshiftwelding.ui.Screen.empleados

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoEmpleadoScreen(
    onBack: () -> Unit = {},
    onGuardar: () -> Unit = {},
    onCancelar: () -> Unit = {},
    navController: NavController,
    viewModel: EmpleadosViewModel

) {

    var nombre by remember { mutableStateOf("") }
    var puesto by remember { mutableStateOf("") }
    var estatus by remember { mutableStateOf("Activo") }

    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var fechaIngreso by remember { mutableStateOf("") }

    var tipoContrato by remember { mutableStateOf("") }
    var valorContrato by remember { mutableStateOf("") }
    var trabajoActual by remember { mutableStateOf("") }

    var notas by remember { mutableStateOf("") }

    var empleadoActivo by remember { mutableStateOf(true) }
    var asignarTrabajos by remember { mutableStateOf(true) }
    var pagoSemanalActivo by remember { mutableStateOf(true) }

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
                    onClick = {
                        onBack()
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }

                Text(
                    text = "Nuevo Empleado",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeccionInformacionPersonalNuevoEmpleado(
                nombre = nombre,
                onNombreChange = { nombre = it },
                puesto = puesto,
                onPuestoChange = { puesto = it },
                estatus = estatus,
                onEstatusChange = { estatus = it }
            )

            SeccionInformacionContactoEmpleado(
                telefono = telefono,
                onTelefonoChange = { telefono = it },
                correo = correo,
                onCorreoChange = { correo = it },
                direccion = direccion,
                onDireccionChange = { direccion = it }
            )

            SeccionInformacionLaboralNuevoEmpleado(
                fechaIngreso = fechaIngreso,
                onFechaIngresoChange = { fechaIngreso = it },
                tipoContrato = tipoContrato,
                onTipoContratoChange = { tipoContrato = it },
                valorContrato = valorContrato,
                onValorContratoChange = { valorContrato = it },
                trabajoActual = trabajoActual,
                onTrabajoActualChange = { trabajoActual = it }
            )

            SeccionNotasNuevoEmpleado(
                notas = notas,
                onNotasChange = { notas = it }
            )
            /*
                        SeccionConfiguracionNuevoEmpleado(
                            empleadoActivo = empleadoActivo,
                            onEmpleadoActivoChange = { empleadoActivo = it },
                            asignarTrabajos = asignarTrabajos,
                            onAsignarTrabajosChange = { asignarTrabajos = it },
                            pagoSemanalActivo = pagoSemanalActivo,
                            onPagoSemanalActivoChange = { pagoSemanalActivo = it }
                        )*/

            BotonesFormularioEmpleado(
                onCancelarClick = {
                    onCancelar()
                    navController.popBackStack()
                },
                onGuardarClick = {
                    val empleado = EmpleadoEntity(
                        nombre = nombre.trim(),
                        telefono = telefono.trim(),
                        correo = correo.trim(),
                        puesto = puesto.trim(),
                        salario = obtenerSalarioEmpleado(tipoContrato, valorContrato),
                        fechaIngreso = fechaIngreso.trim(),
                        activo = empleadoActivo && estatus == "Activo",
                        direccion = direccion.trim(),
                        porcentajeContrato = construirContratoEmpleado(tipoContrato, valorContrato),
                        trabajoActual = trabajoActual.trim(),
                        notas = notas.trim()
                    )

                    viewModel.insertarEmpleado(empleado)

                    onGuardar()
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun SeccionInformacionPersonalNuevoEmpleado(
    nombre: String,
    onNombreChange: (String) -> Unit,
    puesto: String,
    onPuestoChange: (String) -> Unit,
    estatus: String,
    onEstatusChange: (String) -> Unit
) {
    CardFormularioEmpleado(
        titulo = "Información personal",
        icono = Icons.Default.Person,
        color = Color(0xFF2563EB)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AgregarFotoEmpleado(
                modifier = Modifier.width(95.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoTextoEmpleado(
                    valor = nombre,
                    onValorChange = onNombreChange,
                    titulo = "Nombre completo",
                    placeholder = "Ej. Jaime Lozano",
                    requerido = true
                )

                SelectorSimpleEmpleado(
                    titulo = "Puesto",
                    valor = puesto,
                    opciones = listOf(
                        "Soldador",
                        "Ayudante General",
                        "Ayudante",
                        "Supervisor",
                        "Administrador"
                    ),
                    onValorChange = onPuestoChange,
                    requerido = true
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        SelectorSimpleEmpleado(
            titulo = "Estatus",
            valor = estatus,
            opciones = listOf("Activo", "Inactivo", "Pendiente"),
            onValorChange = onEstatusChange,
            requerido = true
        )
    }
}

@Composable
fun AgregarFotoEmpleado(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF2FF))
                .clickable {
                    // Aquí después puedes abrir cámara o galería
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Agregar foto",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Agregar foto",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SeccionInformacionContactoEmpleado(
    telefono: String,
    onTelefonoChange: (String) -> Unit,
    correo: String,
    onCorreoChange: (String) -> Unit,
    direccion: String,
    onDireccionChange: (String) -> Unit
) {
    CardFormularioEmpleado(
        titulo = "Información de contacto",
        icono = Icons.Default.Phone,
        color = Color(0xFF2563EB)
    ) {
        CampoTextoEmpleado(
            valor = telefono,
            onValorChange = onTelefonoChange,
            titulo = "Teléfono",
            placeholder = "Ej. 614 123 4567",
            requerido = true,
            leadingIcon = Icons.Default.Phone
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoEmpleado(
            valor = correo,
            onValorChange = onCorreoChange,
            titulo = "Correo electrónico",
            placeholder = "Ej. empleado@correo.com",
            requerido = false,
            leadingIcon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoEmpleado(
            valor = direccion,
            onValorChange = onDireccionChange,
            titulo = "Dirección",
            placeholder = "Ej. Chihuahua, Chihuahua",
            requerido = false,
            leadingIcon = Icons.Default.LocationOn
        )
    }
}

@Composable
fun SeccionInformacionLaboralNuevoEmpleado(
    fechaIngreso: String,
    onFechaIngresoChange: (String) -> Unit,
    tipoContrato: String,
    onTipoContratoChange: (String) -> Unit,
    valorContrato: String,
    onValorContratoChange: (String) -> Unit,
    trabajoActual: String,
    onTrabajoActualChange: (String) -> Unit
) {
    val mostrarValorContrato =
        tipoContrato.isNotBlank() && tipoContrato != "Sin definir"

    val tituloValorContrato = when (tipoContrato) {
        "% por trabajo" -> "Porcentaje por trabajo"
        "Pago por día" -> "Pago por día"
        "Pago por semana" -> "Pago por semana"
        else -> "Valor"
    }

    val placeholderValorContrato = when (tipoContrato) {
        "% por trabajo" -> "Ej. 20"
        "Pago por día" -> "Ej. 500"
        "Pago por semana" -> "Ej. 2500"
        else -> "Opcional"
    }

    CardFormularioEmpleado(
        titulo = "Información laboral",
        icono = Icons.Default.Work,
        color = Color(0xFF2563EB)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoFechaEmpleado(
                valor = fechaIngreso,
                onValorChange = onFechaIngresoChange,
                titulo = "Fecha ingreso",
                placeholder = "Seleccionar fecha",
                requerido = true,
                modifier = Modifier.weight(1f)
            )

            SelectorSimpleEmpleado(
                titulo = "Tipo de pago",
                valor = tipoContrato,
                opciones = listOf(
                    "Sin definir",
                    "% por trabajo",
                    "Pago por día",
                    "Pago por semana"
                ),
                onValorChange = { opcion ->
                    onTipoContratoChange(opcion)
                    onValorContratoChange("")
                },
                requerido = false,
                modifier = Modifier.weight(1f)
            )
        }

        if (mostrarValorContrato) {
            Spacer(modifier = Modifier.height(10.dp))

            CampoTextoEmpleado(
                valor = valorContrato,
                onValorChange = { nuevoValor ->
                    onValorContratoChange(
                        nuevoValor
                            .replace("$", "")
                            .replace("%", "")
                    )
                },
                titulo = tituloValorContrato,
                placeholder = placeholderValorContrato,
                requerido = false,
                leadingIcon = Icons.Default.AttachMoney
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoEmpleado(
            valor = trabajoActual,
            onValorChange = onTrabajoActualChange,
            titulo = "Trabajo actual",
            placeholder = "Ej. Tejaban 6x4m",
            requerido = false,
            leadingIcon = Icons.Default.Work
        )
    }
}

@Composable
fun SeccionNotasNuevoEmpleado(
    notas: String,
    onNotasChange: (String) -> Unit
) {
    CardFormularioEmpleado(
        titulo = "Notas",
        icono = Icons.Default.Payments,
        color = Color(0xFF2563EB)
    ) {
        CampoTextoEmpleado(
            valor = notas,
            onValorChange = {
                if (it.length <= 200) onNotasChange(it)
            },
            titulo = "Notas del empleado",
            placeholder = "Observaciones sobre el empleado...",
            requerido = false,
            maxLines = 4,
            minHeight = 90.dp
        )

        Text(
            text = "${notas.length}/200",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun SeccionConfiguracionNuevoEmpleado(
    empleadoActivo: Boolean,
    onEmpleadoActivoChange: (Boolean) -> Unit,
    asignarTrabajos: Boolean,
    onAsignarTrabajosChange: (Boolean) -> Unit,
    pagoSemanalActivo: Boolean,
    onPagoSemanalActivoChange: (Boolean) -> Unit
) {
    CardFormularioEmpleado(
        titulo = "Configuración",
        icono = Icons.Default.Settings,
        color = Color(0xFF2563EB)
    ) {
        OpcionCheckEmpleado(
            titulo = "Empleado activo",
            subtitulo = "El empleado aparecerá en los listados y podrá recibir trabajos.",
            checked = empleadoActivo,
            onCheckedChange = onEmpleadoActivoChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        OpcionCheckEmpleado(
            titulo = "Asignar a trabajos",
            subtitulo = "Permite relacionar al empleado con trabajos activos.",
            checked = asignarTrabajos,
            onCheckedChange = onAsignarTrabajosChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        OpcionCheckEmpleado(
            titulo = "Pago semanal",
            subtitulo = "Incluir al empleado en el cálculo de pagos semanales.",
            checked = pagoSemanalActivo,
            onCheckedChange = onPagoSemanalActivoChange
        )
    }
}

@Composable
fun BotonesFormularioEmpleado(
    onCancelarClick: () -> Unit,
    onGuardarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onCancelarClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Cancelar",
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onGuardarClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF16A34A)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Guardar",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CardFormularioEmpleado(
    titulo: String,
    icono: ImageVector,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaEmpleado(
    valor: String,
    onValorChange: (String) -> Unit,
    titulo: String,
    placeholder: String,
    requerido: Boolean,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = modifier
    ) {
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 54.dp)
                .clickable {
                    mostrarCalendario = true
                }
        ) {
            OutlinedTextField(
                value = valor,
                onValueChange = { },
                readOnly = true,
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color(0xFFE5E7EB),
                    disabledLabelColor = Color.DarkGray,
                    disabledTrailingIconColor = Color.DarkGray,
                    disabledPlaceholderColor = Color.Gray,
                    disabledContainerColor = Color.White,
                    disabledLeadingIconColor = Color.Gray
                )
            )
        }
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = {
                mostrarCalendario = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val fechaSeleccionada = datePickerState.selectedDateMillis

                        if (fechaSeleccionada != null) {
                            val formato = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )
                            formato.timeZone = TimeZone.getTimeZone("UTC")

                            onValorChange(
                                formato.format(Date(fechaSeleccionada))
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
                    onClick = {
                        mostrarCalendario = false
                    }
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

@Composable
fun CampoTextoEmpleado(
    valor: String,
    onValorChange: (String) -> Unit,
    titulo: String,
    placeholder: String,
    requerido: Boolean,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    maxLines: Int = 1,
    minHeight: Dp = 54.dp
) {
    Column(
        modifier = modifier
    ) {
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
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                }
            } else null,
            shape = RoundedCornerShape(10.dp),
            singleLine = maxLines == 1,
            maxLines = maxLines,
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun SelectorSimpleEmpleado(
    titulo: String,
    valor: String,
    opciones: List<String>,
    onValorChange: (String) -> Unit,
    requerido: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
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

        Box {
            OutlinedTextField(
                value = valor,
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clickable {
                        expanded = true
                    },
                placeholder = {
                    Text(
                        text = "Seleccionar",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            expanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = {
                            Text(opcion)
                        },
                        onClick = {
                            onValorChange(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OpcionCheckEmpleado(
    titulo: String,
    subtitulo: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

fun construirContratoEmpleado(
    tipoContrato: String,
    valorContrato: String
): String {
    val tipo = tipoContrato.trim()
    val valor = valorContrato
        .replace("$", "")
        .replace("%", "")
        .replace(",", "")
        .trim()

    if (tipo.isBlank() || tipo == "Sin definir") {
        return ""
    }

    if (valor.isBlank()) {
        return tipo
    }

    return when (tipo) {
        "% por trabajo" -> "$tipo: $valor%"
        "Pago por día" -> "$tipo: $$valor"
        "Pago por semana" -> "$tipo: $$valor"
        else -> "$tipo: $valor"
    }
}

fun obtenerTipoContratoEmpleado(contrato: String): String {
    val contratoLimpio = contrato.trim()

    return when {
        contratoLimpio.startsWith("% por trabajo") -> "% por trabajo"
        contratoLimpio.startsWith("Pago por día") -> "Pago por día"
        contratoLimpio.startsWith("Pago por semana") -> "Pago por semana"
        contratoLimpio.endsWith("%") -> "% por trabajo"
        contratoLimpio.isBlank() -> ""
        else -> ""
    }
}

fun obtenerValorContratoEmpleado(contrato: String): String {
    val contratoLimpio = contrato.trim()

    if (!contratoLimpio.contains(":")) {
        return ""
    }

    return contratoLimpio
        .substringAfter(":")
        .replace("$", "")
        .replace("%", "")
        .replace(",", "")
        .trim()
}

fun obtenerSalarioEmpleado(
    tipoContrato: String,
    valorContrato: String
): Double {
    if (tipoContrato == "% por trabajo") {
        return 0.0
    }

    return valorContrato
        .replace("$", "")
        .replace("%", "")
        .replace(",", "")
        .trim()
        .toDoubleOrNull() ?: 0.0
}