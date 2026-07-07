package com.example.arcshiftwelding.ui.Screen.gastos

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.Screen.inventario.BotonAccionRapida
import com.example.arcshiftwelding.ui.gastos.GastoUi
import com.example.arcshiftwelding.ui.gastos.GastosViewModel

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SeccionDetalleInformacionFinanciera(
                        gasto = gastoActual,
                        modifier = Modifier.weight(1f)
                    )

                    SeccionDetalleProveedor(
                        gasto = gastoActual,
                        modifier = Modifier.weight(1f)
                    )
                }

                SeccionDetalleEvidencia()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SeccionDetalleObservaciones(
                        gasto = gastoActual,
                        modifier = Modifier.weight(1f)
                    )

                    SeccionDetalleRelacionado(
                        gasto = gastoActual,
                        modifier = Modifier.weight(1f)
                    )
                }

                SeccionAccionesRapidasGasto(
                    onEditar = {
                        navController.navigate(AppRoutes.editarGasto(gastoActual.id))
                    },
                    onDescargarPDF = {
                        // Pendiente generar PDF
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
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ItemDatoDetalle(
                    titulo = "Concepto",
                    valor = gasto.concepto
                )

                Spacer(modifier = Modifier.height(10.dp))

                ItemDatoDetalle(
                    titulo = "Categoría",
                    valor = gasto.categoria
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                ItemDatoDetalle(
                    titulo = "Método de pago",
                    valor = gasto.metodoPago
                )

                Spacer(modifier = Modifier.height(10.dp))
/*
                ItemDatoDetalle(
                    titulo = "Forma de pago",
                    valor = gasto.formaPago.ifBlank { "No especificada" }
                )

 */
            }
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
fun SeccionDetalleEvidencia() {
    TarjetaDetalleGasto(
        titulo = "Evidencia / Comprobantes",
        icono = Icons.Default.AttachFile
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ArchivoComprobanteCard(
                nombre = "TICKET_250520.jpg",
                peso = "186 KB",
                icono = Icons.Default.Image,
                modifier = Modifier.weight(1f)
            )

            ArchivoComprobanteCard(
                nombre = "FACTURA_1005.pdf",
                peso = "522 KB",
                icono = Icons.Default.PictureAsPdf,
                modifier = Modifier.weight(1f)
            )

            ArchivoComprobanteCard(
                nombre = "NOTA_COMPRA.pdf",
                peso = "245 KB",
                icono = Icons.Default.Description,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ArchivoComprobanteCard(
    nombre: String,
    peso: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = nombre,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Text(
                text = peso,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Icon(
                imageVector = Icons.Default.RemoveRedEye,
                contentDescription = "Ver archivo",
                modifier = Modifier.size(14.dp),
                tint = Color.DarkGray
            )
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
        Text(
            text = gasto.observaciones.ifBlank { "Sin observaciones registradas." },
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
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
    Row {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SeccionAccionesRapidasGasto(
    onEditar: () -> Unit,
    onDescargarPDF: () -> Unit,
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
                texto = "Descargar PDF",
                icono = Icons.Default.AddCircleOutline,
                onClick = onDescargarPDF,
                modifier = Modifier.weight(1f),
                iconTint = Color(0xFF1B7F3A)
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
    valor: String
) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            fontWeight = FontWeight.SemiBold
        )

        if (valor.isNotEmpty()) {
            Text(
                text = valor,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}
