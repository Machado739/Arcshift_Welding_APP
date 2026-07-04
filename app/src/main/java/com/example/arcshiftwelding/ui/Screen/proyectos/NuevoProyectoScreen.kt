package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoProyectoScreen(
    navController: NavController,
    viewModel: ProyectosViewModel
) {
    val clientes by viewModel.clientes.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var clienteSeleccionado by remember { mutableStateOf<ClienteEntity?>(null) }
    var fechaInicio by remember { mutableStateOf(fechaActualProyecto()) }
    var fechaFin by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Pendiente") }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Nuevo proyecto",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .padding(14.dp)
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
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Información del proyecto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre del proyecto") },
                        placeholder = { Text("Ej. Portón industrial") },
                        singleLine = true
                    )

                    SelectorClienteProyecto(
                        clientes = clientes,
                        clienteSeleccionado = clienteSeleccionado,
                        onClienteSeleccionado = { clienteSeleccionado = it }
                    )

                    OutlinedTextField(
                        value = fechaInicio,
                        onValueChange = { fechaInicio = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Fecha inicio") },
                        placeholder = { Text("dd/MM/yyyy") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = fechaFin,
                        onValueChange = { fechaFin = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Fecha estimada de fin") },
                        placeholder = { Text("dd/MM/yyyy") },
                        singleLine = true
                    )

                    SelectorEstadoProyecto(
                        estado = estado,
                        onEstadoChange = { estado = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                            fechaInicio = fechaInicio,
                            fechaFin = fechaFin,
                            estado = estado
                        )

                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = nombre.trim().isNotEmpty() && clienteSeleccionado != null,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
fun SelectorClienteProyecto(
    clientes: List<ClienteEntity>,
    clienteSeleccionado: ClienteEntity?,
    onClienteSeleccionado: (ClienteEntity) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Cliente",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF334155)
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
            onDismissRequest = { expandido = false },
            modifier = Modifier.fillMaxWidth()
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
fun SelectorEstadoProyecto(
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
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF334155)
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

fun fechaActualProyecto(): String {
    return SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    ).format(Date())
}