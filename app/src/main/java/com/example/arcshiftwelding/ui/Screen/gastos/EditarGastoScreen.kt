package com.example.arcshiftwelding.ui.gastos

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
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.Screen.gastos.TarjetaDetalleGasto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarGastoScreen(
    navController: NavController,
    gastoId: Int?
) {
    var concepto by remember { mutableStateOf("Compra de material") }
    var categoria by remember { mutableStateOf("Materiales") }
    var fecha by remember { mutableStateOf("19/05/2026") }
    var proveedor by remember { mutableStateOf("Aceros del Norte") }

    var subtotal by remember { mutableStateOf("2758.62") }
    var ivaPorcentaje by remember { mutableStateOf("16") }
    var metodoPago by remember { mutableStateOf("Efectivo") }
    var formaPago by remember { mutableStateOf("Contado") }

    var telefonoProveedor by remember { mutableStateOf("614 123 4567") }
    var correoProveedor by remember { mutableStateOf("ventas@acerosnorte.com") }
    var rfcProveedor by remember { mutableStateOf("ACN980312K7") }

    var observaciones by remember {
        mutableStateOf("Compra para fabricación de estructura metálica proyecto Cliente XYZ.")
    }

    var proyecto by remember { mutableStateOf("Estructura Nave Industrial") }
    var cotizacion by remember { mutableStateOf("COT-0456") }
    var cliente by remember { mutableStateOf("Cliente XYZ") }

    val subtotalValor = subtotal.toDoubleOrNull() ?: 0.0
    val ivaValor = ivaPorcentaje.toDoubleOrNull() ?: 0.0
    val ivaCalculado = subtotalValor * (ivaValor / 100.0)
    val totalCalculado = subtotalValor + ivaCalculado

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
                    text = "Editar Gasto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )



            }

        },
        contentWindowInsets = WindowInsets(0),
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

            TarjetaDetalleGasto(
                titulo = "Información general",
                icono = Icons.Default.Info
            ) {
                CampoTextoEditar(
                    label = "Concepto *",
                    value = concepto,
                    onValueChange = { concepto = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoSelectorEditar(
                        label = "Categoría *",
                        value = categoria,
                        modifier = Modifier.weight(1f)
                    )

                    CampoTextoEditar(
                        label = "Fecha *",
                        value = fecha,
                        onValueChange = { fecha = it },
                        modifier = Modifier.weight(1f),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CampoSelectorEditar(
                    label = "Proveedor *",
                    value = proveedor,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TarjetaDetalleGasto(
                titulo = "Información financiera",
                icono = Icons.Default.AttachMoney
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoTextoEditar(
                        label = "Subtotal *",
                        value = subtotal,
                        onValueChange = { subtotal = it },
                        modifier = Modifier.weight(1f)
                    )

                    CampoSelectorEditar(
                        label = "IVA (%)",
                        value = ivaPorcentaje,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CampoTextoEditar(
                    label = "IVA calculado",
                    value = "$ ${"%.2f".format(ivaCalculado)}",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Total",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.labelMedium
                        )

                        Text(
                            text = "$ ${"%.2f".format(totalCalculado)}",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampoSelectorEditar(
                        label = "Método de pago *",
                        value = metodoPago,
                        modifier = Modifier.weight(1f)
                    )

                    CampoSelectorEditar(
                        label = "Forma de pago",
                        value = formaPago,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            TarjetaDetalleGasto(
                titulo = "Proveedor",
                icono = Icons.Default.Business
            ) {
                CampoTextoEditar(
                    label = "Teléfono",
                    value = telefonoProveedor,
                    onValueChange = { telefonoProveedor = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoTextoEditar(
                    label = "Correo",
                    value = correoProveedor,
                    onValueChange = { correoProveedor = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoTextoEditar(
                    label = "RFC",
                    value = rfcProveedor,
                    onValueChange = { rfcProveedor = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TarjetaDetalleGasto(
                titulo = "Factura / Comprobante",
                icono = Icons.Default.AttachFile
            ) {
                Text(
                    text = "Archivos actuales",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                ArchivoEditableFactura(
                    nombre = "FACTURA_1005.pdf",
                    peso = "522 KB"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ArchivoEditableFactura(
                    nombre = "TICKET_250520.jpg",
                    peso = "186 KB"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cambiar PDF")
                    }

                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Nueva foto")
                    }
                }
            }

            TarjetaDetalleGasto(
                titulo = "Observaciones",
                icono = Icons.Default.Edit
            ) {
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = {
                        if (it.length <= 300) {
                            observaciones = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = {
                        Text("Agrega observaciones")
                    },
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

            TarjetaDetalleGasto(
                titulo = "Relacionado con",
                icono = Icons.Default.Link
            ) {
                CampoSelectorEditar(
                    label = "Proyecto",
                    value = proyecto,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoSelectorEditar(
                    label = "Cotización",
                    value = cotizacion,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CampoSelectorEditar(
                    label = "Cliente",
                    value = cliente,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        /*
                            Aquí después llamas al ViewModel:

                            viewModel.actualizarGasto(
                                gastoId = gastoId,
                                concepto = concepto,
                                categoria = categoria,
                                fecha = fecha,
                                proveedor = proveedor,
                                subtotal = subtotalValor,
                                iva = ivaCalculado,
                                total = totalCalculado,
                                metodoPago = metodoPago,
                                formaPago = formaPago,
                                observaciones = observaciones
                            )

                            navController.popBackStack()
                        */
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Actualizar")
                }
            }

        }
    }
}

@Composable
fun CampoTextoEditar(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = {
            Text(label)
        },
        readOnly = readOnly,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        leadingIcon = leadingIcon
    )
}

@Composable
fun CampoSelectorEditar(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = modifier,
        label = {
            Text(label)
        },
        readOnly = true,
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Seleccionar"
            )
        }
    )
}

@Composable
fun ArchivoEditableFactura(
    nombre: String,
    peso: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = peso,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = "Ver archivo"
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar archivo",
                    tint = Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
fun SeccionAccionesRapidasGasto(
    onEditarClick: () -> Unit
) {
    TarjetaDetalleGasto(
        titulo = "Acciones rápidas",
        icono = Icons.Default.Build
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BotonAccionDetalleGasto(
                texto = "Editar",
                icono = Icons.Default.Edit,
                color = Color(0xFF2563EB),
                modifier = Modifier.weight(1f),
                onClick = onEditarClick
            )

            BotonAccionDetalleGasto(
                texto = "Duplicar",
                icono = Icons.Default.ContentCopy,
                color = Color(0xFF374151),
                modifier = Modifier.weight(1f),
                onClick = { }
            )

            BotonAccionDetalleGasto(
                texto = "Descargar PDF",
                icono = Icons.Default.Download,
                color = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f),
                onClick = { }
            )

            BotonAccionDetalleGasto(
                texto = "Eliminar",
                icono = Icons.Default.Delete,
                color = Color(0xFFDC2626),
                modifier = Modifier.weight(1f),
                onClick = { }
            )
        }
    }
}

@Composable
fun BotonAccionDetalleGasto(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}