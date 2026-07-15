package com.example.arcshiftwelding.ui.Screen.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.repository.MovimientoInventarioRepository
import com.example.arcshiftwelding.data.repository.ProductoRepository
import com.example.arcshiftwelding.ui.viewmodel.MovimientoInventarioViewModel
import com.example.arcshiftwelding.ui.viewmodel.MovimientoInventarioViewModelFactory
import com.example.arcshiftwelding.ui.viewmodel.ProductoViewModel
import com.example.arcshiftwelding.ui.viewmodel.ProductoViewModelFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.example.arcshiftwelding.ui.theme.arcshiftColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportarSalidaScreen(
    navController: NavController,
    productoId: Int
) {
    val context = LocalContext.current

    val database = remember {
        ArcshiftWeldingDatabase.getDatabase(context)
    }

    val productoRepository = remember {
        ProductoRepository(database.productoDao())
    }

    val productoViewModel: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(productoRepository)
    )

    val movimientoRepository = remember {
        MovimientoInventarioRepository(database.movimientoInventarioDao())
    }

    val movimientoViewModel: MovimientoInventarioViewModel = viewModel(
        factory = MovimientoInventarioViewModelFactory(movimientoRepository)
    )

    val productoSeleccionado by productoViewModel.productoSeleccionado.collectAsState()

    LaunchedEffect(productoId) {
        productoViewModel.cargarProductoPorId(productoId)
    }

    var cantidadSalida by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
   /* var responsable by remember { mutableStateOf("") }*/
    var notas by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

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
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }

                Text(
                    text = "Reportar Salida",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->

        val producto = productoSeleccionado

        if (producto == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando producto...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            return@Scaffold
        }

        val stockActual = producto.stock
        val stockMinimo = producto.stockMinimo
        val unidadMedida = producto.unidad
        val costoUnitario = producto.precioCompra
        val permitirStockNegativo = producto.permitirStockNegativo

        val cantidad = cantidadSalida.toIntOrNull() ?: 0
        val nuevoStock = stockActual - cantidad
        val costoSalida = cantidad * costoUnitario

        val cantidadValida = cantidad > 0
        val hayStockSuficiente = nuevoStock >= 0
        val motivoValido = motivo.isNotBlank()

        val puedeGuardar =
            cantidadValida &&
                    motivoValido &&
                    (hayStockSuficiente || permitirStockNegativo)

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            CardProductoSalidaStock(
                nombreProducto = producto.nombre,
                codigoProducto = producto.codigo,
                stockActual = stockActual,
                stockMinimo = stockMinimo,
                unidadMedida = unidadMedida
            )

            CardCantidadSalida(
                cantidadSalida = cantidadSalida,
                onCantidadChange = {
                    cantidadSalida = it
                    mensajeError = ""
                },
                stockActual = stockActual,
                nuevoStock = nuevoStock,
                unidadMedida = unidadMedida,
                hayStockSuficiente = hayStockSuficiente,
                permitirStockNegativo = permitirStockNegativo
            )

            CardDatosSalida(
                motivo = motivo,
                onMotivoChange = {
                    motivo = it
                    mensajeError = ""
                },
                referencia = referencia,
                onReferenciaChange = { referencia = it },
              /*  responsable = responsable,
                onResponsableChange = { responsable = it },*/
                notas = notas,
                onNotasChange = { notas = it }
            )

            CardResumenCostoSalida(
                cantidad = cantidad,
                costoUnitario = costoUnitario,
                costoSalida = costoSalida
            )

            if (mensajeError.isNotBlank()) {
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (!cantidadValida) {
                            mensajeError = "Ingresa una cantidad válida"
                            return@Button
                        }

                        if (!hayStockSuficiente && !permitirStockNegativo) {
                            mensajeError = "No hay stock suficiente para esta salida"
                            return@Button
                        }

                        if (motivo.isBlank()) {
                            mensajeError = "Ingresa el motivo de salida"
                            return@Button
                        }

                        val estadoCalculado = when {
                            nuevoStock <= 0 -> "Agotado"
                            nuevoStock <= producto.stockMinimo -> "Bajo Stock"
                            else -> "En Stock"
                        }

                        val productoActualizado = ProductoEntity(
                            id = producto.id,

                            nombre = producto.nombre,
                            categoria = producto.categoria,
                            codigo = producto.codigo,
                            ubicacion = producto.ubicacion,

                            stock = nuevoStock,
                            unidad = producto.unidad,
                            stockMinimo = producto.stockMinimo,
                            stockMaximo = producto.stockMaximo,

                            estado = estadoCalculado,

                            precioCompra = producto.precioCompra,
                            precioVenta = producto.precioVenta,

                            descripcion = producto.descripcion,
                            proveedor = producto.proveedor,
                            notas = producto.notas,

                            imagenUri = producto.imagenUri,

                            permitirStockNegativo = producto.permitirStockNegativo,
                            activo = producto.activo,

                            fechaRegistro = producto.fechaRegistro
                        )

                        productoViewModel.actualizarProducto(productoActualizado)

                        val fechaActual = LocalDate.now()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                        val horaActual = LocalTime.now()
                            .format(DateTimeFormatter.ofPattern("HH:mm"))

                        val movimientoSalida = MovimientoInventarioEntity(
                            productoId = producto.id,
                            clienteId = null,
                            cotizacionId = null,
                            tipo = "Salida",
                            cantidad = cantidad,
                            stockAnterior = stockActual,
                            stockNuevo = nuevoStock,
                            unidad = unidadMedida,
                            fecha = fechaActual,
                            hora = horaActual,
                      /*      usuario = responsable.ifBlank { "Admin" },*/
                            referencia = referencia.ifBlank { "SAL-${producto.id}" },
                            observaciones = buildString {
                                append("Motivo: ${motivo.trim()}")

                                if (notas.isNotBlank()) {
                                    append(". Notas: ${notas.trim()}")
                                }
                            }
                        )

                        movimientoViewModel.insertarMovimiento(movimientoSalida)

                        navController.popBackStack()
                    },
                    enabled = puedeGuardar,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Guardar")
                }
            }
        }
    }
}
@Composable
fun CardProductoSalidaStock(
    nombreProducto: String,
    codigoProducto: String,
    stockActual: Int,
    stockMinimo: Int,
    unidadMedida: String
) {
    val stockBajo = stockActual <= stockMinimo

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombreProducto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Código: $codigoProducto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Stock actual: $stockActual $unidadMedida",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (stockBajo) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.arcshiftColors.success,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Stock mínimo: $stockMinimo $unidadMedida",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CardCantidadSalida(
    cantidadSalida: String,
    onCantidadChange: (String) -> Unit,
    stockActual: Int,
    nuevoStock: Int,
    unidadMedida: String,
    hayStockSuficiente: Boolean,
    permitirStockNegativo: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.RemoveCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Cantidad a retirar",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cantidadSalida,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.all { it.isDigit() }) {
                        onCantidadChange(nuevoValor)
                    }
                },
                label = {
                    Text("Cantidad *")
                },
                placeholder = {
                    Text("Ej. 2")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = cantidadSalida.isNotBlank() && !hayStockSuficiente && !permitirStockNegativo,
                supportingText = {
                    if (cantidadSalida.isNotBlank() && !hayStockSuficiente && !permitirStockNegativo) {
                        Text("No hay stock suficiente para esta salida.")
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CajaResumenSalida(
                    titulo = "Stock actual",
                    valor = stockActual.toString(),
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f)
                )

                CajaResumenSalida(
                    titulo = "Retirar",
                    valor = "- ${cantidadSalida.ifBlank { "0" }}",
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f),
                    salida = true
                )

                CajaResumenSalida(
                    titulo = "Nuevo stock",
                    valor = nuevoStock.toString(),
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f),
                    destacado = true,
                    error = nuevoStock < 0
                )
            }

            if (nuevoStock < 0 && !permitirStockNegativo) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "La salida dejaría el inventario en negativo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CajaResumenSalida(
    titulo: String,
    valor: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
    salida: Boolean = false,
    error: Boolean = false
) {
    val fondo = when {
        error -> MaterialTheme.colorScheme.errorContainer
        destacado -> MaterialTheme.arcshiftColors.successContainer
        salida -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val colorTexto = when {
        error -> MaterialTheme.colorScheme.onErrorContainer
        salida -> MaterialTheme.colorScheme.onErrorContainer
        destacado -> MaterialTheme.arcshiftColors.success
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = fondo
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorTexto
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CardDatosSalida(
    motivo: String,
    onMotivoChange: (String) -> Unit,
    referencia: String,
    onReferenciaChange: (String) -> Unit,
 /*   responsable: String,
    onResponsableChange: (String) -> Unit,*/
    notas: String,
    onNotasChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Datos de la salida",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            OutlinedTextField(
                value = motivo,
                onValueChange = onMotivoChange,
                label = {
                    Text("Motivo de salida *")
                },
                placeholder = {
                    Text("Ej. Venta, uso en trabajo, ajuste")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = referencia,
                onValueChange = onReferenciaChange,
                label = {
                    Text("Referencia")
                },
                placeholder = {
                    Text("Ej. SAL-008, cotización, orden")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

           /* OutlinedTextField(
                value = responsable,
                onValueChange = onResponsableChange,
                label = {
                    Text("Responsable")
                },
                placeholder = {
                    Text("Ej. Juan Pérez")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )*/

            OutlinedTextField(
                value = notas,
                onValueChange = {
                    if (it.length <= 150) {
                        onNotasChange(it)
                    }
                },
                label = {
                    Text("Notas")
                },
                placeholder = {
                    Text("Observaciones de la salida")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                supportingText = {
                    Text("${notas.length}/150")
                }
            )
        }
    }
}

@Composable
fun CardResumenCostoSalida(
    cantidad: Int,
    costoUnitario: Double,
    costoSalida: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Resumen de salida",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cantidad retirada")
                Text(
                    text = cantidad.toString(),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Costo unitario")
                Text(
                    text = "$ ${"%.2f".format(costoUnitario)}",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Costo de salida")
                Text(
                    text = "$ ${"%.2f".format(costoSalida)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}