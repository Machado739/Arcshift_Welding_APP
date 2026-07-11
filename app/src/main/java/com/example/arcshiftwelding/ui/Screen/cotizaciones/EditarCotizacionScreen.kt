package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import android.content.Context
import android.widget.Toast
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.arcshiftwelding.utils.ComprobanteArchivoSeleccionado
import com.example.arcshiftwelding.utils.MAX_ARCHIVO_ADJUNTO_BYTES
import com.example.arcshiftwelding.utils.MAX_ARCHIVOS_ADJUNTOS_POR_REGISTRO
import com.example.arcshiftwelding.utils.abrirComprobante
import com.example.arcshiftwelding.utils.deserializarComprobantes
import com.example.arcshiftwelding.utils.formatearTamanoComprobante
import com.example.arcshiftwelding.utils.obtenerTipoRealComprobante
import com.example.arcshiftwelding.utils.prepararComprobanteDesdeDocumento
import com.example.arcshiftwelding.utils.serializarComprobantes


@Composable
fun EditarCotizacionScreen(
    navController: NavController,
    cotizacionId: Int,
    viewModel: CotizacionesViewModel
) {

    val cotizacionCompleta by viewModel
        .obtenerCotizacionCompleta(cotizacionId)
        .collectAsState(initial = null)

    val cotizacionEntity = cotizacionCompleta?.cotizacion

    var datosCargados by remember { mutableStateOf(false) }

    val clientes by viewModel.clientesActivos.collectAsState(initial = emptyList())

    var clienteSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var errorCliente by remember { mutableStateOf(false) }

    var proyecto by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var vigencia by remember { mutableStateOf("") }
    var folio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var descuento by remember { mutableStateOf("0") }
    var iva by remember { mutableStateOf("16") }
    var anticipo by remember { mutableStateOf("50") }
    var observaciones by remember { mutableStateOf("") }

    var conceptos by remember {
        mutableStateOf<List<ConceptoEditarCotizacionForm>>(emptyList())
    }

    var archivosAdjuntos by remember {
        mutableStateOf<List<ArchivoEditarCotizacionForm>>(emptyList())
    }

    val subtotalCalculado = conceptos.sumOf { it.total }
    val descuentoCalculado = subtotalCalculado * ((descuento.toDoubleOrNull() ?: 0.0) / 100.0)
    val subtotalConDescuento = subtotalCalculado - descuentoCalculado
    val ivaCalculado = subtotalConDescuento * ((iva.toDoubleOrNull() ?: 0.0) / 100.0)
    val totalCalculado = subtotalConDescuento + ivaCalculado
    val anticipoCalculado = totalCalculado * ((anticipo.toDoubleOrNull() ?: 0.0) / 100.0)
    val saldoCalculado = totalCalculado - anticipoCalculado

    LaunchedEffect(cotizacionCompleta) {
        val cotizacionActual = cotizacionCompleta?.cotizacion
        val detallesActuales = cotizacionCompleta?.detalles ?: emptyList()

        if (cotizacionActual != null && !datosCargados) {
            clienteSeleccionadoId = cotizacionActual.clienteId
            proyecto = cotizacionActual.proyecto
            fecha = cotizacionActual.fecha
            vigencia = cotizacionActual.vigencia
            folio = cotizacionActual.folio
            descripcion = cotizacionActual.descripcionTrabajo
            observaciones = cotizacionEntity!!.observaciones


            descuento = cotizacionActual.descuentoPorcentaje.formatoNumeroCotizacion()
            iva = cotizacionActual.ivaPorcentaje.formatoNumeroCotizacion()
            anticipo = cotizacionActual.anticipoPorcentaje.formatoNumeroCotizacion()

            conceptos = detallesActuales.map { detalle ->
                ConceptoEditarCotizacionForm(
                    tipo = detalle.tipo,
                    descripcion = detalle.descripcion,
                    cantidad = detalle.cantidad.formatoNumeroCotizacion(),
                    unidad = detalle.unidad,
                    precioUnitario = detalle.precioUnitario.formatoNumeroCotizacion()
                )
            }

            archivosAdjuntos = deserializarComprobantes(
                cotizacionActual.archivosAdjuntosJson
            ).map { archivo ->
                ArchivoEditarCotizacionForm(
                    uri = archivo.uri,
                    nombre = archivo.nombre,
                    detalle = formatearTamanoComprobante(archivo.tamanoBytes),
                    tipoMime = when (archivo.tipo) {
                        "PDF" -> "application/pdf"
                        "Imagen" -> "image/*"
                        else -> "application/octet-stream"
                    },
                    tamanoBytes = archivo.tamanoBytes
                )
            }

            datosCargados = true
        }
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
            )
            {
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
                    text = "Editar Cotización",
                    style = MaterialTheme.typography.titleMedium,
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
                CardAvisoEditarCotizacion()
            }

            item {
                SeccionInformacionGeneralEditarCotizacion(
                    clientes = clientes,
                    clienteSeleccionadoId = clienteSeleccionadoId,
                    onClienteSeleccionado = {
                        clienteSeleccionadoId = it
                        errorCliente = false
                    },
                    errorCliente = errorCliente,
                    proyecto = proyecto,
                    onProyectoChange = { proyecto = it },
                    fecha = fecha,
                    onFechaChange = { fecha = it },
                    vigencia = vigencia,
                    onVigenciaChange = { vigencia = it },
                    folio = folio,
                    onFolioChange = { folio = it },
                    descripcion = descripcion,
                    onDescripcionChange = { descripcion = it }
                )
            }

            item {
                SeccionConceptosEditarCotizacion(
                    conceptos = conceptos,
                    onConceptosChange = { conceptos = it }
                )
            }

            item {
                SeccionResumenEditarCotizacion(
                    subtotal = subtotalCalculado,
                    total = totalCalculado,
                    anticipoSugerido = anticipoCalculado,
                    saldoRestante = saldoCalculado,
                    descuento = descuento,
                    onDescuentoChange = { descuento = it },
                    iva = iva,
                    onIvaChange = { iva = it },
                    anticipo = anticipo,
                    onAnticipoChange = { anticipo = it }
                )
            }

            item {
                SeccionArchivosEditarCotizacion(
                    archivos = archivosAdjuntos,
                    onArchivosChange = { archivosAdjuntos = it }
                )
            }

            item {
                SeccionObservacionesEditarCotizacion(
                    observaciones = observaciones,
                    onObservacionesChange = { observaciones = it }
                )
            }

            item {
                BotonesEditarCotizacion(
                    onCancelarClick = {
                        navController.popBackStack()
                    },
                    onActualizarClick = {
                        val clienteId = clienteSeleccionadoId

                        if (clienteId == null) {
                            errorCliente = true
                            return@BotonesEditarCotizacion
                        }

                        val detallesActualizados = conceptos
                            .filter { it.descripcion.isNotBlank() && it.cantidadNumero > 0.0 && it.precioNumero > 0.0 }
                            .map { concepto ->
                                DetalleCotizacionEntity(
                                    cotizacionId = cotizacionId,
                                    tipo = concepto.tipo,
                                    descripcion = concepto.descripcion.trim(),
                                    cantidad = concepto.cantidadNumero,
                                    unidad = concepto.unidad,
                                    precioUnitario = concepto.precioNumero,
                                    total = concepto.total
                                )
                            }

                        if (detallesActualizados.isEmpty()) {
                            return@BotonesEditarCotizacion
                        }

                        viewModel.actualizarCotizacion(
                            cotizacion = CotizacionEntity(
                                id = cotizacionId,
                                folio = cotizacionEntity?.folio ?: folio,
                                clienteId = clienteId,
                                descripcionTrabajo = descripcion.trim(),
                                proyecto = proyecto.trim(),

                                subtotal = subtotalCalculado,

                                descuentoPorcentaje = descuento.toDoubleOrNull() ?: 0.0,
                                descuento = descuentoCalculado,

                                ivaPorcentaje = iva.toDoubleOrNull() ?: 0.0,
                                iva = ivaCalculado,

                                total = totalCalculado,

                                anticipoPorcentaje = anticipo.toDoubleOrNull() ?: 0.0,
                                anticipo = anticipoCalculado,
                                saldo = saldoCalculado,

                                fecha = fecha,
                                vigencia = vigencia,
                                observaciones = observaciones.trim(),
                                estado = cotizacionEntity?.estado ?: "Pendiente",
                                fechaAprobacion = cotizacionEntity?.fechaAprobacion.orEmpty(),
                                fechaActualizacion = cotizacionEntity?.fechaActualizacion.orEmpty(),
                                archivosAdjuntosJson = serializarComprobantes(
                                    archivosAdjuntos.map { archivo ->
                                        ComprobanteArchivoSeleccionado(
                                            uri = archivo.uri,
                                            tipo = when {
                                                archivo.tipoMime.contains("pdf", ignoreCase = true) -> "PDF"
                                                archivo.tipoMime.startsWith("image", ignoreCase = true) -> "Imagen"
                                                else -> "Archivo"
                                            },
                                            nombre = archivo.nombre,
                                            tamanoBytes = archivo.tamanoBytes
                                        )
                                    }
                                )
                            ),
                            detalles = detallesActualizados,
                            onFinish = {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun CardAvisoEditarCotizacion() {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = 0.dp,
                top = 5.dp,
                end = 0.dp,
                bottom = 0.dp
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFBEB)
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
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "Modificación de cotización",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )

                Text(
                    text = "Revisa los datos antes de guardar los cambios.",
                    fontSize = 10.sp,
                    color = Color(0xFF92400E)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionInformacionGeneralEditarCotizacion(
    clientes: List<ClienteEntity>,
    clienteSeleccionadoId: Int?,
    onClienteSeleccionado: (Int) -> Unit,
    errorCliente: Boolean,
    proyecto: String,
    onProyectoChange: (String) -> Unit,
    fecha: String,
    onFechaChange: (String) -> Unit,
    vigencia: String,
    onVigenciaChange: (String) -> Unit,
    folio: String,
    onFolioChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit
) {
    CardSeccionEditarCotizacion(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        SelectorClienteCotizacion(
            clientes = clientes,
            clienteSeleccionadoId = clienteSeleccionadoId,
            onClienteSeleccionado = onClienteSeleccionado,
            mostrarError = errorCliente
        )

        CampoEditarCotizacion(
            titulo = "Proyecto (opcional)",
            valor = proyecto,
            placeholder = "Seleccionar proyecto",
            onValueChange = onProyectoChange,
            trailingIcon = Icons.Default.KeyboardArrowDown
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoFechaEditarCotizacion(
                titulo = "Fecha *",
                valor = fecha,
                onFechaSeleccionada = onFechaChange,
                modifier = Modifier.weight(1f)
            )

            CampoFechaEditarCotizacion(
                titulo = "Vigencia *",
                valor = vigencia,
                onFechaSeleccionada = onVigenciaChange,
                modifier = Modifier.weight(1f)
            )
        }

        CampoFolioCotizacionSoloLectura(
            titulo = "Folio / Número",
            valor = folio,
            placeholder = "Folio automático"
        )

        CampoTextoLargoEditarCotizacion(
            titulo = "Descripción del trabajo *",
            valor = descripcion,
            placeholder = "Describe el trabajo o proyecto que se va a cotizar...",
            onValueChange = onDescripcionChange
        )
    }
}



@Composable
fun TabEditarCotizacion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .clickable {
                onClick()
            }
            .background(
                color = if (seleccionado) Color.White else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 9.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            color = if (seleccionado) Color(0xFF15803D) else Color.Gray,
            maxLines = 1
        )
    }
}


@Composable
fun EncabezadoConceptosEditarCotizacion() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Concepto",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1.3f)
        )

        Text(
            text = "Cant.",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.5f)
        )

        Text(
            text = "Unidad",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.7f)
        )

        Text(
            text = "Precio",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.8f)
        )

        Text(
            text = "Importe",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.8f)
        )

        Spacer(modifier = Modifier.width(24.dp))
    }

    Divider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = Color(0xFFE2E8F0)
    )
}


