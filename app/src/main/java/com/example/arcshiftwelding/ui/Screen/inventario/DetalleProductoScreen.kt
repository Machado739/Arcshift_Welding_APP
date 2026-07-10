package com.example.arcshiftwelding.ui.Screen.inventario


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.arcshiftwelding.navigation.AppRoutes
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.repository.ProductoRepository
import com.example.arcshiftwelding.ui.viewmodel.ProductoViewModel
import com.example.arcshiftwelding.ui.viewmodel.ProductoViewModelFactory
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.example.arcshiftwelding.data.repository.MovimientoInventarioRepository
import com.example.arcshiftwelding.ui.viewmodel.MovimientoInventarioViewModel
import com.example.arcshiftwelding.ui.viewmodel.MovimientoInventarioViewModelFactory
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
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

    val producto by productoViewModel.productoSeleccionado.collectAsState()

    LaunchedEffect(productoId) {
        productoViewModel.cargarProductoPorId(productoId)
    }

    val movimientoRepository = remember {
        MovimientoInventarioRepository(database.movimientoInventarioDao())
    }

    val movimientoViewModel: MovimientoInventarioViewModel = viewModel(
        factory = MovimientoInventarioViewModelFactory(movimientoRepository)
    )

    val movimientosRecientes by movimientoViewModel.movimientosRecientes.collectAsState()

    LaunchedEffect(productoId) {
        productoViewModel.cargarProductoPorId(productoId)
        movimientoViewModel.cargarMovimientosRecientes(productoId)
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
                    text = "Detalle del Producto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        navController.navigate(AppRoutes.editarProducto(productoId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Producto"
                    )
                }
            }
        },
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0)
    ) { padding ->

        when {
            producto == null -> {
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
            }

            else -> {
                val productoActual = producto!!

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(Color(0xFFF5F6FA))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    EncabezadoProductoDetalle(
                        producto = productoActual
                    )

                    SeccionDetalleInformacionGeneral(
                        producto = productoActual
                    )

                    SeccionDetalleInventario(
                        producto = productoActual
                    )

                    SeccionDetalleCostos(
                        producto = productoActual
                    )

                    SeccionDetalleAdicional(
                        producto = productoActual
                    )

                    SeccionMovimientosRecientes(
                        movimientos = movimientosRecientes,
                        onVerTodo = {
                            navController.navigate(AppRoutes.historialMovimientosProducto(productoId))
                        }
                    )
                    SeccionAccionesRapidas(
                        onEditar = {
                            navController.navigate(AppRoutes.editarProducto(productoId))
                        },
                        onAgregarStock = {
                            navController.navigate(AppRoutes.reponerStock(productoId))
                        },
                        onReportarSalida = {
                            navController.navigate(AppRoutes.reportarSalida(productoId))
                        },
                        onEliminar = {
                            navController.navigate(AppRoutes.eliminarProducto(productoId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EncabezadoProductoDetalle(
    producto: ProductoEntity
) {
    val estadoTexto = when (producto.estado) {
        "Agotado" -> "Sin stock"
        "Bajo Stock" -> "Stock bajo"
        else -> "En stock"
    }

    val estadoColor = when (producto.estado) {
        "Agotado" -> Color(0xFFDC2626)
        "Bajo Stock" -> Color(0xFFF59E0B)
        else -> Color(0xFF16A34A)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color(0xFFEDEDED),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (producto.imagenUri.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(File(producto.imagenUri)),
                        contentDescription = "Imagen del producto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        modifier = Modifier.size(42.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Código: ${producto.codigo}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Ubicación: ${producto.ubicacion}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(estadoTexto)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = estadoColor,
                        leadingIconContentColor = estadoColor
                    )
                )

                OutlinedCard(
                    modifier = Modifier.width(82.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Stock actual",
                            style = MaterialTheme.typography.labelSmall
                        )

                        Text(
                            text = producto.stock.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = producto.unidad,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun DetalleCard(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable () -> Unit
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
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            contenido()
        }
    }
}


@Composable
fun SeccionDetalleInformacionGeneral(
    producto: ProductoEntity
) {
    DetalleCard(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CampoDetalle(
                        titulo = "Nombre del producto",
                        valor = producto.nombre
                    )

                    CampoDetalle(
                        titulo = "Categoría",
                        valor = producto.categoria
                    )

                    CampoDetalle(
                        titulo = "Código / SKU",
                        valor = producto.codigo
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CampoDetalle(
                        titulo = "Descripción",
                        valor = producto.descripcion.ifBlank { "Sin descripción" }
                    )

                    CampoDetalle(
                        titulo = "Unidad de medida",
                        valor = producto.unidad
                    )
                }
            }
        }
    }
}



@Composable
fun CampoDetalle(
    titulo: String,
    valor: String
) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )
    }
}

@Composable
fun SeccionDetalleInventario(
    producto: ProductoEntity
) {
    val estadoTexto = when (producto.estado) {
        "Agotado" -> "Sin stock"
        "Bajo Stock" -> "Stock bajo"
        else -> "En stock"
    }

    val estadoColor = when (producto.estado) {
        "Agotado" -> Color(0xFFDC2626)
        "Bajo Stock" -> Color(0xFFF59E0B)
        else -> Color(0xFF16A34A)
    }

    DetalleCard(
        titulo = "Inventario",
        icono = Icons.Default.Inventory2
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ItemInventarioDetalle(
                titulo = "Stock actual",
                valor = producto.stock.toString(),
                subtitulo = producto.unidad,
                modifier = Modifier.weight(1f)
            )

            SeparadorVertical()

            ItemInventarioDetalle(
                titulo = "Stock mínimo",
                valor = producto.stockMinimo.toString(),
                subtitulo = producto.unidad,
                modifier = Modifier.weight(1f)
            )

            SeparadorVertical()

            ItemInventarioDetalle(
                titulo = "Stock máximo",
                valor = producto.stockMaximo.toString(),
                subtitulo = producto.unidad,
                modifier = Modifier.weight(1f)
            )

            SeparadorVertical()

            ItemInventarioDetalle(
                titulo = "Estado",
                valor = estadoTexto,
                subtitulo = "",
                modifier = Modifier.weight(1f),
                mostrarIconoEstado = true,
                colorEstado = estadoColor
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoDetalle(
            titulo = "Ubicación",
            valor = producto.ubicacion
        )
    }
}


@Composable
fun ItemInventarioDetalle(
    titulo: String,
    valor: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    mostrarIconoEstado: Boolean = false,
    colorEstado: Color = Color(0xFF16A34A)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (mostrarIconoEstado) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = colorEstado
                )

                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = valor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (mostrarIconoEstado) colorEstado else Color(0xFF111827)
            )
        }

        if (subtitulo.isNotEmpty()) {
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
fun SeparadorVertical() {
    Box(
        modifier = Modifier
            .height(48.dp)
            .width(1.dp)
            .background(Color(0xFFE5E7EB))
    )
}

@Composable
fun SeccionDetalleCostos(
    producto: ProductoEntity
) {
    val costoTotal = producto.stock * producto.precioCompra

    DetalleCard(
        titulo = "Costos",
        icono = Icons.Default.AttachMoney
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoDetalle(
                titulo = "Costo unitario",
                valor = "$ ${String.format("%.2f", producto.precioCompra)}"
            )

            Spacer(modifier = Modifier.weight(1f))

            CampoDetalle(
                titulo = "Costo total en inventario",
                valor = "$ ${String.format("%.2f", costoTotal)}"
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SeccionMovimientosRecientes(
    movimientos: List<MovimientoInventarioEntity>,
    onVerTodo: () -> Unit
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
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Movimientos recientes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = onVerTodo
                ) {
                    Text(
                        text = "Ver todo",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }

            Divider()

            Spacer(modifier = Modifier.height(6.dp))

            if (movimientos.isEmpty()) {
                Text(
                    text = "Sin movimientos registrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                EncabezadoTablaMovimientos()

                movimientos.take(5).forEach { movimiento ->

                    val esSalida = movimiento.tipo.contains("Salida", ignoreCase = true)
                    val esEntrada = movimiento.tipo.contains("Entrada", ignoreCase = true) ||
                            movimiento.tipo == "Registro inicial"

                    val cantidadTexto = when {
                        esSalida -> "- ${movimiento.cantidad} ${movimiento.unidad}"
                        esEntrada -> "+ ${movimiento.cantidad} ${movimiento.unidad}"
                        movimiento.tipo == "Ajuste" && movimiento.stockNuevo > movimiento.stockAnterior -> {
                            "+ ${movimiento.cantidad} ${movimiento.unidad}"
                        }
                        movimiento.tipo == "Ajuste" && movimiento.stockNuevo < movimiento.stockAnterior -> {
                            "- ${movimiento.cantidad} ${movimiento.unidad}"
                        }
                        else -> "${movimiento.cantidad} ${movimiento.unidad}"
                    }

                    MovimientoItem(
                        fecha = movimiento.fecha,
                        hora = movimiento.hora,
                        tipo = movimiento.tipo,
                        cantidad = cantidadTexto,
                        referencia = movimiento.referencia.ifBlank { "-" },
                        esEntrada = !esSalida
                    )
                }
            }
        }
    }
}

@Composable
fun EncabezadoTablaMovimientos() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        TextoTabla("Fecha", Modifier.weight(1.1f), esHeader = true)
        TextoTabla("Tipo", Modifier.weight(1f), esHeader = true)
        TextoTabla("Cantidad", Modifier.weight(1f), esHeader = true)
        TextoTabla("Referencia", Modifier.weight(1.2f), esHeader = true)
    }
}

@Composable
fun MovimientoItem(
    fecha: String,
    hora: String,
    tipo: String,
    cantidad: String,
    referencia: String,
    esEntrada: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1.1f)
        ) {
            Text(
                text = fecha,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF111827)
            )

            Text(
                text = hora,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        Text(
            text = tipo,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall,
            color = if (esEntrada) Color(0xFF1B7F3A) else Color(0xFFB42318),
            fontWeight = FontWeight.Medium
        )

        TextoTabla(cantidad, Modifier.weight(1f))
        TextoTabla(referencia, Modifier.weight(1.2f))
    }

    Divider(color = Color(0xFFF1F1F1))
}

@Composable
fun TextoTabla(
    texto: String,
    modifier: Modifier = Modifier,
    esHeader: Boolean = false
) {
    Text(
        text = texto,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = if (esHeader) Color.Gray else Color(0xFF111827),
        fontWeight = if (esHeader) FontWeight.Bold else FontWeight.Normal
    )
}


@Composable
fun SeccionAccionesRapidas(
    onEditar: () -> Unit,
    onAgregarStock: () -> Unit,
    onReportarSalida: () -> Unit,
    onEliminar: () -> Unit
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
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Acciones rápidas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BotonAccionRapida(
                    texto = "Editar\nProducto",
                    icono = Icons.Default.Edit,
                    onClick = onEditar,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionRapida(
                    texto = "Agregar\nStock",
                    icono = Icons.Default.AddCircleOutline,
                    onClick = onAgregarStock,
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFF1B7F3A)
                )

                BotonAccionRapida(
                    texto = "Reportar\nSalida",
                    icono = Icons.Default.RemoveCircleOutline,
                    onClick = onReportarSalida,
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFFB42318)
                )

                BotonAccionRapida(
                    texto = "Eliminar\nProducto",
                    icono = Icons.Default.Delete,
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFF111827)
                )
            }
        }
    }
}

@Composable
fun BotonAccionRapida(
    texto: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color(0xFF111827)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF111827)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SeccionDetalleAdicional(
    producto: ProductoEntity
) {
    DetalleCard(
        titulo = "Información adicional",
        icono = Icons.Default.LocalOffer
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoDetalle(
                titulo = "Proveedor",
                valor = producto.proveedor.ifBlank { "Sin proveedor" }
            )

            CampoDetalle(
                titulo = "Notas",
                valor = producto.notas.ifBlank { "Sin notas" }
            )

            CampoDetalle(
                titulo = "Fecha de registro",
                valor = producto.fechaRegistro.ifBlank { "Sin fecha" }
            )
/*
            CampoDetalle(
                titulo = "Permite stock negativo",
                valor = if (producto.permitirStockNegativo) "Sí" else "No"
            )
*/
            /*
            CampoDetalle(
                titulo = "Producto activo",
                valor = if (producto.activo) "Sí" else "No"
            )
                         */
        }
    }
}
