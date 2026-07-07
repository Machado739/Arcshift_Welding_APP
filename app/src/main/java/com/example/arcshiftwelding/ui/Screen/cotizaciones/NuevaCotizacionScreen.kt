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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity
import com.example.arcshiftwelding.navigation.AppRoutes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.saveable.rememberSaveable

data class ConceptoCotizacionForm(
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

fun Double.formatoMonedaCotizacion(): String {
    return "$ ${"%,.2f".format(this)}"
}
fun unidadDefaultConcepto(tipo: String): String {
    return when (tipo) {
        "Materiales" -> "Pza"
        "Mano de obra" -> "Servicio"
        "Gastos adicionales" -> "Gasto"
        else -> "Pza"
    }
}

fun tituloTipoConcepto(tipo: String): String {
    return when (tipo) {
        "Materiales" -> "Material"
        "Mano de obra" -> "Mano de obra"
        "Gastos adicionales" -> "Gasto adicional"
        else -> "Concepto"
    }
}

fun placeholderDescripcionConcepto(tipo: String): String {
    return when (tipo) {
        "Materiales" -> "Ej. PTR 2x2 Cal. 14"
        "Mano de obra" -> "Ej. Fabricación e instalación"
        "Gastos adicionales" -> "Ej. Flete, viáticos o consumibles"
        else -> "Descripción del concepto"
    }
}

fun colorConceptoCotizacion(tipo: String): Color {
    return when (tipo) {
        "Materiales" -> Color(0xFF15803D)
        "Mano de obra" -> Color(0xFF2563EB)
        "Gastos adicionales" -> Color(0xFFF59E0B)
        else -> Color(0xFF334155)
    }
}

fun fondoConceptoCotizacion(tipo: String): Color {
    return when (tipo) {
        "Materiales" -> Color(0xFFEAF7EE)
        "Mano de obra" -> Color(0xFFEFF6FF)
        "Gastos adicionales" -> Color(0xFFFFF7E6)
        else -> Color(0xFFF1F5F9)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCotizacionScreen(
    navController: NavController,
    viewModel: CotizacionesViewModel
) {
    val clientes by viewModel.clientesActivos.collectAsState(initial = emptyList())



    var clienteSeleccionadoId by rememberSaveable { mutableStateOf<Int?>(null) }
    var errorCliente by rememberSaveable { mutableStateOf(false) }
    var proyecto by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf("19/05/2026") }
    var vigencia by rememberSaveable { mutableStateOf("02/06/2026") }
    val folio by viewModel.siguienteFolio.collectAsState()
    var descripcion by rememberSaveable { mutableStateOf("") }

    var descuento by rememberSaveable { mutableStateOf("0") }
    var iva by rememberSaveable { mutableStateOf("16") }
    var anticipo by rememberSaveable { mutableStateOf("50") }
    var observaciones by rememberSaveable { mutableStateOf("") }

    var conceptos by rememberSaveable {
        mutableStateOf<List<ConceptoCotizacionForm>>(emptyList())
    }

    val subtotalCalculado = conceptos.sumOf { it.total }
    val descuentoCalculado = subtotalCalculado * ((descuento.toDoubleOrNull() ?: 0.0) / 100.0)
    val subtotalConDescuento = subtotalCalculado - descuentoCalculado
    val ivaCalculado = subtotalConDescuento * ((iva.toDoubleOrNull() ?: 0.0) / 100.0)
    val totalCalculado = subtotalConDescuento + ivaCalculado
    val anticipoCalculado = totalCalculado * ((anticipo.toDoubleOrNull() ?: 0.0) / 100.0)
    val saldoCalculado = totalCalculado - anticipoCalculado


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
            ){
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
                    text = "Nueva Cotización",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0)
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
                .padding(
                    start = 8.dp,
                    top = 0.dp,
                    end = 8.dp,
                    bottom = 8.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            item {
                SeccionInformacionGeneralNuevaCotizacion(
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
                    descripcion = descripcion,
                    onDescripcionChange = { descripcion = it },
                    onNuevoClienteClick = {
                        navController.navigate(AppRoutes.NUEVO_CLIENTE)
                    },
                )
            }

            item {
                SeccionConceptosNuevaCotizacion(
                    conceptos = conceptos,
                    onConceptosChange = { conceptos = it }
                )
            }

            item {
                SeccionResumenNuevaCotizacion(
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
                SeccionArchivosNuevaCotizacion()
            }

            item {
                SeccionObservacionesNuevaCotizacion(
                    observaciones = observaciones,
                    onObservacionesChange = { observaciones = it }
                )
            }

            item {
                BotonesNuevaCotizacion(
                    onCancelarClick = {
                        navController.popBackStack()
                    },
                    onGuardarClick = {
                        val clienteId = clienteSeleccionadoId

                        if (clienteId == null) {
                            errorCliente = true
                            return@BotonesNuevaCotizacion
                        }

                        val detallesCotizacion = conceptos
                            .filter {
                                it.descripcion.isNotBlank() &&
                                        it.cantidadNumero > 0.0 &&
                                        it.precioNumero > 0.0
                            }
                            .map { concepto ->
                                DetalleCotizacionEntity(
                                    cotizacionId = 0,
                                    tipo = concepto.tipo,
                                    descripcion = concepto.descripcion,
                                    cantidad = concepto.cantidadNumero,
                                    unidad = concepto.unidad,
                                    precioUnitario = concepto.precioNumero,
                                    total = concepto.total
                                )
                            }

                        if (detallesCotizacion.isEmpty()) {
                            return@BotonesNuevaCotizacion
                        }

                        viewModel.guardarCotizacion(
                            clienteId = clienteId,
                            descripcionTrabajo = descripcion.trim(),
                            proyecto = proyecto.trim(),
                            subtotal = subtotalCalculado,
                            iva = ivaCalculado,
                            total = totalCalculado,
                            fecha = fecha,
                            descuentoPorcentaje = descuento.toDoubleOrNull() ?: 0.0,
                            descuento = descuentoCalculado,
                            ivaPorcentaje = iva.toDoubleOrNull() ?: 0.0,
                            anticipoPorcentaje = anticipo.toDoubleOrNull() ?: 0.0,
                            anticipo = anticipoCalculado,
                            saldo = saldoCalculado,
                            vigencia = vigencia,
                            observaciones = observaciones.trim(),
                            estado = "Pendiente",
                            detalles = detallesCotizacion,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionInformacionGeneralNuevaCotizacion(
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
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    onNuevoClienteClick: () -> Unit
){
    CardSeccionFormularioCotizacion(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectorClienteCotizacion(
                clientes = clientes,
                clienteSeleccionadoId = clienteSeleccionadoId,
                onClienteSeleccionado = onClienteSeleccionado,
                mostrarError = errorCliente,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    onNuevoClienteClick()
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar cliente"
                )
            }
        }

        CampoFormularioCotizacion(
            titulo = "Proyecto (opcional)",
            valor = proyecto,
            placeholder = "Ej. Portón de acceso, escalera metálica, estructura para techo",
            onValueChange = onProyectoChange
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoFechaCotizacion(
                titulo = "Fecha *",
                valor = fecha,
                onFechaSeleccionada = onFechaChange,
                modifier = Modifier.weight(1f)
            )

            CampoFechaCotizacion(
                titulo = "Vigencia *",
                valor = vigencia,
                onFechaSeleccionada = onVigenciaChange,
                modifier = Modifier.weight(1f)
            )
        }

        CampoFolioCotizacionSoloLectura(
            titulo = "Folio / Número",
            valor = folio,
            placeholder = "Se asignará automáticamente"
        )

        CampoTextoLargoCotizacion(
            titulo = "Descripción del trabajo *",
            valor = descripcion,
            placeholder = "Describe el trabajo o proyecto que se va a cotizar...",
            onValueChange = onDescripcionChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorClienteCotizacion(
    clientes: List<ClienteEntity>,
    clienteSeleccionadoId: Int?,
    onClienteSeleccionado: (Int) -> Unit,
    mostrarError: Boolean,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val clienteSeleccionado = clientes.firstOrNull { cliente ->
        cliente.id == clienteSeleccionadoId
    }

    Column(
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = "Cliente *",
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
                value = clienteSeleccionado?.nombre ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = "Seleccionar cliente",
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
                shape = RoundedCornerShape(7.dp),
                isError = mostrarError
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = {
                    expandido = false
                }
            ) {
                if (clientes.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text("No hay clientes activos")
                        },
                        onClick = {
                            expandido = false
                        }
                    )
                } else {
                    clientes.forEach { cliente ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = cliente.nombre,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    if (cliente.empresa.isNotBlank()) {
                                        Text(
                                            text = cliente.empresa,
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onClienteSeleccionado(cliente.id)
                                expandido = false
                            }
                        )
                    }
                }
            }
        }

        if (mostrarError) {
            Text(
                text = "Selecciona un cliente",
                fontSize = 9.sp,
                color = Color(0xFFDC2626),
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}


@Composable
fun TabConceptoCotizacion(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = colorConceptoCotizacion(texto)
    val fondo = fondoConceptoCotizacion(texto)

    Box(
        modifier = modifier
            .height(34.dp)
            .clickable {
                onClick()
            }
            .background(
                color = if (seleccionado) fondo else Color.Transparent,
                shape = RoundedCornerShape(7.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 9.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            color = if (seleccionado) color else Color.Gray,
            maxLines = 1
        )
    }
}


@Composable
fun EncabezadoNuevaCotizacionConceptos() {
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
fun SeccionConceptosNuevaCotizacion(
    conceptos: List<ConceptoCotizacionForm>,
    onConceptosChange: (List<ConceptoCotizacionForm>) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf("Materiales") }

    val categorias = listOf(
        "Materiales",
        "Mano de obra",
        "Gastos adicionales"
    )

    val conceptosCategoria = conceptos
        .mapIndexed { index, concepto -> index to concepto }
        .filter { (_, concepto) ->
            concepto.tipo == categoriaSeleccionada
        }

    CardSeccionFormularioCotizacion(
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
                TabConceptoCotizacion(
                    texto = categoria,
                    seleccionado = categoriaSeleccionada == categoria,
                    onClick = {
                        categoriaSeleccionada = categoria
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (categoriaSeleccionada) {
                "Materiales" -> "Agrega materiales usados en el trabajo."
                "Mano de obra" -> "Agrega costos por fabricación, instalación o servicio."
                "Gastos adicionales" -> "Agrega fletes, viáticos, consumibles u otros gastos."
                else -> ""
            },
            fontSize = 9.sp,
            color = colorConceptoCotizacion(categoriaSeleccionada),
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = fondoConceptoCotizacion(categoriaSeleccionada),
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (conceptosCategoria.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFBEB)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Aún no hay conceptos agregados en esta categoría.",
                    fontSize = 10.sp,
                    color = Color(0xFF92400E),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        } else {
            conceptosCategoria.forEachIndexed { numeroVisible, item ->
                val indexReal = item.first
                val concepto = item.second

                ConceptoNuevaCotizacionItem(
                    numeroConcepto = numeroVisible + 1,
                    concepto = concepto,
                    mostrarEliminar = true,
                    onConceptoChange = { conceptoActualizado ->
                        onConceptosChange(
                            conceptos.mapIndexed { index, conceptoActual ->
                                if (index == indexReal) {
                                    conceptoActualizado
                                } else {
                                    conceptoActual
                                }
                            }
                        )
                    },
                    onEliminarClick = {
                        onConceptosChange(
                            conceptos.filterIndexed { index, _ ->
                                index != indexReal
                            }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        TextButton(
            onClick = {
                onConceptosChange(
                    conceptos + ConceptoCotizacionForm(
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
                tint = colorConceptoCotizacion(categoriaSeleccionada)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Agregar ${tituloTipoConcepto(categoriaSeleccionada).lowercase()}",
                fontSize = 12.sp,
                color = colorConceptoCotizacion(categoriaSeleccionada)
            )
        }
    }
}

@Composable
fun ConceptoNuevaCotizacionItem(
    numeroConcepto: Int,
    concepto: ConceptoCotizacionForm,
    mostrarEliminar: Boolean,
    onConceptoChange: (ConceptoCotizacionForm) -> Unit,
    onEliminarClick: () -> Unit
) {
    val colorTipo = colorConceptoCotizacion(concepto.tipo)
    val fondoTipo = fondoConceptoCotizacion(concepto.tipo)

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

                Text(
                    text = tituloTipoConcepto(concepto.tipo),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorTipo,
                    modifier = Modifier
                        .background(
                            color = fondoTipo,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )

                if (mostrarEliminar) {
                    Spacer(modifier = Modifier.width(6.dp))

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

            CampoConceptoCotizacion(
                titulo = "Descripción",
                valor = concepto.descripcion,
                onValueChange = {
                    onConceptoChange(concepto.copy(descripcion = it))
                },
                placeholder = placeholderDescripcionConcepto(concepto.tipo),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampoConceptoCotizacion(
                    titulo = "Cantidad",
                    valor = concepto.cantidad,
                    onValueChange = {
                        onConceptoChange(concepto.copy(cantidad = it))
                    },
                    placeholder = "1",
                    modifier = Modifier.weight(1f)
                )

                CampoConceptoCotizacion(
                    titulo = "Unidad",
                    valor = concepto.unidad,
                    onValueChange = {
                        onConceptoChange(concepto.copy(unidad = it))
                    },
                    placeholder = unidadDefaultConcepto(concepto.tipo),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                CampoConceptoCotizacion(
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
                            text = concepto.total.formatoMonedaCotizacion(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorTipo,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CampoConceptoCotizacion(
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
fun CampoMiniCotizacion(
    texto: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(28.dp)
            .padding(horizontal = 2.dp)
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 8.sp,
            color = Color.Black,
            maxLines = 1
        )
    }
}


@Composable
fun SeccionResumenNuevaCotizacion(
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

    CardSeccionFormularioCotizacion(
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
                CampoFormularioCotizacion(
                    titulo = "Subtotal",
                    valor = subtotal.formatoMonedaCotizacion(),
                    placeholder = "",
                    onValueChange = { },
                    readOnly = true
                )

                CampoFormularioCotizacion(
                    titulo = "Anticipo requerido (%)",
                    valor = anticipo,
                    placeholder = "50",
                    onValueChange = onAnticipoChange
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                CampoFormularioCotizacion(
                    titulo = "Descuento (%)",
                    valor = descuento,
                    placeholder = "0",
                    onValueChange = onDescuentoChange
                )

                CampoDropdownCotizacion(
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
                    text = total.formatoMonedaCotizacion(),
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
                            text = anticipoSugerido.formatoMonedaCotizacion(),
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
                    text = saldoRestante.formatoMonedaCotizacion(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun SeccionArchivosNuevaCotizacion() {
    CardSeccionFormularioCotizacion(
        titulo = "Archivos adjuntos",
        icono = Icons.Default.AttachFile
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BotonArchivoNuevaCotizacion(
                texto = "Agregar imagen",
                icono = Icons.Default.Image,
                modifier = Modifier.weight(1f)
            )

            BotonArchivoNuevaCotizacion(
                texto = "Adjuntar archivo",
                icono = Icons.Default.AttachFile,
                subtitulo = "Máx. 10 MB",
                modifier = Modifier.weight(1f)
            )

            BotonArchivoNuevaCotizacion(
                texto = "Subir PDF",
                icono = Icons.Default.Description,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BotonArchivoNuevaCotizacion(
    texto: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    subtitulo: String? = null
) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(48.dp),
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

            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    fontSize = 7.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SeccionObservacionesNuevaCotizacion(
    observaciones: String,
    onObservacionesChange: (String) -> Unit
) {
    CardSeccionFormularioCotizacion(
        titulo = "Observaciones",
        icono = Icons.Default.Search
    ) {
        CampoTextoLargoCotizacion(
            titulo = "Observaciones opcional",
            valor = observaciones,
            placeholder = "Agrega notas u observaciones adicionales...",
            onValueChange = onObservacionesChange
        )
    }
}


@Composable
fun BotonesNuevaCotizacion(
    onCancelarClick: () -> Unit,
    onGuardarClick: () -> Unit
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
            onClick = onGuardarClick,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF15803D)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Guardar Cotización",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CardSeccionFormularioCotizacion(
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
fun CampoFormularioCotizacion(
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
fun CampoTextoLargoCotizacion(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaCotizacion(
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
fun CampoDropdownCotizacion(
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

@Composable
fun CampoFolioCotizacionSoloLectura(
    titulo: String,
    valor: String,
    placeholder: String,
    modifier: Modifier = Modifier
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
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 10.sp
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Folio automático",
                    tint = Color(0xFF64748B)
                )
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334155)
            ),
            shape = RoundedCornerShape(7.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedContainerColor = Color(0xFFF1F5F9),
                focusedBorderColor = Color(0xFFCBD5E1),
                unfocusedBorderColor = Color(0xFFCBD5E1)
            )
        )
    }
}

@Composable
fun FilaConceptoNuevaCotizacion(
    concepto: ConceptoCotizacionForm,
    onConceptoChange: (ConceptoCotizacionForm) -> Unit,
    onEliminarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = concepto.descripcion,
            onValueChange = {
                onConceptoChange(concepto.copy(descripcion = it))
            },
            modifier = Modifier.weight(1.3f),
            placeholder = {
                Text(
                    text = placeholderDescripcionConcepto(concepto.tipo),
                    fontSize = 9.sp
                )
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 10.sp)
        )

        OutlinedTextField(
            value = concepto.cantidad,
            onValueChange = {
                onConceptoChange(concepto.copy(cantidad = it))
            },
            modifier = Modifier.weight(0.5f),
            placeholder = {
                Text("0", fontSize = 9.sp)
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 10.sp)
        )

        OutlinedTextField(
            value = concepto.unidad,
            onValueChange = {
                onConceptoChange(concepto.copy(unidad = it))
            },
            modifier = Modifier.weight(0.7f),
            placeholder = {
                Text("Pza", fontSize = 9.sp)
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 10.sp)
        )

        OutlinedTextField(
            value = concepto.precioUnitario,
            onValueChange = {
                onConceptoChange(concepto.copy(precioUnitario = it))
            },
            modifier = Modifier.weight(0.8f),
            placeholder = {
                Text("$0", fontSize = 9.sp)
            },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 10.sp)
        )

        Text(
            text = concepto.total.formatoMonedaCotizacion(),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8f)
        )

        IconButton(
            onClick = onEliminarClick,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar concepto",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}