package com.example.arcshiftwelding.ui.Screen.clientes

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
    val contactoCorreo: Boolean
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarClienteScreen(
    navController: NavController,
    clienteId: Int
) {
    /*
        Estos datos después los vas a cargar desde Room usando el clienteId.
        Por ahora son datos de prueba para que la pantalla funcione.
    */
    val clienteActual = ClienteEditarUI(
        id = clienteId,
        nombre = "Eduardo Barrios",
        empresa = "Taller Barrios S.A. de C.V.",
        tipoCliente = "Empresa",
        estatus = "Activo",
        telefono = "614 123 4567",
        correo = "eduardo@tallerbarrios.com",
        direccion = "Av. de las Industrias #123, Chihuahua, Chih.",
        rfc = "TBA190101ABC",
        personaContacto = "Ing. Eduardo Barrios",
        cargo = "Gerente de proyectos",
        notas = "Cliente recurrente. Prefiere comunicación por WhatsApp.",
        clienteActivo = true,
        recibeCotizaciones = true,
        contactoWhatsapp = true,
        contactoLlamadas = true,
        contactoCorreo = false
    )

    var nombre by remember { mutableStateOf(clienteActual.nombre) }
    var empresa by remember { mutableStateOf(clienteActual.empresa) }
    var tipoCliente by remember { mutableStateOf(clienteActual.tipoCliente) }
    var estatus by remember { mutableStateOf(clienteActual.estatus) }

    var telefono by remember { mutableStateOf(clienteActual.telefono) }
    var correo by remember { mutableStateOf(clienteActual.correo) }
    var direccion by remember { mutableStateOf(clienteActual.direccion) }

    var rfc by remember { mutableStateOf(clienteActual.rfc) }
    var personaContacto by remember { mutableStateOf(clienteActual.personaContacto) }
    var cargo by remember { mutableStateOf(clienteActual.cargo) }
    var notas by remember { mutableStateOf(clienteActual.notas) }

    var clienteActivo by remember { mutableStateOf(clienteActual.clienteActivo) }
    var recibeCotizaciones by remember { mutableStateOf(clienteActual.recibeCotizaciones) }
    var contactoWhatsapp by remember { mutableStateOf(clienteActual.contactoWhatsapp) }
    var contactoLlamadas by remember { mutableStateOf(clienteActual.contactoLlamadas) }
    var contactoCorreo by remember { mutableStateOf(clienteActual.contactoCorreo) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo Gasto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar")
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
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
                    onEstatusChange = { estatus = it }
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

            item {
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
            }

            item {
                BotonesEditarCliente(
                    onCancelarClick = {
                        navController.popBackStack()
                    },
                    onActualizarClick = {
                        /*
                            Aquí después conectas con ViewModel / Room:

                            viewModel.actualizarCliente(
                                clienteId = clienteId,
                                nombre = nombre,
                                empresa = empresa,
                                telefono = telefono,
                                ...
                            )
                        */

                        navController.popBackStack()
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
fun AvisoEditandoCliente(
    nombreCliente: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFF6FF)
        )
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