package com.example.arcshiftwelding.ui.Screen.clientes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoClienteScreen(
    onBack: () -> Unit = {},
    onGuardar: () -> Unit = {},
    onCancelar: () -> Unit = {},
    navController: NavController
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo Cliente",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
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
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
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

            SeccionInformacionPersonalCliente(
                nombre = nombre,
                onNombreChange = { nombre = it },
                empresa = empresa,
                onEmpresaChange = { empresa = it },
                tipoCliente = tipoCliente,
                onTipoClienteChange = { tipoCliente = it },
                estatus = estatus,
                onEstatusChange = { estatus = it }
            )

            SeccionInformacionContactoCliente(
                telefono = telefono,
                onTelefonoChange = { telefono = it },
                correo = correo,
                onCorreoChange = { correo = it },
                direccion = direccion,
                onDireccionChange = { direccion = it }
            )

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
            )

            BotonesFormularioCliente(
                onCancelarClick = {
                    onCancelar()
                    navController.popBackStack()
                },
                onGuardarClick = {
                    onGuardar()
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
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
    onEstatusChange: (String) -> Unit
) {
    CardFormularioCliente(
        titulo = "Información personal",
        icono = Icons.Default.Person,
        color = Color(0xFF2563EB)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AgregarFotoCliente(
                modifier = Modifier.width(95.dp)
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
                    // Aquí después puedes abrir galería o cámara
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
        color = Color(0xFF2563EB)
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
        color = Color(0xFF2563EB)
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
            color = Color.Gray,
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
        color = Color(0xFF2563EB)
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
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
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
                color = Color.Gray
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