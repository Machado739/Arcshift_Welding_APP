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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignarEmpleadoProyectoScreen(
    proyectoId: Int,
    navController: NavController,
    proyectoViewModel: ProyectosViewModel
) {
    var empleadoIdTexto by remember { mutableStateOf("") }
    var tipoPago by remember { mutableStateOf("Día") }
    var pagoAcordado by remember { mutableStateOf("") }
    var diasTrabajados by remember { mutableStateOf("") }
    var horasTrabajadas by remember { mutableStateOf("") }
    var porcentaje by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    val tiposPago = listOf("Día", "Hora", "Semana", "Trabajo", "Porcentaje")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar empleado") },
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
                value = empleadoIdTexto,
                onValueChange = { empleadoIdTexto = it },
                label = { Text("ID del empleado") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownBasicoProyecto(
                titulo = "Tipo de pago",
                valor = tipoPago,
                opciones = tiposPago,
                onSeleccion = { tipoPago = it }
            )

            OutlinedTextField(
                value = pagoAcordado,
                onValueChange = { pagoAcordado = it },
                label = { Text("Pago acordado") },
                modifier = Modifier.fillMaxWidth()
            )

            if (tipoPago == "Día") {
                OutlinedTextField(
                    value = diasTrabajados,
                    onValueChange = { diasTrabajados = it },
                    label = { Text("Días trabajados") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (tipoPago == "Hora") {
                OutlinedTextField(
                    value = horasTrabajadas,
                    onValueChange = { horasTrabajadas = it },
                    label = { Text("Horas trabajadas") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (tipoPago == "Porcentaje") {
                OutlinedTextField(
                    value = porcentaje,
                    onValueChange = { porcentaje = it },
                    label = { Text("Porcentaje") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    // Esta parte se puede mejorar con selector visual de empleados.
                    // Para hacerlo bien, hay que obtener el empleado desde el Dao/ViewModel.
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar empleado")
            }
        }
    }
}