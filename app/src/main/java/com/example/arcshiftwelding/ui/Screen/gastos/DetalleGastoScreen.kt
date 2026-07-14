package com.example.arcshiftwelding.ui.Screen.gastos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.Screen.inventario.BotonAccionRapida
import com.example.arcshiftwelding.ui.viewmodel.GastosViewModel
import com.example.arcshiftwelding.utils.abrirComprobanteGasto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleGastoScreen(
    navController: NavController,
    gastoId: Int,
    viewModel: GastosViewModel
) {
    val gasto by viewModel.obtenerDetalleGasto(gastoId).collectAsState()

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
                    text = "Detalle de Gasto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        navController.navigate(AppRoutes.editarGasto(gastoId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Gasto"
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->

        if (gasto == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontró el gasto",
                    color = Color.Gray
                )
            }
        } else {
            val gastoActual = gasto!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaPrincipalGasto(
                    gasto = gastoActual
                )

                SeccionDetalleInformacionGeneral(
                    gasto = gastoActual
                )

                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (maxWidth < 350.dp) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SeccionDetalleInformacionFinanciera(
                                gasto = gastoActual
                            )

                            SeccionDetalleRelacionado(
                                gasto = gastoActual
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SeccionDetalleInformacionFinanciera(
                                gasto = gastoActual,
                                modifier = Modifier.weight(1f)
                            )

                            SeccionDetalleRelacionado(
                                gasto = gastoActual,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                SeccionDetalleEvidencia(gastoActual)

                SeccionDetalleObservaciones(
                    gasto = gastoActual
                )

                SeccionAccionesRapidasGasto(
                    onEditar = {
                        navController.navigate(AppRoutes.editarGasto(gastoActual.id))
                    },

                    onEliminar = {
                        navController.navigate(AppRoutes.eliminarGasto(gastoId = gastoActual.id))
                    }
                )
            }
        }
    }
}
@Composable
fun TarjetaPrincipalGasto(
    gasto: GastoUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                    .size(72.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoPorCategoriaGasto(gasto.categoria),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = gasto.concepto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$ ${String.format("%.2f", gasto.total)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Proveedor: ${gasto.proveedor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DatoIconoPequeno(
                        icono = Icons.Default.DateRange,
                        texto = gasto.fecha
                    )
                    /*
                                        DatoIconoPequeno(
                                            icono = Icons.Default.Payment,
                                            texto = gasto.metodoPago
                                        )

                     */
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {/*
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Más opciones"
                    )
                }*/

                AssistChip(
                    onClick = { },
                    label = {
                        Text("Registrado")
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color(0xFFDFF3E3),
                        labelColor = Color(0xFF2E7D32)
                    )
                )
            }
        }
    }
}

fun iconoPorCategoriaGasto(
    categoria: String
): androidx.compose.ui.graphics.vector.ImageVector {
    return when (categoria) {
        "Materiales" -> Icons.Default.ShoppingCart
        "Transporte" -> Icons.Default.LocalGasStation
        "Servicios" -> Icons.Default.Build
        "Nómina" -> Icons.Default.Person
        "Herramientas" -> Icons.Default.Handyman
        "Seguridad" -> Icons.Default.HealthAndSafety
        else -> Icons.Default.MoreHoriz
    }
}

@Composable
fun DatoIconoPequeno(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.DarkGray
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SeccionDetalleInformacionGeneral(
    gasto: GastoUi
) {
    TarjetaDetalleGasto(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ItemDatoDetalle(
                titulo = "Concepto",
                valor = gasto.concepto.ifBlank { "Sin concepto" },
                modifier = Modifier.weight(1f)
            )

            ItemDatoDetalle(
                titulo = "Método de pago",
                valor = gasto.metodoPago.ifBlank { "No especificado" },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Divider(color = Color(0xFFE2E8F0))

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ItemDatoDetalle(
                titulo = "Categoría",
                valor = gasto.categoria.ifBlank { "Sin categoría" },
                modifier = Modifier.weight(1f)
            )


            ItemDatoDetalle(
                titulo = "Proveedor",
                valor = gasto.proveedor.ifBlank { "Sin proveedor registrado" },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SeccionDetalleInformacionFinanciera(
    gasto: GastoUi,
    modifier: Modifier = Modifier
) {
    TarjetaDetalleGasto(
        titulo = "Información financiera",
        icono = Icons.Default.AttachMoney,
        modifier = modifier
    ) {
        FilaMontoDetalle(
            titulo = "Subtotal",
            valor = "$ ${String.format("%.2f", gasto.subtotal)}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        FilaMontoDetalle(
            titulo = "IVA (${String.format("%.0f", gasto.ivaPorcentaje)}%)",
            valor = "$ ${String.format("%.2f", gasto.iva)}"
        )

        Divider(
            modifier = Modifier.padding(vertical = 8.dp)
        )

        FilaMontoDetalle(
            titulo = "Total",
            valor = "$ ${String.format("%.2f", gasto.total)}",
            destacar = true
        )


    }
}


@Composable
fun FilaMontoDetalle(
    titulo: String,
    valor: String,
    destacar: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (destacar) Color(0xFF2E7D32) else Color.Black
        )
    }
}

@Composable
fun SeccionDetalleProveedor(
    gasto: GastoUi,
    modifier: Modifier = Modifier
) {
    TarjetaDetalleGasto(
        titulo = "Proveedor",
        icono = Icons.Default.Business,
        modifier = modifier
    ) {
        ItemDatoDetalle(
            titulo = "Nombre",
            valor = gasto.proveedor
        )
        /*
                Spacer(modifier = Modifier.height(8.dp))

                ItemDatoDetalle(
                    titulo = "Teléfono",
                    valor = gasto.telefonoProveedor.ifBlank { "No registrado" }
                )

                Spacer(modifier = Modifier.height(6.dp))

                ItemDatoDetalle(
                    titulo = "Correo",
                    valor = gasto.correoProveedor.ifBlank { "No registrado" }
                )

                Spacer(modifier = Modifier.height(6.dp))

                ItemDatoDetalle(
                    titulo = "RFC",
                    valor = gasto.rfcProveedor.ifBlank { "No registrado" }
                )
                */
    }
}

@Composable
fun SeccionDetalleEvidencia(gasto: GastoUi) {
    val context = LocalContext.current
    var errorApertura by remember { mutableStateOf(false) }

    TarjetaDetalleGasto(
        titulo = "Evidencia / Comprobantes (${gasto.comprobantes.size})",
        icono = Icons.Default.AttachFile
    ) {
        if (gasto.comprobantes.isEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.InsertDriveFile,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "No se adjuntó ningún comprobante.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            gasto.comprobantes.forEachIndexed { indice, comprobante ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            errorApertura = !abrirComprobanteGasto(
                                context = context,
                                comprobanteUri = comprobante.uri,
                                tipoComprobante = comprobante.tipo,
                                nombreComprobante = comprobante.nombre
                            )
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8FAFC)
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (comprobante.tipo) {
                                "PDF" -> Icons.Default.PictureAsPdf
                                "Imagen" -> Icons.Default.Image
                                else -> Icons.Default.InsertDriveFile
                            },
                            contentDescription = null,
                            modifier = Modifier.size(34.dp),
                            tint = when (comprobante.tipo) {
                                "PDF" -> Color(0xFFDC2626)
                                "Imagen" -> Color(0xFF2563EB)
                                else -> Color(0xFF475569)
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = comprobante.nombre.ifBlank {
                                    "Comprobante ${indice + 1}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2
                            )
                            Text(
                                text = comprobante.tipo.ifBlank { "Archivo" },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = "Abrir comprobante",
                            tint = Color.DarkGray
                        )
                    }
                }

                if (indice < gasto.comprobantes.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (errorApertura) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No fue posible abrir uno de los comprobantes en este dispositivo.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun SeccionDetalleObservaciones(
    gasto: GastoUi,
    modifier: Modifier = Modifier
) {
    TarjetaDetalleGasto(
        titulo = "Observaciones",
        icono = Icons.Default.Edit,
        modifier = modifier
    ) {
        val observaciones = gasto.observaciones.trim()

        Text(
            text = observaciones.ifBlank { "Sin observaciones registradas." },
            style = MaterialTheme.typography.bodyMedium,
            color = if (observaciones.isBlank()) Color.Gray else Color(0xFF334155),
            modifier = Modifier.fillMaxWidth(),
            softWrap = true
        )
    }
}

@Composable
fun SeccionDetalleRelacionado(
    gasto: GastoUi,
    modifier: Modifier = Modifier
) {
    TarjetaDetalleGasto(
        titulo = "Relacionado con",
        icono = Icons.Default.Link,
        modifier = modifier
    ) {
        ItemDatoConLink(
            titulo = "Proyecto:",
            valor = gasto.proyecto.ifBlank { "Sin proyecto" }
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoConLink(
            titulo = "Cotización:",
            valor = gasto.cotizacion.ifBlank { "Sin cotización" }
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoConLink(
            titulo = "Cliente:",
            valor = gasto.cliente.ifBlank { "Sin cliente" }
        )
    }
}

@Composable
fun ItemDatoConLink(
    titulo: String,
    valor: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo.removeSuffix(":"),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            color = if (valor.startsWith("Sin ")) {
                Color.Gray
            } else {
                Color(0xFF2563EB)
            },
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            softWrap = true
        )
    }
}

@Composable
fun SeccionAccionesRapidasGasto(
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    TarjetaDetalleGasto(
        titulo = "Acciones rápidas",
        icono = Icons.Default.Build
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BotonAccionRapida(
                texto = "Editar",
                icono = Icons.Default.Edit,
                onClick = onEditar,
                modifier = Modifier.weight(1f)
            )



            BotonAccionRapida(
                texto = "Eliminar",
                icono = Icons.Default.RemoveCircleOutline,
                onClick = onEliminar,
                modifier = Modifier.weight(1f),
                iconTint = Color(0xFFB42318)
            )
        }
    }
}

@Composable
fun TarjetaDetalleGasto(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
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
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            contenido()
        }
    }
}

@Composable
fun ItemDatoDetalle(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = valor.ifBlank { "No registrado" },
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            softWrap = true
        )
    }
}
