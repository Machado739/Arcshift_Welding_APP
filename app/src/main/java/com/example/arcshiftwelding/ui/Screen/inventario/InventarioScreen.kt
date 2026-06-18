package com.example.arcshiftwelding.ui.Screen.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.navigation.BottomNavigationBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.repository.ProductoRepository
import com.example.arcshiftwelding.ui.viewmodel.ProductoViewModel
import com.example.arcshiftwelding.ui.viewmodel.ProductoViewModelFactory


data class ProductoUI(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val codigo: String,
    val ubicacion: String,
    val stock: Int,
    val unidad: String,
    val stockMinimo: Int,
    val stockMaximo: Int,
    val estado: String,
    val imagenUri: String
)


fun ProductoEntity.toProductoUI(): ProductoUI {
    return ProductoUI(
        id = id,
        nombre = nombre,
        categoria = categoria,
        codigo = codigo,
        ubicacion = ubicacion,
        stock = stock,
        unidad = unidad,
        stockMinimo = stockMinimo,
        stockMaximo = stockMaximo,
        estado = estado,
        imagenUri = imagenUri
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    onNuevoProducto: () -> Unit = {},
    onDetalleProducto: (ProductoUI) -> Unit = {},
    navController: NavController
) {



    //      BASE DE DATOS
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

    val productosEntity by productoViewModel.productos.collectAsState()

    val productos = productosEntity.map { producto ->
        producto.toProductoUI()
    }

    //
    var busqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

/*
    val productos = listOf(
        ProductoUI(1, "PRT 2\"x2\" Cal. 14", "Materiales", "MAT-001", "Almacén A", 10, "Piezas"),
        ProductoUI(2, "Soldadura 6013 1/8", "Consumibles", "CON-001", "Almacén B", 25, "Cajas"),
        ProductoUI(3, "Disco de corte 4 1/2", "Herramientas", "HER-001", "Almacén A", 0, "Piezas"),
        ProductoUI(4, "Guantes de carnaza", "Seguridad", "SEG-001", "Almacén C", 4, "Pares"),

        ProductoUI(5, "PTR 1 1/2\"x1 1/2\" Cal. 14", "Materiales", "MAT-002", "Almacén A", 18, "Piezas"),
        ProductoUI(6, "Ángulo 1 1/2\" x 1/8", "Materiales", "MAT-003", "Almacén A", 12, "Piezas"),
        ProductoUI(7, "Solera 1\" x 1/8", "Materiales", "MAT-004", "Almacén A", 20, "Piezas"),
        ProductoUI(8, "Lámina negra Cal. 14", "Materiales", "MAT-005", "Almacén A", 6, "Hojas"),
        ProductoUI(9, "Tubo redondo 1\"", "Materiales", "MAT-006", "Almacén A", 15, "Piezas"),

        ProductoUI(10, "Soldadura 7018 1/8", "Consumibles", "CON-002", "Almacén B", 14, "Cajas"),
        ProductoUI(11, "Soldadura 6011 1/8", "Consumibles", "CON-003", "Almacén B", 8, "Cajas"),
        ProductoUI(12, "Microalambre ER70S-6", "Consumibles", "CON-004", "Almacén B", 5, "Rollos"),
        ProductoUI(13, "Gas CO2 para soldar", "Consumibles", "CON-005", "Almacén B", 3, "Cilindros"),
        ProductoUI(14, "Punta de contacto MIG 0.035", "Consumibles", "CON-006", "Almacén B", 30, "Piezas"),

        ProductoUI(15, "Disco flap 4 1/2", "Herramientas", "HER-002", "Almacén A", 16, "Piezas"),
        ProductoUI(16, "Disco desbaste 7\"", "Herramientas", "HER-003", "Almacén A", 9, "Piezas"),
        ProductoUI(17, "Broca para metal 1/4", "Herramientas", "HER-004", "Almacén C", 22, "Piezas"),
        ProductoUI(18, "Prensa tipo C 6\"", "Herramientas", "HER-005", "Almacén C", 7, "Piezas"),
        ProductoUI(19, "Escuadra magnética", "Herramientas", "HER-006", "Almacén C", 11, "Piezas"),

        ProductoUI(20, "Careta electrónica para soldar", "Seguridad", "SEG-002", "Almacén C", 3, "Piezas"),
        ProductoUI(21, "Lentes de seguridad claros", "Seguridad", "SEG-003", "Almacén C", 18, "Piezas"),
        ProductoUI(22, "Mandil de carnaza", "Seguridad", "SEG-004", "Almacén C", 5, "Piezas"),
        ProductoUI(23, "Mangas de carnaza", "Seguridad", "SEG-005", "Almacén C", 6, "Pares"),
        ProductoUI(24, "Botas de seguridad", "Seguridad", "SEG-006", "Almacén C", 2, "Pares")
    )
*/

    val productosFiltrados = productos.filter { producto ->
        val coincideBusqueda =
            producto.nombre.contains(busqueda, ignoreCase = true) ||
                    producto.codigo.contains(busqueda, ignoreCase = true) ||
                    producto.categoria.contains(busqueda, ignoreCase = true)

        val coincideCategoria =
            categoriaSeleccionada == "Todos" || producto.categoria == categoriaSeleccionada

        coincideBusqueda && coincideCategoria
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(
                    start = 8.dp,
                    top = 0.dp,
                    end = 8.dp,
                    bottom = 8.dp
                )
        ) {

            HeaderInventario(navController = navController)

            Spacer(modifier = Modifier.height(8.dp))

            TarjetasResumenInventario(
                total = productos.size,
                enStock = productos.count { it.stock > 5 },
                stockBajo = productos.count { it.stock in 1..5 },
                sinStock = productos.count { it.stock == 0 }
            )

            Spacer(modifier = Modifier.height(12.dp))

            BarraBusquedaFiltrosInventario(
                busqueda = busqueda,
                onBusquedaChange = { busqueda = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(AppRoutes.NUEVO_PRODUCTO)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D4ED8)
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nuevo producto")
                }

                OutlinedButton(
                    onClick = {
                        navController.navigate(AppRoutes.SELECCIONAR_PRODUCTO_REPONER)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AddBox, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reponer stock")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FiltrosCategoriaInventario(
                seleccionada = categoriaSeleccionada,
                onSeleccionar = { categoriaSeleccionada = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            ListaProductosInventario(
                productos = productosFiltrados,
                onClickProducto = { producto ->
                    navController.navigate(AppRoutes.detalleProducto(producto.id))
                }
            )

        }
    }

@Composable
fun HeaderInventario(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(Icons.Default.Menu, contentDescription = "Menú")
        }

        Text(
            text = "Inventario",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
        }

        IconButton(
            onClick = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
        }
    }
}

@Composable
fun TarjetasResumenInventario(
    total: Int,
    enStock: Int,
    stockBajo: Int,
    sinStock: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TarjetaResumenInventario(
            titulo = "Total",
            valor = total.toString(),
            subtitulo = "Productos",
            icono = Icons.Default.Inventory,
            colorIcono = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumenInventario(
            titulo = "En stock",
            valor = enStock.toString(),
            subtitulo = "Disponibles",
            icono = Icons.Default.CheckCircle,
            colorIcono = Color(0xFF16A34A),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumenInventario(
            titulo = "Stock bajo",
            valor = stockBajo.toString(),
            subtitulo = "Revisar",
            icono = Icons.Default.Warning,
            colorIcono = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumenInventario(
            titulo = "Sin stock",
            valor = sinStock.toString(),
            subtitulo = "Agotados",
            icono = Icons.Default.Cancel,
            colorIcono = Color(0xFFDC2626),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TarjetaResumenInventario(
    titulo: String,
    valor: String,
    subtitulo: String,
    icono: ImageVector,
    colorIcono: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(105.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(colorIcono.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
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
fun BarraBusquedaFiltrosInventario(
    busqueda: String,
    onBusquedaChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            placeholder = {
                Text("Buscar producto...")
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedButton(
            onClick = { },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.Default.FilterList, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Filtros")
        }
    }
}

@Composable
fun FiltrosCategoriaInventario(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf(
        "Todos",
        "Materiales",
        "Consumibles",
        "Herramientas",
        "Seguridad",
        "Equipos",
        "Más"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias) { categoria ->
            CategoriaChipInventario(
                texto = categoria,
                seleccionado = seleccionada == categoria,
                onClick = {
                    onSeleccionar(categoria)
                }
            )
        }
    }
}
@Composable
fun CategoriaChipInventario(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = texto,
                maxLines = 1
            )
        },
        leadingIcon = {
            if (seleccionado) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (seleccionado) Color(0xFFE0ECFF) else Color.White,
            labelColor = if (seleccionado) Color(0xFF1D4ED8) else Color.DarkGray
        )
    )
}
@Composable
fun ListaProductosInventario(
    productos: List<ProductoUI>,
    onClickProducto: (ProductoUI) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
            .padding(
                start = 0.dp,
                top = 0.dp,
                end = 0.dp,
                bottom = 8.dp
            )
    ) {
        items(productos) { producto ->
            ItemProductoInventario(
                producto = producto,
                onClick = {
                    onClickProducto(producto)
                }
            )
        }
    }
}

@Composable
fun ItemProductoInventario(
    producto: ProductoUI,
    onClick: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoCategoriaProducto(producto.categoria)

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Código: ${producto.codigo}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.DarkGray
                )

                Text(
                    text = "Ubicación: ${producto.ubicacion}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Text(
                    text = producto.categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2563EB)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = estadoTexto,
                    fontWeight = FontWeight.Bold,
                    color = estadoColor,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Stock actual",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Text(
                    text = producto.stock.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = producto.unidad,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(4.dp))


        }
    }
}

@Composable
fun IconoCategoriaProducto(
    categoria: String
) {
    val icono = when (categoria) {
        "Materiales" -> Icons.Default.Inventory2
        "Consumibles" -> Icons.Default.Category
        "Herramientas" -> Icons.Default.Warehouse
        "Seguridad" -> Icons.Default.CheckCircle
        else -> Icons.Default.MoreHoriz
    }

    val color = when (categoria) {
        "Materiales" -> Color(0xFF2563EB)
        "Consumibles" -> Color(0xFF7C3AED)
        "Herramientas" -> Color(0xFFF59E0B)
        "Seguridad" -> Color(0xFF16A34A)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(45.dp)
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color
        )
    }
}
