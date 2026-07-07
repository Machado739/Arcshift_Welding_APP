package com.example.arcshiftwelding.ui.Screen.inventario

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.input.KeyboardType
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private val ArcPrimary = Color(0xFF2563EB)
private val ArcPrimaryLight = Color(0xFFDBEAFE)
private val ArcBackground = Color(0xFFF8FAFC)
private val ArcSurface = Color(0xFFFFFFFF)
private val ArcTextPrimary = Color(0xFF0F172A)
private val ArcTextSecondary = Color(0xFF64748B)
private val ArcBorder = Color(0xFFE2E8F0)
private val ArcError = Color(0xFFDC2626)
private val ArcErrorLight = Color(0xFFFEE2E2)
private val ArcSuccess = Color(0xFF16A34A)
private val ArcWarning = Color(0xFFF59E0B)

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

    val codigoGenerado by productoViewModel.codigoSiguiente.collectAsState()

    val movimientoRepository = remember {
        MovimientoInventarioRepository(database.movimientoInventarioDao())
    }

    val movimientoViewModel: MovimientoInventarioViewModel = viewModel(
        factory = MovimientoInventarioViewModelFactory(movimientoRepository)
    )

    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var categoria by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    var stockInicial by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var stockMaximo by remember { mutableStateOf("") }

    var costoUnitario by remember { mutableStateOf("") }

    var proveedor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var permitirStockNegativo by remember { mutableStateOf(true) }
    var productoActivo by remember { mutableStateOf(true) }

    var mensajeError by remember { mutableStateOf("") }

    val fechaActual = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    val horaActual = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    fun guardarProducto() {
        mensajeError = ""

        if (nombre.isBlank()) {
            mensajeError = "Ingresa el nombre del producto"
            return
        }

        if (categoria.isBlank()) {
            mensajeError = "Selecciona una categoría"
            return
        }

        if (unidad.isBlank()) {
            mensajeError = "Selecciona una unidad de medida"
            return
        }

        if (codigoGenerado.isBlank()) {
            mensajeError = "El código del producto es obligatorio"
            return
        }

        if (ubicacion.isBlank()) {
            mensajeError = "La ubicación es obligatoria"
            return
        }

        val stock = stockInicial.toIntOrNull()
        if (stock == null) {
            mensajeError = "El stock inicial debe ser un número válido"
            return
        }

        val minimo = stockMinimo.toIntOrNull()
        if (minimo == null) {
            mensajeError = "El stock mínimo debe ser un número válido"
            return
        }

        val costo = costoUnitario.toDoubleOrNull()
        if (costo == null) {
            mensajeError = "El costo unitario debe ser un número válido"
            return
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
            codigo = codigoGenerado,
            ubicacion = ubicacion.trim(),

            stock = stock,
            unidad = unidad,
            stockMinimo = minimo,
            stockMaximo = maximo,

            estado = estadoCalculado,

            precioCompra = costo,
            precioVenta = 0.0,

            descripcion = descripcion.trim(),
            proveedor = proveedor.trim(),
            notas = notas.trim(),

            imagenUri = rutaImagenInterna,

            permitirStockNegativo = permitirStockNegativo,
            activo = productoActivo,

            fechaRegistro = fechaActual
        )

        productoViewModel.insertarProductoConCodigo(producto) { productoIdGenerado ->

            val movimientoInicial = MovimientoInventarioEntity(
                productoId = productoIdGenerado.toInt(),
                clienteId = null,
                cotizacionId = null,
                tipo = "Registro inicial",
                cantidad = stock,
                stockAnterior = 0,
                stockNuevo = stock,
                unidad = unidad,
                fecha = fechaActual,
                hora = horaActual,
                usuario = "Admin",
                referencia = "REG-INICIAL",
                observaciones = "Registro inicial del producto"
            )

            movimientoViewModel.insertarMovimiento(movimientoInicial)

            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            HeaderNuevoProducto(
                onBack = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = {
            BotonesFormulario(
                onCancelar = {
                    navController.popBackStack()
                },
                onGuardar = {
                    guardarProducto()
                }
            )
        },
        containerColor = ArcBackground,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ResumenNuevoProducto(
                categoria = categoria,
                codigo = codigoGenerado,
                stock = stockInicial,
                unidad = unidad
            )

            SeccionInformacionGeneral(
                nombre = nombre,
                onNombreChange = { nombre = it },
                codigo = codigoGenerado,
                onCodigoChange = {},
                ubicacion = ubicacion,
                onUbicacionChange = { ubicacion = it },
                descripcion = descripcion,
                onDescripcionChange = { descripcion = it },
                categoria = categoria,
                onCategoriaChange = {
                    categoria = it
                    productoViewModel.generarCodigoPorCategoria(it)
                },
                unidad = unidad,
                onUnidadChange = { unidad = it },
                imagenUri = imagenUri,
                onImagenChange = { imagenUri = it }
            )

            SeccionInventario(
                stockInicial = stockInicial,
                onStockInicialChange = {
                    stockInicial = it.filter { caracter -> caracter.isDigit() }
                },
                stockMinimo = stockMinimo,
                onStockMinimoChange = {
                    stockMinimo = it.filter { caracter -> caracter.isDigit() }
                },
                stockMaximo = stockMaximo,
                onStockMaximoChange = {
                    stockMaximo = it.filter { caracter -> caracter.isDigit() }
                }
            )

            SeccionCostos(
                costoUnitario = costoUnitario,
                onCostoUnitarioChange = {
                    if (it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        costoUnitario = it
                    }
                },
                stockInicial = stockInicial
            )

            SeccionInformacionAdicional(
                proveedor = proveedor,
                onProveedorChange = { proveedor = it },
                notas = notas,
                onNotasChange = { notas = it }
            )

            if (mensajeError.isNotBlank()) {
                MensajeErrorProducto(mensaje = mensajeError)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun HeaderNuevoProducto(
    onBack: () -> Unit
) {
    Surface(
        color = ArcSurface,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp, end = 16.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = ArcTextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(ArcPrimaryLight, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    tint = ArcPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Nuevo producto",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ArcTextPrimary
                )

                Text(
                    text = "Registra un artículo para inventario",
                    style = MaterialTheme.typography.bodySmall,
                    color = ArcTextSecondary
                )
            }
        }
    }
}

@Composable
fun ResumenNuevoProducto(
    categoria: String,
    codigo: String,
    stock: String,
    unidad: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = ArcPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Vista previa del registro",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DatoResumenProducto(
                    titulo = "Categoría",
                    valor = categoria.ifBlank { "Pendiente" },
                    modifier = Modifier.weight(1f)
                )

                DatoResumenProducto(
                    titulo = "Código",
                    valor = codigo.ifBlank { "Sin generar" },
                    modifier = Modifier.weight(1f)
                )
            }

            DatoResumenProducto(
                titulo = "Stock inicial",
                valor = if (stock.isBlank()) "Pendiente" else "$stock ${unidad.ifBlank { "" }}",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DatoResumenProducto(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.14f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.80f)
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
fun FormularioCard(
    titulo: String,
    icono: ImageVector,
    descripcion: String? = null,
    contenido: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = ArcSurface
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
                        .size(38.dp)
                        .background(ArcPrimaryLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = ArcPrimary,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ArcTextPrimary
                    )

                    if (!descripcion.isNullOrBlank()) {
                        Text(
                            text = descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = ArcTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = ArcBorder)

            Spacer(modifier = Modifier.height(14.dp))

            contenido()
        }
    }
}

@Composable
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
        icono = Icons.Default.Info,
        descripcion = "Datos principales del producto"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SelectorImagenProductoNuevo(
                imagenUri = imagenUri,
                onSeleccionarImagen = {
                    seleccionarImagenLauncher.launch("image/*")
                }
            )

            CampoTextoProducto(
                valor = nombre,
                onValorChange = onNombreChange,
                titulo = "Nombre del producto",
                placeholder = "Ej. PTR 2 x 2 Cal. 14",
                requerido = true,
                icono = Icons.Default.Inventory2
            )

            MenuDesplegable(
                label = "Categoría",
                placeholder = "Seleccionar categoría",
                opciones = listOf(
                    "Materiales",
                    "Herramientas",
                    "Consumibles",
                    "Seguridad",
                    "Equipos",
                    "Otros"
                ),
                valorSeleccionado = categoria,
                onSeleccionar = onCategoriaChange,
                requerido = true,
                modifier = Modifier.fillMaxWidth()
            )

            CampoTextoProducto(
                valor = codigo,
                onValorChange = onCodigoChange,
                titulo = "Código automático",
                placeholder = "Selecciona una categoría",
                requerido = true,
                readOnly = true,
                icono = Icons.Default.QrCode
            )

            MenuDesplegable(
                label = "Unidad de medida",
                placeholder = "Seleccionar unidad",
                opciones = listOf(
                    "Piezas",
                    "Metros",
                    "Kg",
                    "Cajas",
                    "Pares",
                    "Rollos",
                    "Cilindros",
                    "Hojas",
                    "Litros",
                    "Otros"
                ),
                valorSeleccionado = unidad,
                onSeleccionar = onUnidadChange,
                requerido = true,
                modifier = Modifier.fillMaxWidth()
            )

            CampoTextoProducto(
                valor = ubicacion,
                onValorChange = onUbicacionChange,
                titulo = "Ubicación",
                placeholder = "Ej. Almacén A",
                requerido = true,
                icono = Icons.Default.LocationOn
            )

            CampoTextoProducto(
                valor = descripcion,
                onValorChange = {
                    if (it.length <= 200) onDescripcionChange(it)
                },
                titulo = "Descripción",
                placeholder = "Características, medidas, uso o detalles del producto",
                requerido = false,
                icono = Icons.Default.Description,
                maxLines = 4,
                minHeight = 110.dp,
                limite = 200
            )
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
    onStockMaximoChange: (String) -> Unit
) {
    val stock = stockInicial.toIntOrNull() ?: 0
    val minimo = stockMinimo.toIntOrNull() ?: 0

    val estadoCalculado = when {
        stockInicial.isBlank() || stockMinimo.isBlank() -> "Pendiente"
        stock <= 0 -> "Agotado"
        stock <= minimo -> "Bajo Stock"
        else -> "En Stock"
    }

    val colorEstado = when (estadoCalculado) {
        "En Stock" -> ArcSuccess
        "Bajo Stock" -> ArcWarning
        "Agotado" -> ArcError
        else -> ArcTextSecondary
    }

    FormularioCard(
        titulo = "Inventario",
        icono = Icons.Default.Warehouse,
        descripcion = "Cantidad inicial y niveles de control"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoTextoProducto(
                valor = stockInicial,
                onValorChange = onStockInicialChange,
                titulo = "Stock inicial",
                placeholder = "Ej. 10",
                requerido = true,
                icono = Icons.Default.AddBox,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CampoTextoProducto(
                    valor = stockMinimo,
                    onValorChange = onStockMinimoChange,
                    titulo = "Mínimo",
                    placeholder = "Ej. 5",
                    requerido = true,
                    icono = Icons.Default.Warning,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                CampoTextoProducto(
                    valor = stockMaximo,
                    onValorChange = onStockMaximoChange,
                    titulo = "Máximo",
                    placeholder = "Ej. 100",
                    requerido = false,
                    icono = Icons.Default.Inventory,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colorEstado.copy(alpha = 0.10f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoGraph,
                        contentDescription = null,
                        tint = colorEstado,
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Estado calculado",
                            style = MaterialTheme.typography.labelMedium,
                            color = ArcTextSecondary
                        )

                        Text(
                            text = estadoCalculado,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorEstado
                        )
                    }
                }
            }
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
    val formato = DecimalFormat("#,##0.00")

    FormularioCard(
        titulo = "Costos",
        icono = Icons.Default.AttachMoney,
        descripcion = "Costo inicial del inventario registrado"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoTextoProducto(
                valor = costoUnitario,
                onValorChange = onCostoUnitarioChange,
                titulo = "Costo unitario",
                placeholder = "0.00",
                requerido = true,
                icono = Icons.Default.Payments,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF1F5F9),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ArcBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(ArcPrimaryLight, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = ArcPrimary,
                            modifier = Modifier.size(21.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Costo total inicial",
                            style = MaterialTheme.typography.labelMedium,
                            color = ArcTextSecondary
                        )

                        Text(
                            text = "$${formato.format(costoTotal)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ArcTextPrimary
                        )
                    }
                }
            }
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
        icono = Icons.Default.LocalOffer,
        descripcion = "Datos opcionales para control interno"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CampoTextoProducto(
                valor = proveedor,
                onValorChange = {
                    if (it.length <= 80) onProveedorChange(it)
                },
                titulo = "Proveedor",
                placeholder = "Ej. Aceros del Norte",
                requerido = false,
                icono = Icons.Default.Store,
                limite = 80
            )

            CampoTextoProducto(
                valor = notas,
                onValorChange = {
                    if (it.length <= 150) onNotasChange(it)
                },
                titulo = "Notas",
                placeholder = "Notas adicionales del producto",
                requerido = false,
                icono = Icons.Default.Notes,
                maxLines = 4,
                minHeight = 100.dp,
                limite = 150
            )
        }
    }
}

