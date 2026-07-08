package com.example.arcshiftwelding.ui.Screen.ingresos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarIngresoScreen(
    navController: NavController,
    ingresoId: Int,
    viewModel: IngresosViewModel
) {
    val form by viewModel.formState.collectAsState()
    var pagosProgramados by remember {
        mutableStateOf(emptyList<PagoProgramadoForm>())
    }

    var pagosCargados by remember {
        mutableStateOf(false)
    }

    val pagosProgramadosDb by viewModel
        .obtenerPagosProgramadosPorIngreso(form.id)
        .collectAsState(initial = emptyList())

    LaunchedEffect(pagosProgramadosDb, form.id) {
        if (!pagosCargados && form.id != 0) {
            pagosProgramados = pagosProgramadosDb
            pagosCargados = true
        }
    }

    val clientes by viewModel.clientesActivos.collectAsState(initial = emptyList())
    val cotizaciones by viewModel.cotizaciones.collectAsState(initial = emptyList())
    val proyectos by viewModel.proyectos.collectAsState(initial = emptyList())

    val cotizacionesFiltradas = if (form.clienteId != null) {
        cotizaciones.filter { it.clienteId == form.clienteId }
    } else {
        cotizaciones
    }

    LaunchedEffect(ingresoId) {
        viewModel.cargarIngresoParaEditar(ingresoId)
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
                    text = "Editar Ingreso",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SeccionIngresoInformacionGeneral(
                form = form,
                clientes = clientes,
                onChange = viewModel::actualizarFormulario
            )

            SeccionIngresoInformacionFinanciera(
                form = form,
                onChange = viewModel::actualizarFormulario
            )

            if (form.formaPago == "Anticipo") {
                val montoRecibido = form.subtotal.aDouble()
                val montoTotalProyecto = form.montoTotalProyecto.aDouble()

                SeccionPagosProgramadosIngreso(
                    pagos = pagosProgramados,
                    montoTotalProyecto = montoTotalProyecto,
                    montoRecibido = montoRecibido,
                    onPagosChange = {
                        pagosProgramados = it
                    }
                )
            }

            SeccionIngresoComprobanteNuevo(
                form = form,
                onChange = viewModel::actualizarFormulario
            )

            SeccionIngresoRelacionadoNuevo(
                form = form,
                clientes = clientes,
                onChange = viewModel::actualizarFormulario
            )

            BotonesEditarIngreso(
                onCancelar = {
                    navController.popBackStack()
                },
                onActualizar = {
                    viewModel.actualizarIngreso(
                        pagosProgramados = pagosProgramados
                    ) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

@Composable
fun SeccionEditarIngresoInformacionGeneral() {
    var concepto by remember { mutableStateOf("Pago por fabricación de estructura metálica") }
    var fecha by remember { mutableStateOf("19/05/2026") }
    var cliente by remember { mutableStateOf("Constructora del Bajío") }
    var proyecto by remember { mutableStateOf("Nave Industrial") }

    TarjetaNuevoIngreso(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        CampoTextoIngreso(
            titulo = "Concepto / Descripción *",
            valor = concepto,
            placeholder = "Ej. Pago por fabricación de estructura metálica",
            onValueChange = { concepto = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoSeleccionIngreso(
                titulo = "Cliente *",
                valor = cliente,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFF1F1F1),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo cliente"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTextoIngreso(
                titulo = "Fecha *",
                valor = fecha,
                placeholder = "Fecha",
                onValueChange = { fecha = it },
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )

            CampoSeleccionIngreso(
                titulo = "Proyecto opcional",
                valor = proyecto,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SeccionEditarIngresoInformacionFinanciera() {
    var subtotal by remember { mutableStateOf("$ 12,982.76") }
    var ivaPorcentaje by remember { mutableStateOf("16") }
    var iva by remember { mutableStateOf("$ 2,077.24") }
    var metodoPago by remember { mutableStateOf("Transferencia") }
    var formaPago by remember { mutableStateOf("Contado") }
    var folio by remember { mutableStateOf("FACT-0258") }

    TarjetaNuevoIngreso(
        titulo = "Información financiera",
        icono = Icons.Default.AttachMoney
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTextoIngreso(
                titulo = "Subtotal *",
                valor = subtotal,
                placeholder = "$ 0.00",
                onValueChange = { subtotal = it },
                modifier = Modifier.weight(1f)
            )

            CampoSeleccionIngreso(
                titulo = "IVA (%)",
                valor = ivaPorcentaje,
                modifier = Modifier.weight(1f)
            )

            CampoTextoIngreso(
                titulo = "IVA",
                valor = iva,
                placeholder = "$ 0.00",
                onValueChange = { iva = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F3E6)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Total *",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$ 15,080.00",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoSeleccionIngreso(
                titulo = "Método de pago *",
                valor = metodoPago,
                modifier = Modifier.weight(1f)
            )
/*
            CampoSeleccionIngreso(
                titulo = "Forma de pago",
                valor = formaPago,
                modifier = Modifier.weight(1f)
            )
        }
*/
        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Referencia / Folio opcional",
            valor = folio,
            placeholder = "Ej. FACT-001",
            onValueChange = { folio = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
/*
@Composable
fun SeccionEditarIngresoComprobante() {
    var observaciones by remember {
        mutableStateOf("Pago correspondiente al 50% del proyecto de fabricación de estructura metálica.")
    }

    TarjetaNuevoIngreso(
        titulo = "Comprobante / Evidencia",
        icono = Icons.Default.AttachFile
    ) {
        ArchivoActualIngresoCard(
            nombre = "FACTURA_FACT-0258.pdf",
            peso = "156 KB"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BotonArchivoIngreso(
                titulo = "Generar factura",
                subtitulo = "Crear factura PDF",
                icono = Icons.Default.ReceiptLong,
                modifier = Modifier.weight(1f)
            )

            BotonArchivoIngreso(
                titulo = "Cambiar PDF",
                subtitulo = "Factura o recibo",
                icono = Icons.Default.Description,
                modifier = Modifier.weight(1f)
            )

            BotonArchivoIngreso(
                titulo = "Adjuntar archivo",
                subtitulo = "Máx. 10 MB",
                icono = Icons.Default.AttachFile,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Observaciones opcional",
            valor = observaciones,
            placeholder = "Agrega notas u observaciones opcionales",
            onValueChange = { observaciones = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            singleLine = false
        )
    }*/
}

@Composable
fun SeccionEditarIngresoRelacionado() {
    TarjetaNuevoIngreso(
        titulo = "Relacionado con opcional",
        icono = Icons.Default.Link
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoSeleccionIngreso(
                titulo = "Cotización",
                valor = "COT-0148",
                modifier = Modifier.weight(1f)
            )

         /*   CampoSeleccionIngreso(
                titulo = "Orden de trabajo",
                valor = "OT-0095",
                modifier = Modifier.weight(1f)
            )*/

            CampoSeleccionIngreso(
                titulo = "Cliente",
                valor = "Constructora del Bajío",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ArchivoActualIngresoCard(
    nombre: String,
    peso: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Text(
                    text = "PDF · $peso",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = "Ver archivo",
                    tint = Color.DarkGray
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar archivo",
                    tint = Color(0xFFB42318)
                )
            }
        }
    }
}

@Composable
fun BotonesEditarIngreso(
    onCancelar: () -> Unit,
    onActualizar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.DarkGray
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Cancelar",
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onActualizar,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B8F3A)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Actualizar Ingreso",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}