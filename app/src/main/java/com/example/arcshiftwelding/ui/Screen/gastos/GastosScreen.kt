package com.example.arcshiftwelding.ui.gastos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.navigation.BottomNavigationBar
import com.example.arcshiftwelding.ui.Screen.inventario.HeaderInventario

data class GastoUi(
    val id: Int,
    val titulo: String,
    val proveedor: String,
    val categoria: String,
    val monto: Double,
    val fecha: String,
    val metodoPago: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    navController: NavController

) {
    val gastos = listOf(
        GastoUi(1, "Compra de material", "Aceros del Norte", "Materiales", 3200.00, "10/5/2026", "Efectivo"),
        GastoUi(2, "Combustible", "Gasolinera PEMEX", "Transporte", 850.00, "08/5/2026", "Tarjeta"),
        GastoUi(3, "Mantenimiento de equipo", "Taller Mecánico JR", "Servicios", 1500.00, "10/5/2026", "Transferencia"),
        GastoUi(4, "Pago de servicio eléctrico", "CFE", "Servicios", 2100.00, "18/5/2026", "Efectivo"),
        GastoUi(5, "Pago a empleado", "Jaime Lozano", "Nómina", 980.00, "17/5/2026", "Efectivo"),
        GastoUi(6, "Compra de insumos", "Ferretería Industrial", "Materiales", 1250.00, "17/5/2026", "Tarjeta")
    )
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(8.dp)
        ) {

            HeaderGastos()

            Spacer(modifier = Modifier.height(8.dp))

            TarjetasResumenGastos()

            Spacer(modifier = Modifier.height(12.dp))

            BarraBusquedaFiltros()

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    navController.navigate(AppRoutes.NUEVO_GASTO)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo gasto")
            }

            Spacer(modifier = Modifier.height(10.dp))

            FiltrosCategoriaGastos()

            Spacer(modifier = Modifier.height(8.dp))

            ListaGastos(
                gastos = gastos,
                onClickGasto = { gasto ->
                    navController.navigate(AppRoutes.detalleGasto(gasto.id))
                }
            )
        }
    }
}


@Composable
fun HeaderGastos() {
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
            text = "Gastos",
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
fun TarjetasResumenGastos() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TarjetaResumenGasto(
            titulo = "Total gastos",
            valor = "$13,500",
            subtitulo = "Este mes",
            icono = Icons.Default.AttachMoney,
            colorIcono = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumenGasto(
            titulo = "Gastos hoy",
            valor = "$1,250",
            subtitulo = "19/5/2026",
            icono = Icons.Default.ArrowDownward,
            colorIcono = Color(0xFF16A34A),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumenGasto(
            titulo = "Categorías",
            valor = "6",
            subtitulo = "Registradas",
            icono = Icons.Default.Category,
            colorIcono = Color(0xFF7C3AED),
            modifier = Modifier.weight(1f)
        )

        TarjetaResumenGasto(
            titulo = "Promedio diario",
            valor = "$450",
            subtitulo = "Este mes",
            icono = Icons.Default.Analytics,
            colorIcono = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TarjetaResumenGasto(
    titulo: String,
    valor: String,
    subtitulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
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
fun BarraBusquedaFiltros() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = { },
            placeholder = {
                Text("Buscar gasto...")
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
fun FiltrosCategoriaGastos() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CategoriaChip("Todos", true)
        CategoriaChip("Materiales", false)
        CategoriaChip("Servicios", false)
        CategoriaChip("Transporte", false)
        CategoriaChip("Más", false)
    }
}

@Composable
fun CategoriaChip(
    texto: String,
    seleccionado: Boolean
) {
    AssistChip(
        onClick = { },
        label = {
            Text(texto)
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
fun ListaGastos(
    gastos: List<GastoUi>,
    onClickGasto: (GastoUi) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(gastos) { gasto ->
            ItemGasto(
                gasto = gasto,
                onClick = {
                    onClickGasto(gasto)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            PaginacionGastos()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ItemGasto(
    gasto: GastoUi,
    onClick: () -> Unit
) {
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
            IconoCategoriaGasto(gasto.categoria)

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = gasto.titulo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Proveedor: ${gasto.proveedor}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.DarkGray
                )

                Text(
                    text = gasto.categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2563EB)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${String.format("%.2f", gasto.monto)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = gasto.fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Text(
                    text = gasto.metodoPago,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "Opciones"
                )
            }
        }
    }
}

@Composable
fun IconoCategoriaGasto(
    categoria: String
) {
    val icono = when (categoria) {
        "Materiales" -> Icons.Default.ShoppingCart
        "Transporte" -> Icons.Default.LocalGasStation
        "Servicios" -> Icons.Default.Build
        "Nómina" -> Icons.Default.Person
        else -> Icons.Default.MoreHoriz
    }

    val color = when (categoria) {
        "Materiales" -> Color(0xFF2563EB)
        "Transporte" -> Color(0xFF16A34A)
        "Servicios" -> Color(0xFFF59E0B)
        "Nómina" -> Color(0xFF22C55E)
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

@Composable
fun PaginacionGastos() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mostrando 1 a 7 de 45 gastos",
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            modifier = Modifier.weight(1f)
        )

        AssistChip(
            onClick = { },
            label = { Text("1") },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = Color(0xFF1D4ED8),
                labelColor = Color.White
            )
        )

        Spacer(modifier = Modifier.width(4.dp))

        AssistChip(
            onClick = { },
            label = { Text("2") }
        )

        Spacer(modifier = Modifier.width(4.dp))

        AssistChip(
            onClick = { },
            label = { Text("3") }
        )
    }
}