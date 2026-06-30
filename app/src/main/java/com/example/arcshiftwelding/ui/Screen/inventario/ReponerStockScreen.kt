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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Person
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReponerStockScreen(
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

    var cantidadAgregar by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

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
                    text = "Reponer Stock",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0)
    ) { padding ->

        val producto = productoSeleccionado

        if (producto == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFFF5F6FA)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando producto...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            return@Scaffold
        }

        val stockActual = producto.stock
        val unidadMedida = producto.unidad
        val costoUnitario = producto.precioCompra

        val cantidad = cantidadAgregar.toIntOrNull() ?: 0
        val nuevoStock = stockActual + cantidad
        val costoMovimiento = cantidad * costoUnitario

        val puedeGuardar = cantidad > 0

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F6FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            CardProductoReponerStock(
                nombreProducto = producto.nombre,
                codigoProducto = producto.codigo,
                stockActual = stockActual,
                unidadMedida = unidadMedida
            )

            CardCantidadReponer(
                cantidadAgregar = cantidadAgregar,
                onCantidadChange = {
                    cantidadAgregar = it
                    mensajeError = ""
                },
                stockActual = stockActual,
                nuevoStock = nuevoStock,
                unidadMedida = unidadMedida
            )

            CardDatosMovimientoStock(
                referencia = referencia,
                onReferenciaChange = { referencia = it },
                proveedor = proveedor,
                onProveedorChange = { proveedor = it },
                notas = notas,
                onNotasChange = { notas = it }
            )

            CardResumenCostoStock(
                cantidad = cantidad,
                costoUnitario = costoUnitario,
                costoMovimiento = costoMovimiento
            )

            if (mensajeError.isNotBlank()) {
                Text(
                    text = mensajeError,
                    color = Color(0xFFDC2626),
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
                        if (cantidad <= 0) {
                            mensajeError = "Ingresa una cantidad válida"
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
                            usuario = "Admin",
                            referencia = referencia.ifBlank { "ENT-${producto.id}-${System.currentTimeMillis()}" },
                            observaciones = buildString {
                                if (proveedor.isNotBlank()) {
                                    append("Proveedor: ${proveedor.trim()}")
                                }

                                if (notas.isNotBlank()) {
                                    if (isNotBlank()) append(". ")
                                    append("Notas: ${notas.trim()}")
                                }

                                if (isBlank()) {
                                    append("Reposición de stock")
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
        }
    }
}
@Composable
fun CardProductoReponerStock(
    nombreProducto: String,
    codigoProducto: String,
    stockActual: Int,
    unidadMedida: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                        color = Color(0xFFEDEDED),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    tint = Color.Gray
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
                    color = Color.Gray
                )

                Text(
                    text = "Stock actual: $stockActual $unidadMedida",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1B7F3A),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CardCantidadReponer(
    cantidadAgregar: String,
    onCantidadChange: (String) -> Unit,
    stockActual: Int,
    nuevoStock: Int,
    unidadMedida: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF1B7F3A)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Cantidad a reponer",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cantidadAgregar,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.all { it.isDigit() }) {
                        onCantidadChange(nuevoValor)
                    }
                },
                label = {
                    Text("Cantidad *")
                },
                placeholder = {
                    Text("Ej. 5")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CajaResumenStock(
                    titulo = "Stock actual",
                    valor = stockActual.toString(),
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f)
                )

                CajaResumenStock(
                    titulo = "Agregar",
                    valor = "+ ${cantidadAgregar.ifBlank { "0" }}",
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f)
                )

                CajaResumenStock(
                    titulo = "Nuevo stock",
                    valor = nuevoStock.toString(),
                    subtitulo = unidadMedida,
                    modifier = Modifier.weight(1f),
                    destacado = true
                )
            }
        }
    }
}

@Composable
fun CajaResumenStock(
    titulo: String,
    valor: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (destacado) Color(0xFFE9F8EF) else Color(0xFFF7F7F7)
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
                color = Color.Gray
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (destacado) Color(0xFF1B7F3A) else Color(0xFF111827)
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CardDatosMovimientoStock(
    referencia: String,
    onReferenciaChange: (String) -> Unit,
    proveedor: String,
    onProveedorChange: (String) -> Unit,
    notas: String,
    onNotasChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Datos del movimiento",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            OutlinedTextField(
                value = referencia,
                onValueChange = onReferenciaChange,
                label = {
                    Text("Referencia")
                },
                placeholder = {
                    Text("Ej. OC-015, factura, nota")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = proveedor,
                onValueChange = onProveedorChange,
                label = {
                    Text("Proveedor")
                },
                placeholder = {
                    Text("Ej. Aceros del Norte")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

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
                    Text("Observaciones del movimiento")
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
fun CardResumenCostoStock(
    cantidad: Int,
    costoUnitario: Double,
    costoMovimiento: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Resumen de costo",
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
                Text("Cantidad")
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
                Text("Costo del movimiento")
                Text(
                    text = "$ ${"%.2f".format(costoMovimiento)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B7F3A)
                )
            }
        }
    }
}