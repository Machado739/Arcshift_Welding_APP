package com.example.arcshiftwelding.ui.Screen.empleados

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEmpleadoScreen(
    navController: NavController,
    empleadoId: Int,
    onBack: () -> Unit = {},
    onGuardar: () -> Unit = {},
    onCancelar: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("Jaime Lozano") }
    var puesto by remember { mutableStateOf("Ayudante General") }
    var estatus by remember { mutableStateOf("Activo") }

    var telefono by remember { mutableStateOf("614 123 4567") }
    var correo by remember { mutableStateOf("jaime.lozano@gmail.com") }
    var direccion by remember { mutableStateOf("Chihuahua, Chihuahua") }

    var fechaIngreso by remember { mutableStateOf("10/05/2026") }
    var porcentajeContrato by remember { mutableStateOf("20%") }
    var trabajoActual by remember { mutableStateOf("Tejaban 6x4m") }
    var pagoSemanal by remember { mutableStateOf("$980") }

    var notas by remember { mutableStateOf("Empleado asignado principalmente como ayudante general en trabajos de estructura y montaje.") }

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
                    text = "Editar Empleado",
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
            SeccionInformacionPersonalEditarEmpleado(
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
                porcentajeContrato = porcentajeContrato,
                onPorcentajeContratoChange = { porcentajeContrato = it },
                trabajoActual = trabajoActual,
                onTrabajoActualChange = { trabajoActual = it },
                pagoSemanal = pagoSemanal,
                onPagoSemanalChange = { pagoSemanal = it }
            )

            SeccionNotasNuevoEmpleado(
                notas = notas,
                onNotasChange = { notas = it }
            )

            SeccionConfiguracionNuevoEmpleado(
                empleadoActivo = empleadoActivo,
                onEmpleadoActivoChange = { empleadoActivo = it },
                asignarTrabajos = asignarTrabajos,
                onAsignarTrabajosChange = { asignarTrabajos = it },
                pagoSemanalActivo = pagoSemanalActivo,
                onPagoSemanalActivoChange = { pagoSemanalActivo = it }
            )

            BotonesFormularioEditarEmpleado(
                onCancelarClick = {
                    onCancelar()
                    navController.popBackStack()
                },
                onGuardarClick = {
                    onGuardar()
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun SeccionInformacionPersonalEditarEmpleado(
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
fun BotonesFormularioEditarEmpleado(
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
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
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
            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
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
                text = "Actualizar",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}