@Composable
fun SeccionConceptosEditarCotizacion(
    conceptos: List<ConceptoEditarCotizacionForm>,
    onConceptosChange: (List<ConceptoEditarCotizacionForm>) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf("Materiales") }

    val categorias = listOf(
        "Materiales",
        "Mano de obra",
        "Gastos adicionales"
    )

    val conceptosFiltrados = conceptos
        .mapIndexed { index, concepto -> index to concepto }
        .filter { it.second.tipo == categoriaSeleccionada }

    CardSeccionEditarCotizacion(
        titulo = "Conceptos",
        icono = Icons.Default.FormatListBulleted
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categorias.forEach { categoria ->
                TabEditarCotizacion(
                    texto = categoria,
                    seleccionado = categoriaSeleccionada == categoria,
                    onClick = {
                        categoriaSeleccionada = categoria
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (conceptosFiltrados.isEmpty()) {
            Text(
                text = "No hay conceptos en ${categoriaSeleccionada.lowercase()}.",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        conceptosFiltrados.forEachIndexed { indexVisual, par ->
            val indexReal = par.first
            val concepto = par.second

            ConceptoEditarCotizacionItem(
                numeroConcepto = indexVisual + 1,
                concepto = concepto,
                mostrarEliminar = true,
                onConceptoChange = { conceptoActualizado ->
                    onConceptosChange(
                        conceptos.toMutableList().also {
                            it[indexReal] = conceptoActualizado.copy(
                                tipo = categoriaSeleccionada
                            )
                        }
                    )
                },
                onEliminarClick = {
                    onConceptosChange(
                        conceptos.toMutableList().also {
                            it.removeAt(indexReal)
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        TextButton(
            onClick = {
                onConceptosChange(
                    conceptos + ConceptoEditarCotizacionForm(
                        tipo = categoriaSeleccionada,
                        descripcion = "",
                        cantidad = "1",
                        unidad = unidadDefaultConcepto(categoriaSeleccionada),
                        precioUnitario = ""
                    )
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                tint = Color(0xFF16A34A)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Agregar concepto",
                fontSize = 12.sp,
                color = Color(0xFF16A34A)
            )
        }
    }
}

@Composable
fun ConceptoEditarCotizacionItem(
    numeroConcepto: Int,
    concepto: ConceptoEditarCotizacionForm,
    mostrarEliminar: Boolean,
    onConceptoChange: (ConceptoEditarCotizacionForm) -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Concepto $numeroConcepto",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155),
                    modifier = Modifier.weight(1f)
                )

                if (mostrarEliminar) {
                    IconButton(
                        onClick = onEliminarClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Eliminar concepto",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFFDC2626)
                        )
                    }
                }
            }

            CampoConceptoEditarCotizacion(
                titulo = "Descripción",
                valor = concepto.descripcion,
                onValueChange = {
                    onConceptoChange(concepto.copy(descripcion = it))
                },
                placeholder = "Ej. PTR 2x2 Cal. 14",
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampoConceptoEditarCotizacion(
                    titulo = "Cantidad",
                    valor = concepto.cantidad,
                    onValueChange = {
                        onConceptoChange(concepto.copy(cantidad = it))
                    },
                    placeholder = "1",
                    modifier = Modifier.weight(1f)
                )

                CampoConceptoEditarCotizacion(
                    titulo = "Unidad",
                    valor = concepto.unidad,
                    onValueChange = {
                        onConceptoChange(concepto.copy(unidad = it))
                    },
                    placeholder = "Pza",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                CampoConceptoEditarCotizacion(
                    titulo = "Precio unitario",
                    valor = concepto.precioUnitario,
                    onValueChange = {
                        onConceptoChange(concepto.copy(precioUnitario = it))
                    },
                    placeholder = "0.00",
                    modifier = Modifier.weight(1f)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Importe",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(7.dp)
                            )
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = concepto.total.formatoMonedaEditarCotizacion(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CampoConceptoEditarCotizacion(
    titulo: String,
    valor: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 10.sp
                )
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 11.sp
            ),
            shape = RoundedCornerShape(7.dp)
        )
    }
}
@Composable
fun SeccionResumenEditarCotizacion(
    subtotal: Double,
    total: Double,
    anticipoSugerido: Double,
    saldoRestante: Double,
    descuento: String,
    onDescuentoChange: (String) -> Unit,
    iva: String,
    onIvaChange: (String) -> Unit,
    anticipo: String,
    onAnticipoChange: (String) -> Unit
) {
    val opcionesIva = listOf("0", "8", "16")

    CardSeccionEditarCotizacion(
        titulo = "Resumen de la cotización",
        icono = Icons.Default.ReceiptLong
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CampoEditarCotizacion(
                    titulo = "Subtotal",
                    valor = subtotal.formatoMonedaEditarCotizacion(),
                    placeholder = "",
                    onValueChange = { },
                    readOnly = true
                )

                CampoEditarCotizacion(
                    titulo = "Anticipo requerido (%)",
                    valor = anticipo,
                    placeholder = "50",
                    onValueChange = onAnticipoChange
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                CampoEditarCotizacion(
                    titulo = "Descuento (%)",
                    valor = descuento,
                    placeholder = "0",
                    onValueChange = onDescuentoChange
                )

                CampoDropdownEditarCotizacion(
                    titulo = "IVA (%)",
                    valor = iva,
                    opciones = opcionesIva,
                    placeholder = "IVA",
                    onValueChange = onIvaChange
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Total",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Text(
                    text = total.formatoMonedaEditarCotizacion(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFEAF7EE),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Anticipo sugerido",
                            fontSize = 8.sp,
                            color = Color(0xFF15803D)
                        )

                        Text(
                            text = anticipoSugerido.formatoMonedaEditarCotizacion(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Saldo restante",
                    fontSize = 8.sp,
                    color = Color.Gray
                )

                Text(
                    text = saldoRestante.formatoMonedaEditarCotizacion(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun SeccionArchivosEditarCotizacion(
    archivos: List<ArchivoEditarCotizacionForm>,
    onArchivosChange: (List<ArchivoEditarCotizacionForm>) -> Unit
) {
    val context = LocalContext.current

    fun agregarUris(uris: List<Uri>) {
        val nuevos = uris.mapNotNull { uri ->
            runCatching {
                prepararComprobanteDesdeDocumento(context, uri)
            }.getOrNull()
        }

        val demasiadoGrandes = nuevos.count {
            it.tamanoBytes > MAX_ARCHIVO_ADJUNTO_BYTES
        }

        val actuales = archivos.map {
            ComprobanteArchivoSeleccionado(
                uri = it.uri,
                tipo = when {
                    it.tipoMime.contains("pdf", ignoreCase = true) -> "PDF"
                    it.tipoMime.startsWith("image", ignoreCase = true) -> "Imagen"
                    else -> "Archivo"
                },
                nombre = it.nombre,
                tamanoBytes = it.tamanoBytes
            )
        }

        val resultado = (actuales + nuevos.filter {
            it.tamanoBytes <= MAX_ARCHIVO_ADJUNTO_BYTES
        })
            .distinctBy { it.uri }
            .take(MAX_ARCHIVOS_ADJUNTOS_POR_REGISTRO)

        onArchivosChange(
            resultado.map { archivo ->
                ArchivoEditarCotizacionForm(
                    uri = archivo.uri,
                    nombre = archivo.nombre,
                    detalle = formatearTamanoComprobante(archivo.tamanoBytes),
                    tipoMime = when (archivo.tipo) {
                        "PDF" -> "application/pdf"
                        "Imagen" -> "image/*"
                        else -> "application/octet-stream"
                    },
                    tamanoBytes = archivo.tamanoBytes
                )
            }
        )

        if (demasiadoGrandes > 0) {
            Toast.makeText(
                context,
                "Se omitieron $demasiadoGrandes archivos mayores a 10 MB.",
                Toast.LENGTH_LONG
            ).show()
        } else if (resultado.size >= MAX_ARCHIVOS_ADJUNTOS_POR_REGISTRO &&
            nuevos.isNotEmpty()
        ) {
            Toast.makeText(
                context,
                "Se permiten hasta $MAX_ARCHIVOS_ADJUNTOS_POR_REGISTRO archivos.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val launcherImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        agregarUris(uris)
    }

    val launcherPdf = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        agregarUris(uris)
    }

    val launcherArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        agregarUris(uris)
    }

    CardSeccionEditarCotizacion(
        titulo = "Archivos adjuntos (${archivos.size})",
        icono = Icons.Default.AttachFile
    ) {
        if (archivos.isEmpty()) {
            Text(
                text = "No hay archivos adjuntos.",
                fontSize = 10.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))
        } else {
            archivos.forEachIndexed { index, archivo ->
                ArchivoEditarCotizacionItem(
                    archivo = archivo,
                    onAbrirClick = {
                        val comprobante = ComprobanteArchivoSeleccionado(
                            uri = archivo.uri,
                            tipo = when {
                                archivo.tipoMime.contains("pdf", ignoreCase = true) -> "PDF"
                                archivo.tipoMime.startsWith("image", ignoreCase = true) -> "Imagen"
                                else -> "Archivo"
                            },
                            nombre = archivo.nombre,
                            tamanoBytes = archivo.tamanoBytes
                        )

                        if (!abrirComprobante(context, comprobante)) {
                            Toast.makeText(
                                context,
                                "No se encontró una aplicación para abrir el archivo.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onEliminarClick = {
                        onArchivosChange(
                            archivos.toMutableList().also {
                                it.removeAt(index)
                            }
                        )
                    }
                )

                if (index < archivos.lastIndex) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BotonArchivoEditarCotizacion(
                texto = "Imágenes",
                icono = Icons.Default.Image,
                modifier = Modifier.weight(1f),
                onClick = {
                    launcherImagen.launch(arrayOf("image/*"))
                }
            )

            BotonArchivoEditarCotizacion(
                texto = "Archivo",
                icono = Icons.Default.AttachFile,
                modifier = Modifier.weight(1f),
                onClick = {
                    launcherArchivo.launch(arrayOf("*/*"))
                }
            )

            BotonArchivoEditarCotizacion(
                texto = "PDF",
                icono = Icons.Default.PictureAsPdf,
                modifier = Modifier.weight(1f),
                onClick = {
                    launcherPdf.launch(arrayOf("application/pdf"))
                }
            )
        }
    }
}

@Composable
fun ArchivoEditarCotizacionItem(
    archivo: ArchivoEditarCotizacionForm,
    onAbrirClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    val context = LocalContext.current
    val comprobante = remember(archivo) {
        ComprobanteArchivoSeleccionado(
            uri = archivo.uri,
            tipo = when {
                archivo.tipoMime.contains("pdf", ignoreCase = true) -> "PDF"
                archivo.tipoMime.startsWith("image", ignoreCase = true) -> "Imagen"
                else -> "Archivo"
            },
            nombre = archivo.nombre,
            tamanoBytes = archivo.tamanoBytes
        )
    }
    val tipoReal = obtenerTipoRealComprobante(context, comprobante)

    val icono = when (tipoReal) {
        "PDF" -> Icons.Default.PictureAsPdf
        "Imagen" -> Icons.Default.Image
        else -> Icons.Default.AttachFile
    }

    val color = when (tipoReal) {
        "PDF" -> Color(0xFFDC2626)
        "Imagen" -> Color(0xFF2563EB)
        else -> Color(0xFF334155)
    }

    val fondo = when (tipoReal) {
        "PDF" -> Color(0xFFFEE2E2)
        "Imagen" -> Color(0xFFEFF6FF)
        else -> Color(0xFFF1F5F9)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onAbrirClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(fondo, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = tipoReal,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = archivo.nombre,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 2
            )

            Text(
                text = "$tipoReal · ${archivo.detalle}",
                fontSize = 8.sp,
                color = Color.Gray
            )
        }

        IconButton(
            onClick = onEliminarClick,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Eliminar archivo",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
fun BotonArchivoEditarCotizacion(
    texto: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF334155)
            )

            Text(
                text = texto,
                fontSize = 8.sp,
                color = Color.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SeccionObservacionesEditarCotizacion(
    observaciones: String,
    onObservacionesChange: (String) -> Unit
) {
    CardSeccionEditarCotizacion(
        titulo = "Observaciones",
        icono = Icons.Default.Search
    ) {
        CampoTextoLargoEditarCotizacion(
            titulo = "Observaciones opcional",
            valor = observaciones,
            placeholder = "Agrega notas u observaciones adicionales...",
            onValueChange = onObservacionesChange
        )
    }
}

@Composable
fun BotonesEditarCotizacion(
    onCancelarClick: () -> Unit,
    onActualizarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCancelarClick,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Cancelar",
                fontSize = 12.sp
            )
        }

        Button(
            onClick = onActualizarClick,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Actualizar",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CardSeccionEditarCotizacion(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            contenido()
        }
    }
}

@Composable
fun CampoEditarCotizacion(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    readOnly: Boolean = false
) {
    Column(
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            readOnly = readOnly,
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 10.sp
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
            trailingIcon = if (trailingIcon != null) {
                {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp
            ),
            shape = RoundedCornerShape(7.dp)
        )
    }
}

@Composable
fun CampoTextoLargoEditarCotizacion(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 10.sp
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp
            ),
            shape = RoundedCornerShape(7.dp),
            maxLines = 3
        )
    }
}

data class ConceptoEditarCotizacionForm(
    val tipo: String = "Materiales",
    val descripcion: String = "",
    val cantidad: String = "",
    val unidad: String = "Pza",
    val precioUnitario: String = ""
) {
    val cantidadNumero: Double
        get() = cantidad.replace(",", ".").toDoubleOrNull() ?: 0.0

    val precioNumero: Double
        get() = precioUnitario.replace(",", ".").toDoubleOrNull() ?: 0.0

    val total: Double
        get() = cantidadNumero * precioNumero
}

data class ArchivoEditarCotizacionForm(
    val uri: String,
    val nombre: String,
    val detalle: String,
    val tipoMime: String,
    val tamanoBytes: Long = 0L
)

fun Double.formatoMonedaEditarCotizacion(): String {
    return "$ ${"%,.2f".format(this)}"
}

fun Double.formatoNumeroCotizacion(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaEditarCotizacion(
    titulo: String,
    valor: String,
    onFechaSeleccionada: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = "Seleccionar fecha",
                        fontSize = 10.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Seleccionar fecha",
                        modifier = Modifier.size(16.dp)
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 10.sp
                ),
                shape = RoundedCornerShape(7.dp),
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
                            val formato = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )

                            formato.timeZone = TimeZone.getTimeZone("UTC")

                            onFechaSeleccionada(
                                formato.format(Date(fechaSeleccionada))
                            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDropdownEditarCotizacion(
    titulo: String,
    valor: String,
    opciones: List<String>,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

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
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = placeholder,
                        fontSize = 10.sp
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandido
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 10.sp
                ),
                shape = RoundedCornerShape(7.dp)
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
                                fontSize = 12.sp
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

fun crearArchivoEditarCotizacionDesdeUri(
    context: Context,
    uri: Uri
): ArchivoEditarCotizacionForm {
    val nombre = obtenerNombreArchivoEditarCotizacion(context, uri)
    val tamano = obtenerTamanoArchivoEditarCotizacion(context, uri)
    val tipoMime = context.contentResolver.getType(uri) ?: "archivo"

    return ArchivoEditarCotizacionForm(
        uri = uri.toString(),
        nombre = nombre,
        detalle = tamano,
        tipoMime = tipoMime
    )
}

fun obtenerNombreArchivoEditarCotizacion(
    context: Context,
    uri: Uri
): String {
    var nombre = "Archivo adjunto"

    val cursor = context.contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )

    cursor?.use {
        val indiceNombre = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

        if (indiceNombre >= 0 && it.moveToFirst()) {
            nombre = it.getString(indiceNombre)
        }
    }

    return nombre
}

fun obtenerTamanoArchivoEditarCotizacion(
    context: Context,
    uri: Uri
): String {
    var bytes = 0L

    val cursor = context.contentResolver.query(
        uri,
        null,
        null,
        null,
        null
    )

    cursor?.use {
        val indiceTamano = it.getColumnIndex(OpenableColumns.SIZE)

        if (indiceTamano >= 0 && it.moveToFirst()) {
            bytes = it.getLong(indiceTamano)
        }
    }

    return when {
        bytes <= 0L -> "Tamaño desconocido"
        bytes < 1024L -> "$bytes B"
        bytes < 1024L * 1024L -> "${bytes / 1024L} KB"
        else -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
    }
}