@Composable
fun CampoTextoProducto(
    valor: String,
    onValorChange: (String) -> Unit,
    titulo: String,
    placeholder: String,
    requerido: Boolean,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1,
    minHeight: androidx.compose.ui.unit.Dp = 56.dp,
    limite: Int? = null
) {
    Column(
        modifier = modifier
    ) {
        Row {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ArcTextPrimary
            )

            if (requerido) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ArcError
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = ArcTextSecondary
                )
            },
            leadingIcon = if (icono != null) {
                {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = ArcTextSecondary
                    )
                }
            } else null,
            trailingIcon = if (readOnly) {
                {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ArcTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else null,
            singleLine = maxLines == 1,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ArcPrimary,
                unfocusedBorderColor = ArcBorder,
                focusedContainerColor = ArcSurface,
                unfocusedContainerColor = ArcSurface,
                cursorColor = ArcPrimary
            )
        )

        if (limite != null) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${valor.length}/$limite",
                style = MaterialTheme.typography.labelSmall,
                color = ArcTextSecondary,
                modifier = Modifier.align(Alignment.End)
            )
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
    modifier: Modifier = Modifier,
    requerido: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val rotacionFlecha by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotacionFlechaDropdown"
    )

    Column(
        modifier = modifier
    ) {
        Row {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ArcTextPrimary
            )

            if (requerido) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ArcError
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                value = valorSeleccionado,
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodySmall,
                        color = ArcTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = ArcTextSecondary,
                        modifier = Modifier.rotate(rotacionFlecha)
                    )
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArcPrimary,
                    unfocusedBorderColor = ArcBorder,
                    focusedContainerColor = ArcSurface,
                    unfocusedContainerColor = ArcSurface,
                    cursorColor = ArcPrimary
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = opcion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onSeleccionar(opcion)
                            expanded = false
                        }
                    )
                }
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
            .height(170.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF1F5F9))
            .border(
                width = 1.dp,
                color = ArcBorder,
                shape = RoundedCornerShape(18.dp)
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

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                color = ArcPrimary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Cambiar",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(ArcPrimaryLight, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = ArcPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Agregar foto del producto",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ArcTextPrimary
                )

                Text(
                    text = "JPG o PNG",
                    style = MaterialTheme.typography.bodySmall,
                    color = ArcTextSecondary
                )
            }
        }
    }
}

@Composable
fun MensajeErrorProducto(
    mensaje: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = ArcErrorLight
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = ArcError
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = ArcError
            )
        }
    }
}

@Composable
fun BotonesFormulario(
    onCancelar: () -> Unit,
    onGuardar: () -> Unit
) {
    Surface(
        color = ArcSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ArcBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ArcTextPrimary
                )
            ) {
                Text(
                    text = "Cancelar",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onGuardar,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ArcPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Guardar",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}