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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import java.util.Locale
import com.example.arcshiftwelding.ui.theme.arcshiftColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReponerStockScreen(
    navController: NavController,
    productoId: Int
) {
    val context = androidx.compose.ui.platform.LocalContext.current

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

    var cantidadAgregar by remember { mutableStateOf("") }
    var costoUnitarioEntrada by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var datosCargados by remember { mutableStateOf(false) }

    LaunchedEffect(productoSeleccionado) {
        val producto = productoSeleccionado

        if (producto != null && !datosCargados) {
            costoUnitarioEntrada = if (producto.precioCompra > 0.0) {
                "%.2f".format(Locale.US, producto.precioCompra)
            } else {
                ""
            }

            proveedor = producto.proveedor
            datosCargados = true
        }
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
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Reponer Stock",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Entrada de inventario",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
        val stockMaximo = producto.stockMaximo
        val unidadMedida = producto.unidad

        val cantidad = cantidadAgregar.toIntOrNull() ?: 0
        val costoUnitario = costoUnitarioEntrada.replace(",", ".").toDoubleOrNull() ?: 0.0

        val nuevoStock = stockActual + cantidad
        val costoMovimiento = cantidad * costoUnitario

        val cantidadValida = cantidad > 0
        val costoValido = costoUnitario > 0.0
        val superaStockMaximo = stockMaximo > 0 && nuevoStock > stockMaximo

        val puedeGuardar = cantidadValida && costoValido

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardProductoReponerStockMejorado(
                nombreProducto = producto.nombre,
                codigoProducto = producto.codigo,
                categoria = producto.categoria,
                ubicacion = producto.ubicacion,
                stockActual = stockActual,
                stockMinimo = stockMinimo,
                stockMaximo = stockMaximo,
                unidadMedida = unidadMedida
            )

            CardCantidadReponerMejorado(
                cantidadAgregar = cantidadAgregar,
                onCantidadChange = {
                    cantidadAgregar = it
                    mensajeError = ""
                },
                stockActual = stockActual,
                nuevoStock = nuevoStock,
                unidadMedida = unidadMedida,
                superaStockMaximo = superaStockMaximo
            )

            CardCostoReponerStock(
                costoUnitario = costoUnitarioEntrada,
                onCostoUnitarioChange = {
                    costoUnitarioEntrada = it
                    mensajeError = ""
                },
                cantidad = cantidad,
                costoMovimiento = costoMovimiento
            )

            CardDatosMovimientoStockMejorado(
                referencia = referencia,
                onReferenciaChange = { referencia = it },
                proveedor = proveedor,
                onProveedorChange = { proveedor = it },
                notas = notas,
                onNotasChange = { notas = it }
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

                        if (!costoValido) {
                            mensajeError = "Ingresa un costo unitario válido"
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

                            precioCompra = costoUnitario,
                            precioVenta = producto.precioVenta,

                            descripcion = producto.descripcion,
                            proveedor = proveedor.trim().ifBlank { producto.proveedor },
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

                        val movimientoEntrada = MovimientoInventarioEntity(
                            productoId = producto.id,
                            clienteId = null,
                            cotizacionId = null,
                            tipo = "Entrada",
                            cantidad = cantidad,
                            stockAnterior = stockActual,
                            stockNuevo = nuevoStock,
                            unidad = unidadMedida,
                            fecha = fechaActual,
                            hora = horaActual,
                            usuario = "",
                            referencia = referencia.trim(),
                            observaciones = buildString {
                                append("Reposición de stock")
                                if (proveedor.isNotBlank()) {
                                    append(". Proveedor: ${proveedor.trim()}")
                                }
                                append(". Costo unitario: ${formatoDineroReponer(costoUnitario)}")
                                append(". Costo total: ${formatoDineroReponer(costoMovimiento)}")
                                if (notas.isNotBlank()) {
                                    append(". Notas: ${notas.trim()}")
                                }
                            }
                        )

                        movimientoViewModel.insertarMovimiento(movimientoEntrada)

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

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CardProductoReponerStockMejorado(
    nombreProducto: String,
    codigoProducto: String,
    categoria: String,
    ubicacion: String,
    stockActual: Int,
    stockMinimo: Int,
    stockMaximo: Int,
    unidadMedida: String
) {
    val stockColor = when {
        stockActual <= 0 -> MaterialTheme.colorScheme.error
        stockActual <= stockMinimo -> MaterialTheme.arcshiftColors.warning
        else -> MaterialTheme.arcshiftColors.success
    }

    val stockTexto = when {
        stockActual <= 0 -> "Agotado"
        stockActual <= stockMinimo -> "Bajo stock"
        else -> "Disponible"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
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

                    Text(
                        text = "Código: $codigoProducto",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = categoria,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stockTexto,
                        style = MaterialTheme.typography.labelSmall,
                        color = stockColor,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$stockActual",
                        style = MaterialTheme.typography.headlineSmall,
                        color = stockColor,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = unidadMedida,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CajaDatoInventarioReponer(
                    titulo = "Mínimo",
                    valor = "$stockMinimo",
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f)
                )

                CajaDatoInventarioReponer(
                    titulo = "Máximo",
                    valor = if (stockMaximo > 0) "$stockMaximo" else "N/A",
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f)
                )

                CajaDatoInventarioReponer(
                    titulo = "Ubicación",
                    valor = ubicacion.ifBlank { "Sin dato" },
                    subtitulo = "almacén",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CardCantidadReponerMejorado(
    cantidadAgregar: String,
    onCantidadChange: (String) -> Unit,
    stockActual: Int,
    nuevoStock: Int,
    unidadMedida: String,
    superaStockMaximo: Boolean
) {
    FormularioCard(
        titulo = "Cantidad a reponer",
        icono = Icons.Default.AddCircleOutline
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = cantidadAgregar,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.all { it.isDigit() }) {
                        onCantidadChange(nuevoValor)
                    }
                },
                label = { Text("Cantidad *") },
                placeholder = { Text("Ej. 5") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CajaResumenStockReponer(
                    titulo = "Stock actual",
                    valor = stockActual.toString(),
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f)
                )

                CajaResumenStockReponer(
                    titulo = "Agregar",
                    valor = "+ ${cantidadAgregar.ifBlank { "0" }}",
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f),
                    colorPrincipal = MaterialTheme.colorScheme.primary
                )

                CajaResumenStockReponer(
                    titulo = "Nuevo stock",
                    valor = nuevoStock.toString(),
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f),
                    destacado = true
                )
            }

            if (superaStockMaximo) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.arcshiftColors.warningContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.arcshiftColors.warning,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "El nuevo stock supera el stock máximo registrado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.arcshiftColors.onWarningContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CardCostoReponerStock(
    costoUnitario: String,
    onCostoUnitarioChange: (String) -> Unit,
    cantidad: Int,
    costoMovimiento: Double
) {
    FormularioCard(
        titulo = "Costo de entrada",
        icono = Icons.Default.AttachMoney
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = costoUnitario,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.all { it.isDigit() || it == '.' || it == ',' }) {
                        onCostoUnitarioChange(nuevoValor)
                    }
                },
                label = { Text("Costo unitario *") },
                placeholder = { Text("0.00") },
                leadingIcon = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cantidad",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = cantidad.toString(),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Costo total de entrada",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = formatoDineroReponer(costoMovimiento),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardDatosMovimientoStockMejorado(
    referencia: String,
    onReferenciaChange: (String) -> Unit,
    proveedor: String,
    onProveedorChange: (String) -> Unit,
    notas: String,
    onNotasChange: (String) -> Unit
) {
    FormularioCard(
        titulo = "Datos del movimiento",
        icono = Icons.Default.Description
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = referencia,
                onValueChange = {
                    if (it.length <= 40) {
                        onReferenciaChange(it)
                    }
                },
                label = { Text("Referencia") },
                placeholder = { Text("Ej. factura, nota, OC-015") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("${referencia.length}/40")
                }
            )

            OutlinedTextField(
                value = proveedor,
                onValueChange = {
                    if (it.length <= 80) {
                        onProveedorChange(it)
                    }
                },
                label = { Text("Proveedor") },
                placeholder = { Text("Ej. Aceros del Norte") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("${proveedor.length}/80")
                }
            )

            OutlinedTextField(
                value = notas,
                onValueChange = {
                    if (it.length <= 150) {
                        onNotasChange(it)
                    }
                },
                label = { Text("Notas") },
                placeholder = { Text("Observaciones de la reposición") },
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
fun CajaDatoInventarioReponer(
    titulo: String,
    valor: String,
    subtitulo: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CajaResumenStockReponer(
    titulo: String,
    valor: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false,
    colorPrincipal: Color = MaterialTheme.colorScheme.onSurface
) {
    val fondo = if (destacado) {
        MaterialTheme.arcshiftColors.successContainer
    } else {
        MaterialTheme.colorScheme.background
    }

    val colorTexto = if (destacado) {
        MaterialTheme.arcshiftColors.success
    } else {
        colorPrincipal
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

fun formatoDineroReponer(valor: Double): String {
    return "$ ${"%.2f".format(Locale.US, valor)}"
}