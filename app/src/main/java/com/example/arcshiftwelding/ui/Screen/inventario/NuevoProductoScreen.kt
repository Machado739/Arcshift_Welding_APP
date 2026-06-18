package com.example.arcshiftwelding.ui.Screen.inventario

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.arcshiftwelding.navigation.BottomNavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
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
import com.example.arcshiftwelding.utils.guardarImagenProductoEnInterno

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun NuevoProductoScreen(
    navController: NavController
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

    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var categoria by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    var stockInicial by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var stockMaximo by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("En Stock") }

    var costoUnitario by remember { mutableStateOf("") }

    var proveedor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var permitirStockNegativo by remember { mutableStateOf(true) }
    var productoActivo by remember { mutableStateOf(true) }

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
                    text = "Nuevo Producto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
        },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeccionInformacionGeneral(
                nombre = nombre,
                onNombreChange = { nombre = it },
                codigo = codigo,
                onCodigoChange = { codigo = it },
                ubicacion = ubicacion,
                onUbicacionChange = { ubicacion = it },
                descripcion = descripcion,
                onDescripcionChange = { descripcion = it },
                categoria = categoria,
                onCategoriaChange = { categoria = it },
                unidad = unidad,
                onUnidadChange = { unidad = it },
                imagenUri = imagenUri,
                onImagenChange = { imagenUri = it }
            )

            SeccionInventario(
                stockInicial = stockInicial,
                onStockInicialChange = { stockInicial = it },
                stockMinimo = stockMinimo,
                onStockMinimoChange = { stockMinimo = it },
                stockMaximo = stockMaximo,
                onStockMaximoChange = { stockMaximo = it },
                estado = estado,
                onEstadoChange = { estado = it }
            )

            SeccionCostos(
                costoUnitario = costoUnitario,
                onCostoUnitarioChange = { costoUnitario = it },
                stockInicial = stockInicial
            )

            SeccionInformacionAdicional(
                proveedor = proveedor,
                onProveedorChange = { proveedor = it },
                notas = notas,
                onNotasChange = { notas = it }
            )

            SeccionOpciones(
                permitirStockNegativo = permitirStockNegativo,
                onPermitirStockNegativoChange = { permitirStockNegativo = it },
                productoActivo = productoActivo,
                onProductoActivoChange = { productoActivo = it }
            )

            if (mensajeError.isNotBlank()) {
                Text(
                    text = mensajeError,
                    color = Color(0xFFDC2626),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            BotonesFormulario(
                onCancelar = {
                    navController.popBackStack()
                },
                onGuardar = {
                    if (nombre.isBlank()) {
                        mensajeError = "El nombre del producto es obligatorio"
                        return@BotonesFormulario
                    }

                    if (categoria.isBlank()) {
                        mensajeError = "Selecciona una categoría"
                        return@BotonesFormulario
                    }

                    if (unidad.isBlank()) {
                        mensajeError = "Selecciona una unidad de medida"
                        return@BotonesFormulario
                    }

                    if (codigo.isBlank()) {
                        mensajeError = "El código del producto es obligatorio"
                        return@BotonesFormulario
                    }

                    if (ubicacion.isBlank()) {
                        mensajeError = "La ubicación es obligatoria"
                        return@BotonesFormulario
                    }

                    val stock = stockInicial.toIntOrNull()
                    if (stock == null) {
                        mensajeError = "El stock inicial debe ser un número válido"
                        return@BotonesFormulario
                    }

                    val minimo = stockMinimo.toIntOrNull()
                    if (minimo == null) {
                        mensajeError = "El stock mínimo debe ser un número válido"
                        return@BotonesFormulario
                    }

                    val costo = costoUnitario.toDoubleOrNull()
                    if (costo == null) {
                        mensajeError = "El costo unitario debe ser un número válido"
                        return@BotonesFormulario
                    }

                    val maximo = stockMaximo.toIntOrNull() ?: 0

                    val estadoCalculado = when {
                        stock <= 0 -> "Agotado"
                        stock <= minimo -> "Bajo Stock"
                        else -> "En Stock"
                    }

                    val rutaImagenInterna = guardarImagenProductoEnInterno(
                        context = context,
                        imagenUri = imagenUri
                    )

                    val producto = ProductoEntity(
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
                        precioVenta = 0.0,

                        descripcion = descripcion.trim(),
                        proveedor = proveedor,
                        notas = notas.trim(),

                        imagenUri = rutaImagenInterna,

                        permitirStockNegativo = permitirStockNegativo,
                        activo = productoActivo,

                        fechaRegistro = "18/06/2026"
                    )

                    productoViewModel.insertarProducto(producto) { productoIdGenerado ->

                        val movimientoInicial = MovimientoInventarioEntity(
                            productoId = productoIdGenerado.toInt(),
                            tipo = "Registro inicial",
                            cantidad = stock,
                            stockAnterior = 0,
                            stockNuevo = stock,
                            unidad = unidad,
                            fecha = "18/06/2026",
                            hora = "00:00",
                            usuario = "Admin",
                            referencia = "REG-INICIAL",
                            observaciones = "Registro inicial del producto"
                        )

                        movimientoViewModel.insertarMovimiento(movimientoInicial)

                        navController.popBackStack()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun FormularioCard(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable () -> Unit
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
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider()

            Spacer(modifier = Modifier.height(12.dp))

            contenido()
        }
    }
}@Composable
fun SeccionInformacionGeneral(
    nombre: String,
    onNombreChange: (String) -> Unit,
    codigo: String,
    onCodigoChange: (String) -> Unit,
    ubicacion: String,
    onUbicacionChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    categoria: String,
    onCategoriaChange: (String) -> Unit,
    unidad: String,
    onUnidadChange: (String) -> Unit,
    imagenUri: Uri?,
    onImagenChange: (Uri?) -> Unit
) {
    val seleccionarImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagenChange(uri)
    }

    FormularioCard(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        BoxWithConstraints {
            val esPantallaChica = maxWidth < 600.dp

            if (esPantallaChica) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(
                                color = Color(0xFFF2F2F2),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        SelectorImagenProductoNuevo(
                            imagenUri = imagenUri,
                            onSeleccionarImagen = {
                                seleccionarImagenLauncher.launch("image/*")
                            }
                        )
                    }

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = onNombreChange,
                        label = { Text("Nombre del producto *") },
                        placeholder = { Text("Ej. PTR 2 x2 Cal. 14") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    MenuDesplegable(
                        label = "Categoría *",
                        placeholder = "Seleccionar categoría",
                        opciones = listOf("Materiales", "Herramientas", "Consumibles", "Seguridad"),
                        valorSeleccionado = categoria,
                        onSeleccionar = onCategoriaChange,
                        modifier = Modifier.fillMaxWidth()
                    )

                    MenuDesplegable(
                        label = "Unidad de medida *",
                        placeholder = "Seleccionar unidad",
                        opciones = listOf("Piezas", "Metros", "Kg", "Cajas", "Pares", "Rollos", "Cilindros"),
                        valorSeleccionado = unidad,
                        onSeleccionar = onUnidadChange,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = codigo,
                        onValueChange = onCodigoChange,
                        label = { Text("Código / SKU *") },
                        placeholder = { Text("Ej. MAT-001") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ubicacion,
                        onValueChange = onUbicacionChange,
                        label = { Text("Ubicación *") },
                        placeholder = { Text("Ej. Almacén A") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            if (it.length <= 200) onDescripcionChange(it)
                        },
                        label = { Text("Descripción") },
                        placeholder = {
                            Text("Describe el producto, características, usos, etc.")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        supportingText = {
                            Text("${descripcion.length}/200")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SeccionInventario(
    stockInicial: String,
    onStockInicialChange: (String) -> Unit,
    stockMinimo: String,
    onStockMinimoChange: (String) -> Unit,
    stockMaximo: String,
    onStockMaximoChange: (String) -> Unit,
    estado: String,
    onEstadoChange: (String) -> Unit
) {
    FormularioCard(
        titulo = "Inventario",
        icono = Icons.Default.Inventory2
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = stockInicial,
                onValueChange = onStockInicialChange,
                label = { Text("Stock inicial *") },
                placeholder = { Text("Ej. 10") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = stockMinimo,
                onValueChange = onStockMinimoChange,
                label = { Text("Stock mínimo *") },
                placeholder = { Text("Ej. 5") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = stockMaximo,
                onValueChange = onStockMaximoChange,
                label = { Text("Stock máximo") },
                placeholder = { Text("Ej. 100") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            MenuDesplegable(
                label = "Estado inicial *",
                placeholder = "Estado",
                opciones = listOf("En Stock", "Bajo Stock", "Agotado"),
                valorSeleccionado = estado,
                onSeleccionar = onEstadoChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SeccionCostos(
    costoUnitario: String,
    onCostoUnitarioChange: (String) -> Unit,
    stockInicial: String
) {
    val stock = stockInicial.toIntOrNull() ?: 0
    val costo = costoUnitario.toDoubleOrNull() ?: 0.0
    val costoTotal = stock * costo

    FormularioCard(
        titulo = "Costos",
        icono = Icons.Default.AttachMoney
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = costoUnitario,
                onValueChange = onCostoUnitarioChange,
                label = { Text("Costo unitario *") },
                placeholder = { Text("0.00") },
                leadingIcon = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = costoTotal.toString(),
                onValueChange = {},
                label = { Text("Costo total inicial") },
                leadingIcon = { Text("$") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true
            )
        }
    }
}

@Composable
fun SeccionInformacionAdicional(
    proveedor: String,
    onProveedorChange: (String) -> Unit,
    notas: String,
    onNotasChange: (String) -> Unit
) {
    FormularioCard(
        titulo = "Información adicional",
        icono = Icons.Default.LocalOffer
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuDesplegable(
                label = "Proveedor",
                placeholder = "Seleccionar proveedor",
                opciones = listOf("Proveedor 1", "Proveedor 2", "Proveedor 3"),
                valorSeleccionado = proveedor,
                onSeleccionar = onProveedorChange,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = {
                    if (it.length <= 150) onNotasChange(it)
                },
                label = { Text("Notas") },
                placeholder = { Text("Notas adicionales opcional") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text("${notas.length}/150")
                }
            )
        }
    }
}

@Composable
fun SeccionOpciones(
    permitirStockNegativo: Boolean,
    onPermitirStockNegativoChange: (Boolean) -> Unit,
    productoActivo: Boolean,
    onProductoActivoChange: (Boolean) -> Unit
) {
    FormularioCard(
        titulo = "Opciones",
        icono = Icons.Default.Settings
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = permitirStockNegativo,
                onCheckedChange = onPermitirStockNegativoChange
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
                onCheckedChange = onProductoActivoChange
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



@Composable
fun BotonesFormulario(
    onCancelar: () -> Unit,
    onGuardar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
        ) {
            Text("Cancelar")
        }

        Button(
            onClick = onGuardar,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Guardar Producto")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDesplegable(
    label: String,
    placeholder: String,
    opciones: List<String>,
    valorSeleccionado: String,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        OutlinedTextField(
            value = valorSeleccionado,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionar(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun SelectorImagenProductoNuevo(
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