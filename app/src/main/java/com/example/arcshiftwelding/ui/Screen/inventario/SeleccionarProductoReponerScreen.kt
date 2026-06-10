package com.example.arcshiftwelding.ui.Screen.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
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

data class ProductoPrueba(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val codigo: String,
    val stockActual: Int,
    val unidad: String
)

@Composable
fun SeleccionarProductoReponerScreen(
    navController: NavController
) {
    var busqueda by remember { mutableStateOf("") }

    val productos = listOf(
        ProductoPrueba(
            id = 1,
            nombre = "PTR 2x2 Cal. 14",
            categoria = "Materiales",
            codigo = "MAT-001",
            stockActual = 10,
            unidad = "Piezas"
        ),
        ProductoPrueba(
            id = 2,
            nombre = "Soldadura 7018",
            categoria = "Consumibles",
            codigo = "CON-001",
            stockActual = 25,
            unidad = "Cajas"
        ),
        ProductoPrueba(
            id = 3,
            nombre = "Disco de corte",
            categoria = "Herramientas",
            codigo = "HER-001",
            stockActual = 40,
            unidad = "Piezas"
        )
    )

    val productosFiltrados = productos.filter { producto ->
        producto.nombre.contains(busqueda, ignoreCase = true) ||
                producto.codigo.contains(busqueda, ignoreCase = true) ||
                producto.categoria.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(8.dp)
    ) {
        HeaderSeleccionarProductoReponer(
            onBackClick = {
                navController.popBackStack()
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        TarjetaInfoReponerStock(
            totalProductos = productos.size
        )

        Spacer(modifier = Modifier.height(10.dp))

        BarraBusquedaProductoReponer(
            busqueda = busqueda,
            onBusquedaChange = {
                busqueda = it
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Productos disponibles",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(productosFiltrados) { producto ->
                ItemProductoReponer(
                    producto = producto,
                    onClick = {
                        navController.navigate(
                            AppRoutes.reponerStock(producto.id)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderSeleccionarProductoReponer(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick
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
                text = "Reponer stock",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Seleccionar producto",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}
@Composable
fun TarjetaInfoReponerStock(
    totalProductos: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0ECFF)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF1D4ED8).copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AddBox,
                    contentDescription = null,
                    tint = Color(0xFF1D4ED8),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Selecciona un producto",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Elige el producto al que deseas agregar stock",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "$totalProductos productos",
                color = Color(0xFF1D4ED8),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun BarraBusquedaProductoReponer(
    busqueda: String,
    onBusquedaChange: (String) -> Unit
) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = onBusquedaChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text("Buscar producto...")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF2563EB),
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )
}

@Composable
fun ItemProductoReponer(
    producto: ProductoPrueba,
    onClick: () -> Unit
) {
    val stockColor = when {
        producto.stockActual == 0 -> Color(0xFFDC2626)
        producto.stockActual <= 5 -> Color(0xFFF59E0B)
        else -> Color(0xFF16A34A)
    }

    val stockTexto = when {
        producto.stockActual == 0 -> "Sin stock"
        producto.stockActual <= 5 -> "Stock bajo"
        else -> "Disponible"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoProductoReponer(
                icono = Icons.Default.Inventory,
                color = Color(0xFF2563EB)
            )

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
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = producto.categoria,
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stockTexto,
                    color = stockColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = "Stock actual",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = producto.stockActual.toString(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = producto.unidad,
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun IconoProductoReponer(
    icono: ImageVector,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}