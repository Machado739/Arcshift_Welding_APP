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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.ui.viewmodel.EmpleadosViewModel
import com.example.arcshiftwelding.ui.theme.arcshiftColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEmpleadoScreen(
    navController: NavController,
    empleadoId: Int,
    onBack: () -> Unit = {},
    onGuardar: () -> Unit = {},
    onCancelar: () -> Unit = {},
    viewModel: EmpleadosViewModel

) {
    val context = LocalContext.current
    val empleadoEntity by viewModel
        .observarEmpleado(empleadoId)
        .collectAsState(initial = null)

    val proyectoActual by remember(empleadoId) {
        ArcshiftWeldingDatabase.getDatabase(context)
            .proyectoEmpleadoDao()
            .observarProyectoActualEmpleado(empleadoId)
    }.collectAsState(initial = null)

    var datosCargados by remember { mutableStateOf(false) }

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
    var fotoUri by remember { mutableStateOf("") }

    var empleadoActivo by remember { mutableStateOf(true) }
    var asignarTrabajos by remember { mutableStateOf(true) }
    var pagoSemanalActivo by remember { mutableStateOf(true) }

    LaunchedEffect(empleadoEntity) {
        val empleado = empleadoEntity

        if (empleado != null && !datosCargados) {
            nombre = empleado.nombre
            puesto = empleado.puesto
            estatus = if (empleado.activo) "Activo" else "Inactivo"

            telefono = empleado.telefono
            correo = empleado.correo
            direccion = empleado.direccion

            fechaIngreso = empleado.fechaIngreso
            tipoContrato = obtenerTipoContratoEmpleado(empleado.porcentajeContrato)
            valorContrato = obtenerValorContratoEmpleado(empleado.porcentajeContrato)
            trabajoActual = empleado.trabajoActual

            notas = empleado.notas
            fotoUri = empleado.fotoUri
            empleadoActivo = empleado.activo

            datosCargados = true
        }
    }

    LaunchedEffect(proyectoActual?.proyectoId) {
        proyectoActual?.let { proyecto ->
            trabajoActual = proyecto.nombreProyecto
        }
    }

    if (empleadoEntity == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("Empleado no encontrado")
        }
        return
    }
    Scaffold(
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
            SeccionInformacionPersonalEditarEmpleado(
                nombre = nombre,
                onNombreChange = { nombre = it },
                puesto = puesto,
                onPuestoChange = { puesto = it },
                estatus = estatus,
                onEstatusChange = { estatus = it },
                fotoUri = fotoUri,
                onFotoUriChange = { fotoUri = it }
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

            BotonesFormularioEditarEmpleado(
                onCancelarClick = {
                    onCancelar()
                    navController.popBackStack()
                },
                onGuardarClick = {
                    val empleadoActualizado = empleadoEntity!!.copy(
                        nombre = nombre,
                        telefono = telefono,
                        correo = correo,
                        direccion = direccion,
                        puesto = puesto,
                        salario = obtenerSalarioEmpleado(tipoContrato, valorContrato),
                        porcentajeContrato = construirContratoEmpleado(tipoContrato, valorContrato),
                        fechaIngreso = fechaIngreso,
                        trabajoActual = trabajoActual,
                        notas = notas,
                        fotoUri = fotoUri,
                        activo = empleadoActivo && estatus == "Activo"
                    )

                    viewModel.actualizarEmpleado(empleadoActualizado)

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
    onEstatusChange: (String) -> Unit,
    fotoUri: String,
    onFotoUriChange: (String) -> Unit
) {
    CardFormularioEmpleado(
        titulo = "Información personal",
        icono = Icons.Default.Person,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AgregarFotoEmpleado(
                fotoUri = fotoUri,
                nombreEmpleado = nombre,
                onFotoUriChange = onFotoUriChange,
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
                text = "Actualizar",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}