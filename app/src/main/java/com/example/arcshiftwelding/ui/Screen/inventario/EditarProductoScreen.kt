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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    navController: NavController,
    productoId: Int
) {
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val seleccionarImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }
    var nombre by remember { mutableStateOf("PTR 2\"x2\" Cal. 14") }
    var categoria by remember { mutableStateOf("Materiales") }
    var unidad by remember { mutableStateOf("Pieza") }
    var codigo by remember { mutableStateOf("MAT-001") }
    var ubicacion by remember { mutableStateOf("Estante A-01") }
    var descripcion by remember { mutableStateOf("Tubo cuadrado estructural de acero al carbón.") }

    var stockActual by remember { mutableStateOf("10") }
    var stockMinimo by remember { mutableStateOf("5") }
    var stockMaximo by remember { mutableStateOf("100") }
    var estado by remember { mutableStateOf("En Stock") }

    var costoUnitario by remember { mutableStateOf("120.00") }
    var proveedor by remember { mutableStateOf("Proveedor 1") }
    var notas by remember { mutableStateOf("") }

    var permitirStockNegativo by remember { mutableStateOf(true) }
    var productoActivo by remember { mutableStateOf(true) }

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
                        opciones = listOf("Materiales", "Herramientas", "Consumibles"),
                        valorSeleccionado = categoria,
                        onSeleccionar = { categoria = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    MenuDesplegable(
                        label = "Unidad de medida *",
                        placeholder = "Seleccionar unidad",
                        opciones = listOf("Pieza", "Metro", "Kg", "Caja"),
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
                        // Aquí después harás el UPDATE en Room
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