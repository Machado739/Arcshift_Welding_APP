package com.example.arcshiftwelding.ui.Screen.inventario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReponerStockScreen(
    navController: NavController,
    productoId: Int
) {
    /*
        Estos datos son de ejemplo.
        Después los cargarás desde Room usando el productoId.
    */
    val nombreProducto = "PTR 2\"x2\" Cal. 14"
    val codigoProducto = "MAT-001"
    val unidadMedida = "Piezas"
    val stockActual = 10
    val costoUnitario = 120.00

    var cantidadAgregar by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    val cantidad = cantidadAgregar.toIntOrNull() ?: 0
    val nuevoStock = stockActual + cantidad
    val costoMovimiento = cantidad * costoUnitario

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Reponer Stock")
                },
                navigationIcon = {
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
                }
            )
        }
    ) { padding ->

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
                nombreProducto = nombreProducto,
                codigoProducto = codigoProducto,
                stockActual = stockActual,
                unidadMedida = unidadMedida
            )

            CardCantidadReponer(
                cantidadAgregar = cantidadAgregar,
                onCantidadChange = { cantidadAgregar = it },
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
                        /*
                            Aquí después harás:
                            1. Buscar producto por productoId.
                            2. Sumar cantidad al stock actual.
                            3. Actualizar producto en Room.
                            4. Registrar movimiento tipo "Entrada".
                        */

                        navController.popBackStack()
                    },
                    enabled = cantidad > 0,
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

            Spacer(modifier = Modifier.height(80.dp))
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