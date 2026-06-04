package com.example.arcshiftwelding.ui.Screen.inventario

import android.R.attr.background
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.navigation.BottomNavigationBar

data class ProductoUI(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val codigo: String,
    val ubicacion: String,
    val stock: Int,
    val unidad: String
)

@Composable
fun InventarioScreen(
    onNuevoProducto: () -> Unit = {},
    onDetalleProducto: (ProductoUI) -> Unit = {},
    navController: NavController
) {



    var busqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todo") }

    val productos = listOf(
        ProductoUI(1,"PRT 2\"x2\" Cal. 14", "Material", "MAT-001", "MAT-001", 10, "Piezas"),
        ProductoUI(2,"Soldadura 6013 1/8", "Consumible", "CON-001", "Almacén B", 25, "Cajas"),
        ProductoUI(3,"Disco de corte 4 1/2", "Herramienta", "HER-001", "Almacén A", 0, "Piezas")
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->



    val productosFiltrados = productos.filter {
        it.nombre.contains(busqueda, ignoreCase = true) &&
                (categoriaSeleccionada == "Todo" || it.categoria == categoriaSeleccionada)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(background))
            .padding(8.dp)
    ) {

        HeaderInventario()

        Spacer(modifier = Modifier.height(8.dp))

        ResumenInventario(
            total = productos.size,
            enStock = productos.count { it.stock > 5 },
            stockBajo = productos.count { it.stock in 1..5 },
            sinStock = productos.count { it.stock == 0 }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar Material") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { }) {
                Icon(Icons.Default.FilterList, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Filtros")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(
                onClick = {
                    navController.navigate(AppRoutes.NUEVO_PRODUCTO)
                }
            ) {
                Text("Agregar producto")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    navController.navigate(AppRoutes.SELECCIONAR_PRODUCTO_REPONER)
                          },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reponer Stock")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CategoriasInventario(
            seleccionada = categoriaSeleccionada,
            onSeleccionar = { categoriaSeleccionada = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(productosFiltrados) { producto ->
                ProductoCard(
                    producto = producto,
                    onClick = { onDetalleProducto(producto) }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    }
}

@Composable
fun HeaderInventario() {
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
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
        }


    }
}

@Composable
fun ResumenInventario(
    total: Int,
    enStock: Int,
    stockBajo: Int,
    sinStock: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ResumenCard("Total productos", total.toString(), Icons.Default.Inventory, Modifier.weight(1f))
        ResumenCard("En Stock", enStock.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
        ResumenCard("Stock bajo", stockBajo.toString(), Icons.Default.Warning, Modifier.weight(1f))
        ResumenCard("Sin Stock", sinStock.toString(), Icons.Default.Cancel, Modifier.weight(1f))
    }
}

@Composable
fun ResumenCard(
    titulo: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, contentDescription = null)
            Text(titulo, style = MaterialTheme.typography.bodySmall)
            Text(valor, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun CategoriasInventario(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf("Todo", "Materiales", "Consumibles", "Herramientas", "Más")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        categorias.forEach { categoria ->
            Button(
                onClick = { onSeleccionar(categoria) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (seleccionada == categoria) Color.DarkGray else Color.LightGray,
                    contentColor = if (seleccionada == categoria) Color.White else Color.Black
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = categoria,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: ProductoUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clickable { onClick() }
            .border(1.dp, Color.Gray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(65.dp)
                    .background(Color(0xFFD9D9D9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Inventory2, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(producto.nombre, style = MaterialTheme.typography.titleSmall)
                Text(producto.categoria, style = MaterialTheme.typography.bodySmall)
                Text("Código: ${producto.codigo}", style = MaterialTheme.typography.bodySmall)
                Text("Ubicación: ${producto.ubicacion}", style = MaterialTheme.typography.bodySmall)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when {
                        producto.stock == 0 -> "Sin Stock"
                        producto.stock <= 5 -> "Stock bajo"
                        else -> "En Stock"
                    },
                    color = when {
                        producto.stock == 0 -> Color.Red
                        producto.stock <= 5 -> Color(0xFFE67E22)
                        else -> Color(0xFF2E7D32)
                    }
                )

                Text("Stock actual", style = MaterialTheme.typography.bodySmall)
                Text("${producto.stock}", style = MaterialTheme.typography.titleLarge)
                Text(producto.unidad, style = MaterialTheme.typography.bodySmall)
            }

            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }

}
