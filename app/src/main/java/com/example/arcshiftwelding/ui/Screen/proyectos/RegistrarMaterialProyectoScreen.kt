package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.ui.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMaterialProyectoScreen(
    proyectoId: Int,
    navController: NavController,
    proyectosViewModel: ProyectosViewModel
) {
    var productoIdTexto by remember { mutableStateOf("") }
    var cantidadTexto by remember { mutableStateOf("") }
    var fechaUso by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    val mensaje by proyectosViewModel.mensaje.collectAsState()

    LaunchedEffect(mensaje) {
        if (mensaje == "Material registrado correctamente") {
            navController.popBackStack()
            proyectosViewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar material") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = productoIdTexto,
                onValueChange = { productoIdTexto = it },
                label = { Text("ID del producto") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = cantidadTexto,
                onValueChange = { cantidadTexto = it },
                label = { Text("Cantidad usada") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fechaUso,
                onValueChange = { fechaUso = it },
                label = { Text("Fecha de uso") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (mensaje != null) {
                Text(
                    text = mensaje ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    val productoId = productoIdTexto.toIntOrNull()
                    val cantidad = cantidadTexto.toIntOrNull()

                    if (productoId != null && cantidad != null && cantidad > 0) {
                        proyectosViewModel.registrarMaterialUsado(
                            proyectoId = proyectoId,
                            productoId = productoId,
                            cantidadUsada = cantidad,
                            fechaUso = fechaUso,
                            observaciones = observaciones
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar material")
            }
        }
    }
}