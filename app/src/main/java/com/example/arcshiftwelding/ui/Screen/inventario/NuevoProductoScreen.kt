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


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun NuevoProductoScreen(
    navController: NavController
) {


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Nuevo Producto")
                        },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar")
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeccionInformacionGeneral()
            SeccionInventario()
            SeccionCostos()
            SeccionInformacionAdicional()
            SeccionOpciones()
            BotonesFormulario(
                onCancelar = { navController.popBackStack() },
                onGuardar = { }
            )

            Spacer(modifier = Modifier.height(80.dp))
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
}
@Composable
fun SeccionInformacionGeneral() {
    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    var categoria by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val seleccionarImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
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
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del producto *") },
                        placeholder = { Text("Ej. PTR 2 x2 Cal. 14") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    MenuDesplegable(
                        label = "Categoría *",
                        placeholder = "Seleccionar categoría",
                        opciones = listOf("Material", "Herramienta", "Consumible"),
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
                        placeholder = { Text("Ej. MAT-001") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = ubicacion,
                        onValueChange = { ubicacion = it },
                        label = { Text("Ubicación *") },
                        placeholder = { Text("Ej. Estante A-01") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            if (it.length <= 200) descripcion = it
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

            } else {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(width = 170.dp, height = 180.dp)
                                .background(
                                    color = Color(0xFFF2F2F2),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Agregar imagen")

                                Text(
                                    text = "JPG, PNG (máx. 2MB)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            OutlinedTextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre del producto *") },
                                placeholder = { Text("Ej. PTR 2 x2 Cal. 14") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                MenuDesplegable(
                                    label = "Categoría *",
                                    placeholder = "Seleccionar categoría",
                                    opciones = listOf("Material", "Herramienta", "Consumible"),
                                    valorSeleccionado = categoria,
                                    onSeleccionar = { categoria = it },
                                    modifier = Modifier.weight(1f)
                                )

                                MenuDesplegable(
                                    label = "Unidad de medida *",
                                    placeholder = "Seleccionar unidad",
                                    opciones = listOf("Pieza", "Metro", "Kg", "Caja"),
                                    valorSeleccionado = unidad,
                                    onSeleccionar = { unidad = it },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = codigo,
                                    onValueChange = { codigo = it },
                                    label = { Text("Código / SKU *") },
                                    placeholder = { Text("Ej. MAT-001") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = ubicacion,
                                    onValueChange = { ubicacion = it },
                                    label = { Text("Ubicación *") },
                                    placeholder = { Text("Ej. Estante A-01") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            if (it.length <= 200) descripcion = it
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
fun SeccionInventario() {
    var stockInicial by remember { mutableStateOf("") }
    var stockMinimo by remember { mutableStateOf("") }
    var stockMaximo by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("En Stock") }

    FormularioCard(
        titulo = "Inventario",
        icono = Icons.Default.Inventory2
    ) {

        BoxWithConstraints {
            val esPantallaChica = maxWidth < 600.dp

            if (esPantallaChica) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = stockInicial,
                        onValueChange = { stockInicial = it },
                        label = { Text("Stock inicial *") },
                        placeholder = { Text("Ej. 10") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = stockMinimo,
                        onValueChange = { stockMinimo = it },
                        label = { Text("Stock mínimo *") },
                        placeholder = { Text("Ej. 5") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = stockMaximo,
                        onValueChange = { stockMaximo = it },
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
                        onSeleccionar = { estado = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = stockInicial,
                        onValueChange = { stockInicial = it },
                        label = { Text("Stock inicial *") },
                        placeholder = { Text("Ej. 10") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = stockMinimo,
                        onValueChange = { stockMinimo = it },
                        label = { Text("Stock mínimo *") },
                        placeholder = { Text("Ej. 5") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = stockMaximo,
                        onValueChange = { stockMaximo = it },
                        label = { Text("Stock máximo") },
                        placeholder = { Text("Ej. 100") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    MenuDesplegable(
                        label = "Estado inicial *",
                        placeholder = "Estado",
                        opciones = listOf("En Stock", "Bajo Stock", "Agotado"),
                        valorSeleccionado = estado,
                        onSeleccionar = { estado = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
@Composable
fun SeccionCostos() {
    var costoUnitario by remember { mutableStateOf("") }

    FormularioCard(
        titulo = "Costos",
        icono = Icons.Default.AttachMoney
    ) {

        BoxWithConstraints {
            val esPantallaChica = maxWidth < 600.dp

            val costoTotal = 0.0

            if (esPantallaChica) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = costoUnitario,
                        onValueChange = { costoUnitario = it },
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

            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    OutlinedTextField(
                        value = costoUnitario,
                        onValueChange = { costoUnitario = it },
                        label = { Text("Costo unitario *") },
                        placeholder = { Text("0.00") },
                        leadingIcon = { Text("$") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = costoTotal.toString(),
                        onValueChange = {},
                        label = { Text("Costo total inicial") },
                        leadingIcon = { Text("$") },
                        modifier = Modifier.weight(1f),
                        enabled = false,
                        singleLine = true
                    )
                }
            }
        }
    }
}
@Composable
fun SeccionInformacionAdicional() {
    var proveedor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    FormularioCard(
        titulo = "Información adicional",
        icono = Icons.Default.LocalOffer
    ) {

        BoxWithConstraints {
            val esPantallaChica = maxWidth < 600.dp

            if (esPantallaChica) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                        placeholder = { Text("Notas adicionales opcional") },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            Text("${notas.length}/150")
                        }
                    )
                }

            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    MenuDesplegable(
                        label = "Proveedor",
                        placeholder = "Seleccionar proveedor",
                        opciones = listOf("Proveedor 1", "Proveedor 2", "Proveedor 3"),
                        valorSeleccionado = proveedor,
                        onSeleccionar = { proveedor = it },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = notas,
                        onValueChange = {
                            if (it.length <= 150) notas = it
                        },
                        label = { Text("Notas") },
                        placeholder = { Text("Notas adicionales opcional") },
                        modifier = Modifier.weight(1f),
                        supportingText = {
                            Text("${notas.length}/150")
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun SeccionOpciones() {
    var permitirStockNegativo by remember { mutableStateOf(true) }
    var productoActivo by remember { mutableStateOf(true) }

    FormularioCard(
        titulo = "Opciones",
        icono = Icons.Default.Settings
    ) {

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