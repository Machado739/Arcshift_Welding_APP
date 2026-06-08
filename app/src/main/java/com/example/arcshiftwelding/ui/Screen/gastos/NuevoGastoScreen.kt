package com.example.arcshiftwelding.ui.Screen.gastos

import androidx.compose.foundation.background
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoGastoScreen(
    onBack: () -> Unit = {},
    onGuardar: () -> Unit = {},
    onCancelar: () -> Unit = {}

) {
    var concepto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Seleccionar categoría") }
    var fecha by remember { mutableStateOf("19/05/2026") }
    var proveedor by remember { mutableStateOf("Seleccionar proveedor") }

    var subtotal by remember { mutableStateOf("") }
    var ivaPorcentaje by remember { mutableStateOf("16") }
    var metodoPago by remember { mutableStateOf("Seleccionar método") }
    var formaPago by remember { mutableStateOf("Contado") }

    var observaciones by remember { mutableStateOf("") }

    var proyecto by remember { mutableStateOf("Seleccionar") }
    var cotizacion by remember { mutableStateOf("Seleccionar") }
    var cliente by remember { mutableStateOf("Seleccionar") }

    val subtotalValor = subtotal.toDoubleOrNull() ?: 0.0
    val porcentajeValor = ivaPorcentaje.toDoubleOrNull() ?: 0.0
    val ivaCalculado = subtotalValor * (porcentajeValor / 100.0)
    val totalCalculado = subtotalValor + ivaCalculado

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo Gasto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }

                    TextButton(onClick = { }) {
                        Text("Log Out")
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 1. INFORMACIÓN GENERAL
            TarjetaSeccion(
                titulo = "Información general",
                icono = Icons.Default.Info
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoTexto(
                        label = "Concepto *",
                        value = concepto,
                        onValueChange = { concepto = it },
                        placeholder = "Ej. Compra de material",
                        modifier = Modifier.weight(1f)
                    )

                    CampoSelector(
                        label = "Categoría *",
                        value = categoria,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoTexto(
                        label = "Fecha *",
                        value = fecha,
                        onValueChange = { fecha = it },
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        CampoSelector(
                            label = "Proveedor *",
                            value = proveedor,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.height(56.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar proveedor")
                        }
                    }
                }
            }

            // 2. INFORMACIÓN FINANCIERA
            TarjetaSeccion(
                titulo = "Información financiera",
                icono = Icons.Default.AttachMoney
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoTexto(
                        label = "Subtotal *",
                        value = subtotal,
                        onValueChange = { subtotal = it },
                        placeholder = "$ 0.00",
                        modifier = Modifier.weight(1f)
                    )

                    CampoSelector(
                        label = "IVA (%)",
                        value = ivaPorcentaje,
                        modifier = Modifier.weight(1f)
                    )

                    CampoTexto(
                        label = "IVA",
                        value = "$ ${"%.2f".format(ivaCalculado)}",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEFF7EF)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Total *",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "$ ${"%.2f".format(totalCalculado)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoSelector(
                        label = "Método de pago *",
                        value = metodoPago,
                        modifier = Modifier.weight(1f)
                    )

                    CampoSelector(
                        label = "Forma de pago",
                        value = formaPago,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. EVIDENCIA / COMPROBANTES
            TarjetaSeccion(
                titulo = "Evidencia / Comprobantes",
                icono = Icons.Default.AttachFile
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotonAdjunto(
                        texto = "Tomar foto",
                        icono = Icons.Default.CameraAlt,
                        modifier = Modifier.weight(1f)
                    )

                    BotonAdjunto(
                        texto = "Subir PDF",
                        icono = Icons.Default.Description,
                        modifier = Modifier.weight(1f)
                    )

                    BotonAdjunto(
                        texto = "Adjuntar archivo",
                        subtitulo = "Máx. 10 MB",
                        icono = Icons.Default.AttachFile,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. OBSERVACIONES
            TarjetaSeccion(
                titulo = "Observaciones",
                icono = Icons.Default.Edit
            ) {
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text("Agrega notas u observaciones (opcional)")
                    },
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${observaciones.length}/300",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            // 5. RELACIONADO CON
            TarjetaSeccion(
                titulo = "Relacionado con (opcional)",
                icono = Icons.Default.Link
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoSelector(
                        label = "Proyecto",
                        value = proyecto,
                        modifier = Modifier.weight(1f)
                    )
                    CampoSelector(
                        label = "Cotización",
                        value = cotizacion,
                        modifier = Modifier.weight(1f)
                    )
                    CampoSelector(
                        label = "Cliente",
                        value = cliente,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 6. BOTONES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancelar")
                }

                Button(
                    onClick = onGuardar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Guardar Gasto")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TarjetaSeccion(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF424242),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            contenido()
        }
    }
}

@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        placeholder = {
            if (placeholder.isNotEmpty()) Text(placeholder)
        },
        readOnly = readOnly,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = leadingIcon
    )
}

@Composable
fun CampoSelector(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        label = { Text(label) },
        readOnly = true,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        trailingIcon = {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Desplegar")
        }
    )
}

@Composable
fun BotonAdjunto(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    subtitulo: String = ""
) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(82.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, contentDescription = null)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium
            )
            if (subtitulo.isNotEmpty()) {
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
