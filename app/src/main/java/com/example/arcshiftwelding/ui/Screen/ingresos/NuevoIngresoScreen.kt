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
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import androidx.compose.foundation.clickable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import java.util.TimeZone
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.collections.emptyList
import kotlin.collections.toMutableList


@Composable
fun NuevoIngresoScreen(
    navController: NavController,
    viewModel: IngresosViewModel
) {
    val form by viewModel.formState.collectAsState()

    var pagosProgramados by remember {
        mutableStateOf(emptyList<PagoProgramadoForm>())
    }

    var mensajeError by remember {
        mutableStateOf("")
    }

    LaunchedEffect(form.formaPago) {
        if (form.formaPago != "Anticipo") {
            pagosProgramados = emptyList<PagoProgramadoForm>()
        }

        mensajeError = ""
    }

    val clientes by viewModel.clientesActivos.collectAsState(initial = emptyList())
    val proyectos by viewModel.proyectos.collectAsState(initial = emptyList())
    val cotizaciones by viewModel.cotizaciones.collectAsState(initial = emptyList())



    LaunchedEffect(Unit) {
        viewModel.limpiarFormulario()
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
                    text = "Nuevo Ingreso",
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
            SeccionIngresoInformacionGeneralNueva(
                form = form,
                proyectos = proyectos,
                cotizaciones = cotizaciones,
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

            if (mensajeError.isNotBlank()) {
                Text(
                    text = mensajeError,
                    color = Color(0xFFDC2626),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            BotonesNuevoIngreso(
                onCancelar = { navController.popBackStack() },
                onGuardar = {
                    mensajeError = ""

                    val esIngresoDeProyecto = form.proyectoId != null
                    val esAnticipo = form.formaPago == "Anticipo"

                    val montoRecibido = form.subtotal.aDouble()

                    val ivaNumero = if (esIngresoDeProyecto || esAnticipo) {
                        0.0
                    } else {
                        montoRecibido * (form.ivaPorcentaje.aDouble() / 100.0)
                    }

                    val totalRecibido = if (esIngresoDeProyecto || esAnticipo) {
                        montoRecibido
                    } else {
                        montoRecibido + ivaNumero
                    }

                    val montoTotalProyecto = form.montoTotalProyecto.aDouble()
                    val saldoPendiente = (montoTotalProyecto - totalRecibido).coerceAtLeast(0.0)

                    val pagosIncompletos = pagosProgramados.any { pago ->
                        pago.fecha.isBlank() || pago.monto.aDouble() <= 0.0
                    }

                    val sumaPagosProgramados = pagosProgramados.sumOf { pago ->
                        pago.monto.aDouble()
                    }

                    if (form.trabajo.isBlank()) {
                        mensajeError = "Ingresa el trabajo o selecciona un proyecto"
                        return@BotonesNuevoIngreso
                    }

                    if (form.concepto.isBlank()) {
                        mensajeError = "Ingresa el concepto o descripción"
                        return@BotonesNuevoIngreso
                    }
/*
                    if (subtotalNumero <= 0.0) {
                        mensajeError = "Ingresa un monto recibido válido"
                        return@BotonesNuevoIngreso
                    }
*/
                    if (form.metodoPago.isBlank()) {
                        mensajeError = "Selecciona el método de pago"
                        return@BotonesNuevoIngreso
                    }

                    if (form.formaPago == "Anticipo") {
                        if (form.proyectoId == null) {
                            mensajeError = "Para registrar un anticipo selecciona un proyecto"
                            return@BotonesNuevoIngreso
                        }

                        if (montoTotalProyecto <= 0.0) {
                            mensajeError = "Ingresa el monto total del proyecto"
                            return@BotonesNuevoIngreso
                        }

                        if (totalRecibido > montoTotalProyecto) {
                            mensajeError = "El monto recibido no puede ser mayor al monto total del proyecto"
                            return@BotonesNuevoIngreso
                        }

                        if (pagosIncompletos) {
                            mensajeError = "Completa la fecha y el monto de los pagos programados"
                            return@BotonesNuevoIngreso
                        }

                        if (sumaPagosProgramados > saldoPendiente) {
                            mensajeError = "Los pagos programados superan el saldo pendiente"
                            return@BotonesNuevoIngreso
                        }
                    }

                    viewModel.guardarIngreso(
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
fun SeccionIngresoInformacionGeneralNueva(
    form: IngresoFormState,
    proyectos: List<ProyectoEntity>,
    cotizaciones: List<CotizacionEntity>,
    onChange: (IngresoFormState) -> Unit
) {
    TarjetaNuevoIngreso(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        CampoTrabajoIngreso(
            trabajo = form.trabajo,
            proyectos = proyectos,
            onTrabajoChange = { nuevoTrabajo ->
                onChange(
                    form.copy(
                        trabajo = nuevoTrabajo,
                        proyectoId = null,
                        proyecto = "",
                        clienteId = null,
                        cotizacionId = null,
                        montoTotalProyecto = "",
                        anticipo = "",
                        ivaPorcentaje = "16",
                        iva = "0"
                    )
                )
            },
            onProyectoSeleccionado = { proyecto ->
                val cotizacionProyecto = cotizaciones.firstOrNull {
                    it.id == proyecto.cotizacionId
                }

                val montoTotalProyecto = when {
                    cotizacionProyecto != null -> cotizacionProyecto.total
                    proyecto.presupuestoEstimado > 0.0 -> proyecto.presupuestoEstimado
                    proyecto.costoTotal > 0.0 -> proyecto.costoTotal
                    else -> 0.0
                }

                val pagoInicialRequerido = cotizacionProyecto?.anticipo ?: 0.0

                val requiereAnticipo = pagoInicialRequerido > 0.0 &&
                        pagoInicialRequerido < montoTotalProyecto

                onChange(
                    form.copy(
                        proyectoId = proyecto.id,
                        trabajo = proyecto.nombre,
                        proyecto = proyecto.nombre,
                        clienteId = proyecto.clienteId,
                        cotizacionId = proyecto.cotizacionId,

                        formaPago = if (requiereAnticipo) "Anticipo" else "Pago",

                        montoTotalProyecto = numeroIngresoTexto(montoTotalProyecto),

                        subtotal = if (requiereAnticipo) {
                            numeroIngresoTexto(pagoInicialRequerido)
                        } else {
                            ""
                        },

                        ivaPorcentaje = "0",
                        iva = "0",

                        anticipo = if (requiereAnticipo) {
                            numeroIngresoTexto(pagoInicialRequerido)
                        } else {
                            ""
                        }
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Concepto / Descripción *",
            valor = form.concepto,
            placeholder = "Ej. Pago de avance, anticipo, liquidación del trabajo",
            onValueChange = {
                onChange(form.copy(concepto = it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoFechaIngreso(
            titulo = "Fecha *",
            valor = form.fecha,
            onFechaSeleccionada = {
                onChange(form.copy(fecha = it))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
fun numeroIngresoTexto(valor: Double): String {
    return if (valor % 1.0 == 0.0) {
        valor.toInt().toString()
    } else {
        "%.2f".format(Locale.US, valor)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTrabajoIngreso(
    trabajo: String,
    proyectos: List<ProyectoEntity>,
    onTrabajoChange: (String) -> Unit,
    onProyectoSeleccionado: (ProyectoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Text(
            text = "Trabajo / Proyecto *",
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = {
                expandido = !expandido
            }
        ) {
            OutlinedTextField(
                value = trabajo,
                onValueChange = onTrabajoChange,
                placeholder = {
                    Text(
                        text = "Escribe el trabajo o selecciona un proyecto",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandido
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = {
                    expandido = false
                }
            ) {
                if (proyectos.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "No hay proyectos registrados",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        },
                        onClick = { },
                        enabled = false
                    )
                } else {
                    proyectos.forEach { proyecto ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = proyecto.nombre,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Text(
                                        text = proyecto.estado,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            },
                            onClick = {
                                onProyectoSeleccionado(proyecto)
                                expandido = false
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SeccionIngresoInformacionFinanciera(
    form: IngresoFormState,
    onChange: (IngresoFormState) -> Unit
) {
    val opcionesIva = listOf("0", "8", "16")

    val tiposIngreso = listOf(
        "Pago",
        "Anticipo"
    )

    val metodosPago = listOf(
        "Efectivo",
        "Tarjeta",
        "Transferencia",
        "Cheque",
        "Crédito"
    )

    val esIngresoDeProyecto = form.proyectoId != null
    val esAnticipo = form.formaPago == "Anticipo"

    val montoRecibido = form.subtotal.aDouble()

    val ivaNumero = if (esIngresoDeProyecto || esAnticipo) {
        0.0
    } else {
        montoRecibido * (form.ivaPorcentaje.aDouble() / 100.0)
    }

    val totalRecibido = if (esIngresoDeProyecto || esAnticipo) {
        montoRecibido
    } else {
        montoRecibido + ivaNumero
    }

    val montoTotalProyecto = if (esIngresoDeProyecto || esAnticipo) {
        form.montoTotalProyecto.aDouble()
    } else {
        totalRecibido
    }

    val saldoPendiente = if (esAnticipo || esIngresoDeProyecto) {
        (montoTotalProyecto - totalRecibido).coerceAtLeast(0.0)
    } else {
        0.0
    }

    val pagoInicialRequerido = form.anticipo.aDouble()

    TarjetaNuevoIngreso(
        titulo = "Información financiera",
        icono = Icons.Default.AttachMoney
    ) {
        CampoDropdownIngreso(
            titulo = "Tipo de ingreso *",
            valor = form.formaPago,
            opciones = tiposIngreso,
            placeholder = "Tipo",
            onValueChange = { tipo ->
                onChange(
                    form.copy(
                        formaPago = tipo,
                        iva = "0",
                        ivaPorcentaje = if (form.proyectoId != null || tipo == "Anticipo") {
                            "0"
                        } else {
                            "16"
                        },
                        montoTotalProyecto = if (tipo == "Pago" && form.proyectoId == null) {
                            ""
                        } else {
                            form.montoTotalProyecto
                        }
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (esAnticipo || esIngresoDeProyecto) {
            CampoTextoIngreso(
                titulo = "Monto total del proyecto",
                valor = form.montoTotalProyecto,
                placeholder = "$ 0.00",
                onValueChange = {
                    onChange(form.copy(montoTotalProyecto = it))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (pagoInicialRequerido > 0.0 && esAnticipo) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF7E6)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "Pago inicial requerido",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = pagoInicialRequerido.formatoDinero(),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Puedes registrar una cantidad mayor; se descontará del saldo pendiente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            CampoTextoIngreso(
                titulo = if (esAnticipo) {
                    "Monto recibido como anticipo *"
                } else {
                    "Monto recibido *"
                },
                valor = form.subtotal,
                placeholder = "$ 0.00",
                onValueChange = { nuevoMonto ->
                    onChange(
                        form.copy(
                            subtotal = nuevoMonto,
                            iva = "0",
                            ivaPorcentaje = "0"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoTextoIngreso(
                    titulo = "Subtotal *",
                    valor = form.subtotal,
                    placeholder = "$ 0.00",
                    onValueChange = { nuevoSubtotal ->
                        val nuevoIva = nuevoSubtotal.aDouble() *
                                (form.ivaPorcentaje.aDouble() / 100.0)

                        onChange(
                            form.copy(
                                subtotal = nuevoSubtotal,
                                iva = nuevoIva.toString()
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                CampoDropdownIngreso(
                    titulo = "IVA (%)",
                    valor = form.ivaPorcentaje,
                    opciones = opcionesIva,
                    placeholder = "IVA",
                    onValueChange = { nuevoIvaPorcentaje ->
                        val nuevoIva = form.subtotal.aDouble() *
                                (nuevoIvaPorcentaje.aDouble() / 100.0)

                        onChange(
                            form.copy(
                                ivaPorcentaje = nuevoIvaPorcentaje,
                                iva = nuevoIva.toString()
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (esAnticipo) {
                    Color(0xFFFFF7E6)
                } else {
                    Color(0xFFE3F3E6)
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = if (esAnticipo) {
                        "Anticipo recibido"
                    } else {
                        "Pago recibido"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (esAnticipo) {
                        Color(0xFF92400E)
                    } else {
                        Color(0xFF2E7D32)
                    },
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = totalRecibido.formatoDinero(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (esAnticipo) {
                        Color(0xFF92400E)
                    } else {
                        Color(0xFF2E7D32)
                    }
                )

                if (esIngresoDeProyecto || esAnticipo) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Monto total del proyecto: ${montoTotalProyecto.formatoDinero()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "Saldo restante: ${saldoPendiente.formatoDinero()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "IVA calculado: ${ivaNumero.formatoDinero()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoDropdownIngreso(
            titulo = "Método de pago *",
            valor = form.metodoPago,
            opciones = metodosPago,
            placeholder = "Método",
            onValueChange = {
                onChange(form.copy(metodoPago = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Referencia / Folio",
            valor = form.folio,
            placeholder = "Ej. FACT-001, transferencia, recibo",
            onValueChange = {
                onChange(form.copy(folio = it))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SeccionPagosProgramadosIngreso(
    pagos: List<PagoProgramadoForm>,
    montoTotalProyecto: Double,
    montoRecibido: Double,
    onPagosChange: (List<PagoProgramadoForm>) -> Unit
) {
    val sumaProgramada = pagos
        .filter { it.estado != "Pagado" }
        .sumOf {
            it.monto.aDouble()
        }

    val saldoPendiente = (montoTotalProyecto - montoRecibido).coerceAtLeast(0.0)
    val saldoSinProgramar = (saldoPendiente - sumaProgramada).coerceAtLeast(0.0)

    TarjetaNuevoIngreso(
        titulo = "Pagos programados",
        icono = Icons.Default.DateRange
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEFF6FF)
            )
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilaResumenPagoProgramado(
                    titulo = "Monto total del proyecto",
                    valor = montoTotalProyecto.formatoDinero()
                )

                FilaResumenPagoProgramado(
                    titulo = "Anticipo recibido",
                    valor = montoRecibido.formatoDinero()
                )

                FilaResumenPagoProgramado(
                    titulo = "Saldo pendiente",
                    valor = saldoPendiente.formatoDinero()
                )

                FilaResumenPagoProgramado(
                    titulo = "Total programado",
                    valor = sumaProgramada.formatoDinero()
                )

                FilaResumenPagoProgramado(
                    titulo = "Falta por programar",
                    valor = saldoSinProgramar.formatoDinero()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (pagos.isEmpty()) {
            Text(
                text = "No hay pagos programados.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        pagos.forEachIndexed { index, pago ->
            val pagado = pago.estado == "Pagado"

            if (pagado) {
                ItemPagoProgramadoPagadoNoEditable(
                    pago = pago
                )

                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pago ${index + 1}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )

                        CampoFechaIngreso(
                            titulo = "Fecha programada",
                            valor = pago.fecha,
                            onFechaSeleccionada = { nuevaFecha ->
                                onPagosChange(
                                    pagos.toMutableList().also {
                                        it[index] = pago.copy(fecha = nuevaFecha)
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        CampoTextoIngreso(
                            titulo = "Monto",
                            valor = pago.monto,
                            placeholder = "$ 0.00",
                            onValueChange = { nuevoMonto ->
                                onPagosChange(
                                    pagos.toMutableList().also {
                                        it[index] = pago.copy(monto = nuevoMonto)
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        val acumuladoHastaEstePago = pagos
                            .filter { it.estado != "Pagado" }
                            .takeWhile { it != pago }
                            .sumOf { it.monto.aDouble() } + pago.monto.aDouble()

                        val saldoDespuesDeEstePago = (
                                montoTotalProyecto - montoRecibido - acumuladoHastaEstePago
                                ).coerceAtLeast(0.0)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                FilaResumenPagoProgramado(
                                    titulo = "Monto de este pago",
                                    valor = pago.monto.aDouble().formatoDinero()
                                )

                                FilaResumenPagoProgramado(
                                    titulo = "Saldo después de este pago",
                                    valor = saldoDespuesDeEstePago.formatoDinero()
                                )
                            }
                        }

                        CampoTextoIngreso(
                            titulo = "Observaciones",
                            valor = pago.observaciones,
                            placeholder = "Opcional",
                            onValueChange = { nuevasObservaciones ->
                                onPagosChange(
                                    pagos.toMutableList().also {
                                        it[index] = pago.copy(observaciones = nuevasObservaciones)
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedButton(
                            onClick = {
                                onPagosChange(
                                    pagos.toMutableList().also {
                                        it.removeAt(index)
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text("Eliminar pago")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        TextButton(
            onClick = {
                onPagosChange(
                    pagos + PagoProgramadoForm()
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = null,
                tint = Color(0xFF16A34A)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Agregar pago programado",
                color = Color(0xFF16A34A)
            )
        }
    }
}
@Composable
fun ItemPagoProgramadoPagadoNoEditable(
    pago: PagoProgramadoForm
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0FDF4)
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Pago realizado",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF166534)
                    )

                    Text(
                        text = "Programado: ${pago.fecha}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray
                    )
                }

                Text(
                    text = pago.monto.aDouble().formatoDinero(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF16A34A)
                )
            }

            if (pago.fechaPago.isNotBlank()) {
                Text(
                    text = "Pagado el ${pago.fechaPago} · ${pago.metodoPago.ifBlank { "Sin método" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }

            Text(
                text = "Este pago ya fue registrado y no se puede editar desde esta pantalla.",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
@Composable
fun FilaResumenPagoProgramado(
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB)
        )
    }
}

@Composable
fun SeccionIngresoComprobanteNuevo(
    form: IngresoFormState,
    onChange: (IngresoFormState) -> Unit
) {
    val seleccionarArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val tipo = when {
                uri.toString().contains(".pdf", ignoreCase = true) -> "PDF"
                else -> "Imagen"
            }

            onChange(
                form.copy(
                    comprobanteUri = uri.toString(),
                    tipoComprobante = tipo
                )
            )
        }
    }

    TarjetaNuevoIngreso(
        titulo = "Comprobante",
        icono = Icons.Default.AttachFile
    ) {
        if (form.comprobanteUri.isBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        seleccionarArchivo.launch("application/pdf")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("PDF")
                }

                OutlinedButton(
                    onClick = {
                        seleccionarArchivo.launch("image/*")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("Foto")
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (form.tipoComprobante == "PDF") {
                            Icons.Default.PictureAsPdf
                        } else {
                            Icons.Default.Image
                        },
                        contentDescription = null,
                        tint = if (form.tipoComprobante == "PDF") {
                            Color(0xFFDC2626)
                        } else {
                            Color(0xFF2563EB)
                        }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = form.tipoComprobante.ifBlank { "Comprobante" },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "Archivo seleccionado",
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    }

                    TextButton(
                        onClick = {
                            onChange(
                                form.copy(
                                    comprobanteUri = "",
                                    tipoComprobante = ""
                                )
                            )
                        }
                    ) {
                        Text("Quitar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Observaciones",
            valor = form.observaciones,
            placeholder = "Agrega notas u observaciones opcionales",
            onValueChange = {
                onChange(form.copy(observaciones = it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            singleLine = false
        )
    }
}
@Composable
fun SeccionIngresoRelacionadoNuevo(
    form: IngresoFormState,
    clientes: List<ClienteEntity>,
    onChange: (IngresoFormState) -> Unit
) {
    TarjetaNuevoIngreso(
        titulo = "Relaciones",
        icono = Icons.Default.Link
    ) {
        if (form.proyecto.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFEFF6FF)
                )
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Proyecto seleccionado",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Text(
                        text = form.proyecto,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        SelectorClienteIngreso(
            clientes = clientes,
            clienteSeleccionadoId = form.clienteId,
            onClienteSeleccionado = { clienteId ->
                onChange(
                    form.copy(
                        clienteId = clienteId,
                        cotizacionId = null
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun SeccionIngresoInformacionGeneral(
    form: IngresoFormState,
    clientes: List<ClienteEntity>,
    onChange: (IngresoFormState) -> Unit
) {
    TarjetaNuevoIngreso(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        CampoTextoIngreso(
            titulo = "Concepto / Descripción *",
            valor = form.concepto,
            placeholder = "Ej. Pago por fabricación de estructura metálica",
            onValueChange = {
                onChange(form.copy(concepto = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        SelectorClienteIngreso(
            clientes = clientes,
            clienteSeleccionadoId = form.clienteId,
            onClienteSeleccionado = { clienteId ->
                onChange(
                    form.copy(
                        clienteId = clienteId,
                        cotizacionId = null
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoFechaIngreso(
                titulo = "Fecha *",
                valor = form.fecha,
                onFechaSeleccionada = {
                    onChange(form.copy(fecha = it))
                },
                modifier = Modifier.weight(1f)
            )
            CampoTextoIngreso(
                titulo = "Trabajo",
                valor = form.trabajo,
                placeholder = "Ej. Tejaban 6x4m",
                onValueChange = {
                    onChange(form.copy(trabajo = it))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BotonesNuevoIngreso(
    onCancelar: () -> Unit,
    onGuardar: () -> Unit
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
            onClick = onGuardar,
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
                text = "Guardar Ingreso",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TarjetaNuevoIngreso(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            contenido()
        }
    }
}

@Composable
fun CampoTextoIngreso(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    singleLine: Boolean = true,
    readOnly: Boolean = false
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = valor,
            readOnly = readOnly,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
            singleLine = singleLine,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDropdownIngreso(
    titulo: String,
    valor: String,
    opciones: List<String>,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = {
                expandido = !expandido
            }
        ) {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandido
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = {
                    expandido = false
                }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = opcion,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        onClick = {
                            onValueChange(opcion)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaIngreso(
    titulo: String,
    valor: String,
    onFechaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = modifier
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    mostrarCalendario = true
                }
        ) {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Seleccionar fecha",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Seleccionar fecha",
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color(0xFFE0E0E0),
                    disabledContainerColor = Color.White,
                    disabledPlaceholderColor = Color.Gray,
                    disabledTrailingIconColor = Color.DarkGray
                )
            )
        }
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = {
                mostrarCalendario = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val fechaSeleccionada = datePickerState.selectedDateMillis

                        if (fechaSeleccionada != null) {
                            val calendario = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = fechaSeleccionada
                            }

                            val dia = calendario.get(Calendar.DAY_OF_MONTH)
                            val mes = calendario.get(Calendar.MONTH) + 1
                            val anio = calendario.get(Calendar.YEAR)

                            val fechaFormateada = "%02d/%02d/%04d".format(dia, mes, anio)

                            onFechaSeleccionada(fechaFormateada)
                        }

                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarCalendario = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@Composable
fun CampoSeleccionIngreso(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.DarkGray
            )
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun BotonArchivoIngreso(
    titulo: String,
    subtitulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(6.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFFFAFAFA),
            contentColor = Color.Black
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorClienteIngreso(
    clientes: List<ClienteEntity>,
    clienteSeleccionadoId: Int?,
    onClienteSeleccionado: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val clienteSeleccionado = clientes.firstOrNull {
        it.id == clienteSeleccionadoId
    }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = clienteSeleccionado?.nombre ?: "Sin cliente",
            onValueChange = {},
            readOnly = true,
            label = { Text("Cliente *") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin cliente") },
                onClick = {
                    onClienteSeleccionado(null)
                    expandido = false
                }
            )

            clientes.forEach { cliente ->
                DropdownMenuItem(
                    text = { Text(cliente.nombre) },
                    onClick = {
                        onClienteSeleccionado(cliente.id)
                        expandido = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorProyectoIngreso(
    proyectos: List<ProyectoEntity>,
    proyectoSeleccionadoNombre: String,
    onProyectoSeleccionado: (ProyectoEntity?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val proyectoSeleccionado = proyectos.firstOrNull {
        it.nombre == proyectoSeleccionadoNombre
    }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = proyectoSeleccionado?.nombre ?: if (proyectoSeleccionadoNombre.isBlank()) "Sin proyecto" else proyectoSeleccionadoNombre,
            onValueChange = {},
            readOnly = true,
            label = { Text("Proyecto") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin proyecto") },
                onClick = {
                    onProyectoSeleccionado(null)
                    expandido = false
                }
            )

            if (proyectos.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "No hay proyectos registrados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    },
                    onClick = { },
                    enabled = false
                )
            } else {
                proyectos.forEach { proyecto ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = proyecto.nombre,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = proyecto.estado,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    maxLines = 1
                                )
                            }
                        },
                        onClick = {
                            onProyectoSeleccionado(proyecto)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorCotizacionIngreso(
    cotizaciones: List<CotizacionEntity>,
    cotizacionSeleccionadaId: Int?,
    onCotizacionSeleccionada: (CotizacionEntity?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val cotizacionSeleccionada = cotizaciones.firstOrNull {
        it.id == cotizacionSeleccionadaId
    }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = cotizacionSeleccionada?.folio ?: "Sin cotización",
            onValueChange = {},
            readOnly = true,
            label = { Text("Cotización") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sin cotización") },
                onClick = {
                    onCotizacionSeleccionada(null)
                    expandido = false
                }
            )

            cotizaciones.forEach { cotizacion ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = cotizacion.folio,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = cotizacion.descripcionTrabajo,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    },
                    onClick = {
                        onCotizacionSeleccionada(cotizacion)
                        expandido = false
                    }
                )
            }
        }
    }
}