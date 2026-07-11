package com.example.arcshiftwelding.ui.Screen.clientes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

data class ClienteEditarUI(
    val id: Int,
    val nombre: String,
    val empresa: String,
    val tipoCliente: String,
    val estatus: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val rfc: String,
    val personaContacto: String,
    val cargo: String,
    val notas: String,
    val clienteActivo: Boolean,
    val recibeCotizaciones: Boolean,
    val contactoWhatsapp: Boolean,
    val contactoLlamadas: Boolean,
    val contactoCorreo: Boolean,
    val fotoUri: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarClienteScreen(
    navController: NavController,
    clienteId: Int,
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

    val clienteFlow = remember(clienteId) {
        viewModel.obtenerClienteEditar(clienteId)
    }

    val clienteState by clienteFlow.collectAsState()
    val clienteActual = clienteState

    if (clienteActual == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val cliente = clienteActual!!

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

    LaunchedEffect(cliente.id) {
        nombre = cliente.nombre
        empresa = cliente.empresa
        tipoCliente = cliente.tipoCliente
        estatus = cliente.estatus

        telefono = cliente.telefono
        correo = cliente.correo
        direccion = cliente.direccion

        rfc = cliente.rfc
        personaContacto = cliente.personaContacto
        cargo = cliente.cargo
        notas = cliente.notas
        fotoUri = cliente.fotoUri

        clienteActivo = cliente.clienteActivo
        recibeCotizaciones = cliente.recibeCotizaciones
        contactoWhatsapp = cliente.contactoWhatsapp
        contactoLlamadas = cliente.contactoLlamadas
        contactoCorreo = cliente.contactoCorreo
    }

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
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }

                Text(
                    text = "Editar Cliente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .padding(
                    start = 8.dp,
                    top = 0.dp,
                    end = 8.dp,
                    bottom = 8.dp
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                AvisoEditandoCliente(
                    nombreCliente = clienteActual.nombre
                )
            }

            item {
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

            item {
                SeccionInformacionContactoCliente(
                    telefono = telefono,
                    onTelefonoChange = { telefono = it },
                    correo = correo,
                    onCorreoChange = { correo = it },
                    direccion = direccion,
                    onDireccionChange = { direccion = it }
                )
            }

            item {
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
            }

            item {/*
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
            }

            item {
                BotonesEditarCliente(
                    onCancelarClick = {
                        navController.popBackStack()
                    },
                    onActualizarClick = {
                        viewModel.actualizarCliente(
                            clienteId = clienteId,
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
}

@Composable
fun AvisoEditandoCliente(
    nombreCliente: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = 0.dp,
                top = 5.dp,
                end = 0.dp,
                bottom = 0.dp
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFF6FF)
        ),
        elevation = CardDefaults.cardElevation(1.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "Editando cliente",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D4ED8)
                )

                Text(
                    text = nombreCliente,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun BotonesEditarCliente(
    onCancelarClick: () -> Unit,
    onActualizarClick: () -> Unit
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
            onClick = onActualizarClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Actualizar Cliente",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}