package com.example.arcshiftwelding.ui.Screen.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.example.arcshiftwelding.navigation.AppRoutes
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
import kotlin.math.abs
import com.example.arcshiftwelding.utils.guardarImagenProductoEnInterno
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    navController: NavController,
    productoId: Int
) {
    val context = LocalContext.current

    val database = remember {
        ArcshiftWeldingDatabase.getDatabase(context)
    }

    val repository = remember {
        ProductoRepository(database.productoDao())
    }

    val productoViewModel: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(repository)
    )
    val movimientoRepository = remember {
        MovimientoInventarioRepository(database.movimientoInventarioDao())
    }

    val movimientoViewModel: MovimientoInventarioViewModel = viewModel(
        factory = MovimientoInventarioViewModelFactory(movimientoRepository)
    )

    val productoSeleccionado by productoViewModel.productoSeleccionado.collectAsState()

    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val seleccionarImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var stockActual by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var stockMaximo by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("En Stock") }

    var costoUnitario by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var permitirStockNegativo by remember { mutableStateOf(false) }
    var productoActivo by remember { mutableStateOf(true) }

    var mensajeError by remember { mutableStateOf("") }
    var datosCargados by remember { mutableStateOf(false) }

    LaunchedEffect(productoId) {
        productoViewModel.cargarProductoPorId(productoId)
    }

    LaunchedEffect(productoSeleccionado) {
        val producto = productoSeleccionado

        if (producto != null && !datosCargados) {
            nombre = producto.nombre
            categoria = producto.categoria
            unidad = producto.unidad
            codigo = producto.codigo
            ubicacion = producto.ubicacion
            descripcion = producto.descripcion

            stockActual = producto.stock.toString()
            stockMinimo = producto.stockMinimo.toString()
            stockMaximo = producto.stockMaximo.toString()
            estado = producto.estado

            costoUnitario = producto.precioCompra.toString()
            proveedor = producto.proveedor
            notas = producto.notas

            permitirStockNegativo = producto.permitirStockNegativo
            productoActivo = producto.activo

            imagenUri = if (producto.imagenUri.isNotBlank()) {
                Uri.fromFile(File(producto.imagenUri))
            } else {
                null
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
                    text = "Editar Producto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

            }
        },
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        if (productoSeleccionado == null) {
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F6FA))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormularioCard(
                titulo = "Foto del producto",
                icono = Icons.Default.Inventory2
            ) {
                SelectorImagenProductoEditar(
                    imagenUri = imagenUri,
                    onSeleccionarImagen = {
                        seleccionarImagenLauncher.launch("image/*")
                    }
                )
            }
            FormularioCard(
                titulo = "Información general",
                icono = Icons.Default.Info
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del producto *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    MenuDesplegable(
                        label = "Categoría *",
                        placeholder = "Seleccionar categoría",
                        opciones = listOf("Materiales", "Herramientas", "Consumibles", "Seguridad"),
                        valorSeleccionado = categoria,
                        onSeleccionar = { categoria = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    MenuDesplegable(
                        label = "Unidad de medida *",
                        placeholder = "Seleccionar unidad",
                        opciones = listOf("Piezas", "Metros", "Kg", "Cajas", "Pares", "Rollos", "Cilindros"),
                        valorSeleccionado = unidad,
                        onSeleccionar = { unidad = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Código / SKU *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ubicacion,
                        onValueChange = { ubicacion = it },
                        label = { Text("Ubicación *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            if (it.length <= 200) descripcion = it
                        },
                        label = { Text("Descripción") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        supportingText = {
                            Text("${descripcion.length}/200")
                        }
                    )
                }
            }

            FormularioCard(
                titulo = "Inventario",
                icono = Icons.Default.Inventory2
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = stockActual,
                        onValueChange = { stockActual = it },
                        label = { Text("Stock actual *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = stockMinimo,
                        onValueChange = { stockMinimo = it },
                        label = { Text("Stock mínimo *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = stockMaximo,
                        onValueChange = { stockMaximo = it },
                        label = { Text("Stock máximo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    MenuDesplegable(
                        label = "Estado *",
                        placeholder = "Seleccionar estado",
                        opciones = listOf("En Stock", "Bajo Stock", "Agotado"),
                        valorSeleccionado = estado,
                        onSeleccionar = { estado = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            FormularioCard(
                titulo = "Costos",
                icono = Icons.Default.AttachMoney
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = costoUnitario,
                        onValueChange = { costoUnitario = it },
                        label = { Text("Costo unitario *") },
                        leadingIcon = { Text("$") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    val total = calcularTotalEditar(
                        stockActual = stockActual,
                        costoUnitario = costoUnitario
                    )

                    OutlinedTextField(
                        value = total,
                        onValueChange = {},
                        label = { Text("Costo total en inventario") },
                        leadingIcon = { Text("$") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        singleLine = true
                    )
                }
            }

            FormularioCard(
                titulo = "Información adicional",
                icono = Icons.Default.LocalOffer
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MenuDesplegable(
                        label = "Proveedor",
                        placeholder = "Seleccionar proveedor",
                        opciones = listOf("Proveedor 1", "Proveedor 2", "Proveedor 3"),
                        valorSeleccionado = proveedor,
                        onSeleccionar = { proveedor = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notas,
                        onValueChange = {
                            if (it.length <= 150) notas = it
                        },
                        label = { Text("Notas") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            Text("${notas.length}/150")
                        }
                    )
                }
            }

            FormularioCard(
                titulo = "Opciones",
                icono = Icons.Default.Settings
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = permitirStockNegativo,
                            onCheckedChange = { permitirStockNegativo = it }
                        )

                        Column {
                            Text("Permitir stock negativo")
                            Text(
                                text = "Permite que el stock actual pueda ser menor a cero.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = productoActivo,
                            onCheckedChange = { productoActivo = it }
                        )

                        Column {
                            Text("Producto activo")
                            Text(
                                text = "El producto estará disponible en el inventario.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
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
                        val productoOriginal = productoSeleccionado

                        if (productoOriginal == null) {
                            mensajeError = "No se pudo cargar el producto"
                            return@Button
                        }

                        if (nombre.isBlank()) {
                            mensajeError = "El nombre del producto es obligatorio"
                            return@Button
                        }

                        if (categoria.isBlank()) {
                            mensajeError = "Selecciona una categoría"
                            return@Button
                        }

                        if (unidad.isBlank()) {
                            mensajeError = "Selecciona una unidad de medida"
                            return@Button
                        }

                        if (codigo.isBlank()) {
                            mensajeError = "El código del producto es obligatorio"
                            return@Button
                        }

                        if (ubicacion.isBlank()) {
                            mensajeError = "La ubicación es obligatoria"
                            return@Button
                        }

                        val stock = stockActual.toIntOrNull()
                        if (stock == null) {
                            mensajeError = "El stock actual debe ser un número válido"
                            return@Button
                        }

                        val minimo = stockMinimo.toIntOrNull()
                        if (minimo == null) {
                            mensajeError = "El stock mínimo debe ser un número válido"
                            return@Button
                        }

                        val maximo = stockMaximo.toIntOrNull() ?: 0

                        val costo = costoUnitario.toDoubleOrNull()
                        if (costo == null) {
                            mensajeError = "El costo unitario debe ser un número válido"
                            return@Button
                        }

                        val estadoCalculado = when {
                            stock <= 0 -> "Agotado"
                            stock <= minimo -> "Bajo Stock"
                            else -> "En Stock"
                        }

                        val rutaImagenInterna = when {
                            imagenUri == null -> {
                                ""
                            }

                            imagenUri.toString().startsWith("/") -> {
                                imagenUri.toString()
                            }

                            imagenUri.toString().startsWith("content://") -> {
                                guardarImagenProductoEnInterno(
                                    context = context,
                                    imagenUri = imagenUri
                                )
                            }

                            else -> {
                                productoOriginal.imagenUri
                            }
                        }

                        val productoEditado = ProductoEntity(
                            id = productoOriginal.id,

                            nombre = nombre.trim(),
                            categoria = categoria,
                            codigo = codigo.trim(),
                            ubicacion = ubicacion.trim(),

                            stock = stock,
                            unidad = unidad,
                            stockMinimo = minimo,
                            stockMaximo = maximo,

                            estado = estadoCalculado,

                            precioCompra = costo,
                            precioVenta = productoOriginal.precioVenta,

                            descripcion = descripcion.trim(),
                            proveedor = proveedor,
                            notas = notas.trim(),

                            imagenUri = rutaImagenInterna,

                            permitirStockNegativo = permitirStockNegativo,
                            activo = productoActivo,

                            fechaRegistro = productoOriginal.fechaRegistro
                        )

                        val stockAnterior = productoOriginal.stock
                        val stockNuevo = stock
                        val diferenciaStock = stockNuevo - stockAnterior

                        productoViewModel.actualizarProducto(productoEditado)

                        if (diferenciaStock != 0) {
                            val fechaActual = LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                            val horaActual = LocalTime.now()
                                .format(DateTimeFormatter.ofPattern("HH:mm"))

                            val movimientoAjuste = MovimientoInventarioEntity(
                                productoId = productoOriginal.id,
                                tipo = "Ajuste",
                                cantidad = abs(diferenciaStock),
                                stockAnterior = stockAnterior,
                                stockNuevo = stockNuevo,
                                unidad = unidad,
                                fecha = fechaActual,
                                hora = horaActual,
                                usuario = "Admin",
                                referencia = "AJ-${productoOriginal.id}",
                                observaciones = if (diferenciaStock > 0) {
                                    "Ajuste manual: aumento de stock desde edición de producto"
                                } else {
                                    "Ajuste manual: disminución de stock desde edición de producto"
                                }
                            )

                            movimientoViewModel.insertarMovimiento(movimientoAjuste)
                        }

                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Guardar Cambios")
                }
            }

        }
    }
}

fun calcularTotalEditar(
    stockActual: String,
    costoUnitario: String
): String {
    val stock = stockActual.toDoubleOrNull() ?: 0.0
    val costo = costoUnitario.toDoubleOrNull() ?: 0.0
    val total = stock * costo

    return "%.2f".format(total)
}

@Composable
fun SelectorImagenProductoEditar(
    imagenUri: Uri?,
    onSeleccionarImagen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                color = Color(0xFFF2F2F2),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                onSeleccionarImagen()
            },
        contentAlignment = Alignment.Center
    ) {
        if (imagenUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imagenUri),
                contentDescription = "Imagen del producto",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cambiar foto del producto",
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "JPG, PNG",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}