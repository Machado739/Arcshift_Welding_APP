package com.example.arcshiftwelding.ui.Screen.clientes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.ui.components.AvisoValidacionFormulario
import com.example.arcshiftwelding.ui.components.mostrarErrorEnCampo
import com.example.arcshiftwelding.ui.components.rememberEstadoValidacionFormulario
import com.example.arcshiftwelding.ui.components.rememberSnackbarValidacion
import com.example.arcshiftwelding.ui.theme.arcshiftColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NuevoClienteScreen(
    navController: NavController,
    viewModel: ClientesViewModel
) {
    val context = LocalContext.current
    var fotoUri by remember { mutableStateOf("") }

    val seleccionarFotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            fotoUri = conservarPermisoFotoCliente(context, uri)
        }
    }

    var nombre by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var tipoCliente by remember { mutableStateOf("") }
    var estatus by remember { mutableStateOf("Activo") }

    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    var rfc by remember { mutableStateOf("") }
    var personaContacto by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var clienteActivo by remember { mutableStateOf(true) }
    var recibeCotizaciones by remember { mutableStateOf(true) }
    var contactoWhatsapp by remember { mutableStateOf(true) }
    var contactoLlamadas by remember { mutableStateOf(true) }
    var contactoCorreo by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val validacion = rememberEstadoValidacionFormulario()
    val snackbarValidacion = rememberSnackbarValidacion(validacion)
    val personalBring = remember { BringIntoViewRequester() }
    val contactoBring = remember { BringIntoViewRequester() }

    fun mostrarErrorCliente(mensaje: String, destino: BringIntoViewRequester) {
        mostrarErrorEnCampo(
            scope = scope,
            estado = validacion,
            mensaje = mensaje,
            bringIntoViewRequester = destino
        )
    }

    Scaffold(
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarValidacion) },
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
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
                Text(
                    text = "Nuevo Cliente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(modifier = Modifier.bringIntoViewRequester(personalBring)) {
                SeccionInformacionPersonalCliente(
                nombre = nombre,
                onNombreChange = { nombre = it },
                empresa = empresa,
                onEmpresaChange = { empresa = it },
                tipoCliente = tipoCliente,
                onTipoClienteChange = { tipoCliente = it },
                estatus = estatus,
                onEstatusChange = { estatus = it },
                fotoUri = fotoUri,
                onSeleccionarFotoClick = {
                    seleccionarFotoLauncher.launch(arrayOf("image/*"))
                },
                onEliminarFotoClick = {
                    fotoUri = ""
                }
                )
            }

            Box(modifier = Modifier.bringIntoViewRequester(contactoBring)) {
                SeccionInformacionContactoCliente(
                telefono = telefono,
                onTelefonoChange = { telefono = it },
                correo = correo,
                onCorreoChange = { correo = it },
                direccion = direccion,
                onDireccionChange = { direccion = it }
                )
            }

            SeccionInformacionAdicionalNuevoCliente(
                rfc = rfc,
                onRfcChange = { rfc = it },
                personaContacto = personaContacto,
                onPersonaContactoChange = { personaContacto = it },
                cargo = cargo,
                onCargoChange = { cargo = it },
                notas = notas,
                onNotasChange = { notas = it }
            )
            /*
                        SeccionConfiguracionNuevoCliente(
                            clienteActivo = clienteActivo,
                            onClienteActivoChange = { clienteActivo = it },
                            recibeCotizaciones = recibeCotizaciones,
                            onRecibeCotizacionesChange = { recibeCotizaciones = it },
                            contactoWhatsapp = contactoWhatsapp,
                            onContactoWhatsappChange = { contactoWhatsapp = it },
                            contactoLlamadas = contactoLlamadas,
                            onContactoLlamadasChange = { contactoLlamadas = it },
                            contactoCorreo = contactoCorreo,
                            onContactoCorreoChange = { contactoCorreo = it }
                        )*/

            AvisoValidacionFormulario(validacion)

            BotonesFormularioCliente(
                onCancelarClick = {
                    navController.popBackStack()
                },
                onGuardarClick = {
                    validacion.limpiar()
                    when {
                        nombre.isBlank() -> {
                            mostrarErrorCliente("El nombre completo es obligatorio.", personalBring)
                            return@BotonesFormularioCliente
                        }
                        tipoCliente.isBlank() -> {
                            mostrarErrorCliente("Selecciona el tipo de cliente.", personalBring)
                            return@BotonesFormularioCliente
                        }
                        estatus.isBlank() -> {
                            mostrarErrorCliente("Selecciona el estatus del cliente.", personalBring)
                            return@BotonesFormularioCliente
                        }
                        telefono.isBlank() -> {
                            mostrarErrorCliente("El teléfono es obligatorio.", contactoBring)
                            return@BotonesFormularioCliente
                        }
                    }

                    viewModel.guardarCliente(
                        nombre = nombre,
                        empresa = empresa,
                        tipoCliente = tipoCliente,
                        estatus = estatus,
                        telefono = telefono,
                        correo = correo,
                        direccion = direccion,
                        rfc = rfc,
                        personaContacto = personaContacto,
                        cargo = cargo,
                        notas = notas,
                        fotoUri = fotoUri,
                        clienteActivo = clienteActivo,
                        recibeCotizaciones = recibeCotizaciones,
                        contactoWhatsapp = contactoWhatsapp,
                        contactoLlamadas = contactoLlamadas,
                        contactoCorreo = contactoCorreo
                    ) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}


@Composable
fun SeccionInformacionPersonalCliente(
    nombre: String,
    onNombreChange: (String) -> Unit,
    empresa: String,
    onEmpresaChange: (String) -> Unit,
    tipoCliente: String,
    onTipoClienteChange: (String) -> Unit,
    estatus: String,
    onEstatusChange: (String) -> Unit,
    fotoUri: String = "",
    onSeleccionarFotoClick: () -> Unit = {},
    onEliminarFotoClick: () -> Unit = {}
) {
    CardFormularioCliente(
        titulo = "Información personal",
        icono = Icons.Default.Person,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AgregarFotoCliente(
                fotoUri = fotoUri,
                nombreCliente = nombre,
                onSeleccionarClick = onSeleccionarFotoClick,
                onEliminarClick = onEliminarFotoClick,
                modifier = Modifier.width(105.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoTextoCliente(
                    valor = nombre,
                    onValorChange = onNombreChange,
                    titulo = "Nombre completo",
                    placeholder = "Ej. Juan Pérez",
                    requerido = true
                )

                CampoTextoCliente(
                    valor = empresa,
                    onValorChange = onEmpresaChange,
                    titulo = "Empresa",
                    placeholder = "Ej. Aceros del Norte S.A. de C.V.",
                    requerido = false
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SelectorSimpleCliente(
                titulo = "Tipo de cliente",
                valor = tipoCliente,
                opciones = listOf("Empresa", "Persona física", "Cliente general"),
                onValorChange = onTipoClienteChange,
                requerido = true,
                modifier = Modifier.weight(1f)
            )

            SelectorSimpleCliente(
                titulo = "Estatus",
                valor = estatus,
                opciones = listOf("Activo", "Inactivo", "Pendiente"),
                onValorChange = onEstatusChange,
                requerido = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AgregarFotoCliente(
    fotoUri: String,
    nombreCliente: String,
    onSeleccionarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            ImagenPerfilCliente(
                fotoUri = fotoUri,
                iniciales = obtenerIniciales(nombreCliente),
                modifier = Modifier
                    .size(82.dp)
                    .clickable(onClick = onSeleccionarClick)
            )

            if (fotoUri.isNotBlank()) {
                SmallFloatingActionButton(
                    onClick = onEliminarClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp),
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Quitar foto",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        TextButton(
            onClick = onSeleccionarClick,
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (fotoUri.isBlank()) "Seleccionar foto" else "Cambiar foto",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SeccionInformacionContactoCliente(
    telefono: String,
    onTelefonoChange: (String) -> Unit,
    correo: String,
    onCorreoChange: (String) -> Unit,
    direccion: String,
    onDireccionChange: (String) -> Unit
) {
    CardFormularioCliente(
        titulo = "Información de contacto",
        icono = Icons.Default.Phone,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTextoCliente(
                valor = telefono,
                onValorChange = onTelefonoChange,
                titulo = "Teléfono",
                placeholder = "Ej. 614 123 4567",
                requerido = true,
                leadingIcon = Icons.Default.Phone,
                modifier = Modifier.weight(1f)
            )

            CampoTextoCliente(
                valor = correo,
                onValorChange = onCorreoChange,
                titulo = "Correo electrónico",
                placeholder = "Ej. correo@ejemplo.com",
                requerido = false,
                leadingIcon = Icons.Default.Email,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoCliente(
            valor = direccion,
            onValorChange = onDireccionChange,
            titulo = "Dirección",
            placeholder = "Ej. Av. Tecnológico 123, Chihuahua, Chih.",
            requerido = false,
            leadingIcon = Icons.Default.LocationOn
        )
    }
}

@Composable
fun SeccionInformacionAdicionalNuevoCliente(
    rfc: String,
    onRfcChange: (String) -> Unit,
    personaContacto: String,
    onPersonaContactoChange: (String) -> Unit,
    cargo: String,
    onCargoChange: (String) -> Unit,
    notas: String,
    onNotasChange: (String) -> Unit
) {
    CardFormularioCliente(
        titulo = "Información adicional",
        icono = Icons.Default.Description,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTextoCliente(
                valor = rfc,
                onValorChange = onRfcChange,
                titulo = "RFC",
                placeholder = "Ej. XAXX010101000",
                requerido = false,
                modifier = Modifier.weight(1f)
            )

            CampoTextoCliente(
                valor = personaContacto,
                onValorChange = onPersonaContactoChange,
                titulo = "Persona de contacto",
                placeholder = "Ej. Ing. Juan Pérez",
                requerido = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoCliente(
            valor = cargo,
            onValorChange = onCargoChange,
            titulo = "Cargo",
            placeholder = "Ej. Gerente de proyectos",
            requerido = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoCliente(
            valor = notas,
            onValorChange = {
                if (it.length <= 200) onNotasChange(it)
            },
            titulo = "Notas",
            placeholder = "Notas sobre el cliente...",
            requerido = false,
            maxLines = 4,
            minHeight = 90.dp
        )

        Text(
            text = "${notas.length}/200",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun SeccionConfiguracionNuevoCliente(
    clienteActivo: Boolean,
    onClienteActivoChange: (Boolean) -> Unit,
    recibeCotizaciones: Boolean,
    onRecibeCotizacionesChange: (Boolean) -> Unit,
    contactoWhatsapp: Boolean,
    onContactoWhatsappChange: (Boolean) -> Unit,
    contactoLlamadas: Boolean,
    onContactoLlamadasChange: (Boolean) -> Unit,
    contactoCorreo: Boolean,
    onContactoCorreoChange: (Boolean) -> Unit
) {
    CardFormularioCliente(
        titulo = "Configuración",
        icono = Icons.Default.Settings,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OpcionCheckCliente(
                titulo = "Cliente activo",
                subtitulo = "El cliente podrá aparecer en listados.",
                checked = clienteActivo,
                onCheckedChange = onClienteActivoChange,
                modifier = Modifier.weight(1f)
            )

            OpcionCheckCliente(
                titulo = "Recibir cotizaciones",
                subtitulo = "Permitir enviar cotizaciones.",
                checked = recibeCotizaciones,
                onCheckedChange = onRecibeCotizacionesChange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Preferencias de contacto",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CheckPreferenciaContacto(
                texto = "WhatsApp",
                checked = contactoWhatsapp,
                onCheckedChange = onContactoWhatsappChange
            )

            CheckPreferenciaContacto(
                texto = "Llamadas",
                checked = contactoLlamadas,
                onCheckedChange = onContactoLlamadasChange
            )

            CheckPreferenciaContacto(
                texto = "Correo electrónico",
                checked = contactoCorreo,
                onCheckedChange = onContactoCorreoChange
            )
        }
    }
}

@Composable
fun BotonesFormularioCliente(
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
                containerColor = MaterialTheme.arcshiftColors.success
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Guardar Cliente",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CardFormularioCliente(
    titulo: String,
    icono: ImageVector,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
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

@Composable
fun CampoTextoCliente(
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
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else null,
            shape = RoundedCornerShape(10.dp),
            singleLine = maxLines == 1,
            maxLines = maxLines,
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
fun SelectorSimpleCliente(
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
                        text = "Seleccionar tipo",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
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
fun OpcionCheckCliente(
    titulo: String,
    subtitulo: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
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
fun CheckPreferenciaContacto(
    texto: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(36.dp)
        )

        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}