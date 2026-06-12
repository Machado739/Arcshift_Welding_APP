package com.example.arcshiftwelding.ui.Screen.inventario


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.arcshiftwelding.navigation.AppRoutes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productoId: Int
) {
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
                    text = "Detalle del Producto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        navController.navigate(AppRoutes.editarProducto(productoId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Producto"
                    )
                }
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
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            EncabezadoProductoDetalle()

            SeccionDetalleInformacionGeneral()


            SeccionDetalleInventario()
            SeccionDetalleCostos()

            SeccionMovimientosRecientes()

            SeccionAccionesRapidas(
                onEditar = {
                    navController.navigate(AppRoutes.editarProducto(productoId = 1))
                },
                onAgregarStock = {
                    navController.navigate(AppRoutes.reponerStock(productoId = 1))
                },
                onReportarSalida = {
                    navController.navigate(AppRoutes.reportarSalida(productoId = 1))
                },
                onEliminar = {
                    navController.navigate(AppRoutes.eliminarProducto(productoId = 1))
                }
            )

        }
    }
}

@Composable
fun EncabezadoProductoDetalle() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color(0xFFEDEDED),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "PTR 2\"x2\" Cal. 14",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Código: MAT-001",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Ubicación: MAT-001",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text("En Stock")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = Color(0xFF1B7F3A),
                        leadingIconContentColor = Color(0xFF1B7F3A)
                    )
                )

                OutlinedCard(
                    modifier = Modifier.width(82.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Stock actual",
                            style = MaterialTheme.typography.labelSmall
                        )

                        Text(
                            text = "10",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Piezas",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun DetalleCard(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable () -> Unit
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
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            contenido()
        }
    }
}
@Composable
fun SeccionDetalleInformacionGeneral() {
    DetalleCard(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoDetalle(
                    titulo = "Nombre del producto",
                    valor = "PTR 2\"x2\" Cal. 14"
                )

                CampoDetalle(
                    titulo = "Categoría",
                    valor = "Materiales"
                )

                CampoDetalle(
                    titulo = "Código / SKU",
                    valor = "MAT-001"
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoDetalle(
                    titulo = "Descripción",
                    valor = "Tubo cuadrado estructural de acero al carbón."
                )

                CampoDetalle(
                    titulo = "Unidad de medida",
                    valor = "Pieza"
                )
            }
        }
    }
}

@Composable
fun CampoDetalle(
    titulo: String,
    valor: String
) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF111827)
        )
    }
}

@Composable
fun SeccionDetalleInventario() {
    DetalleCard(
        titulo = "Inventario",
        icono = Icons.Default.Inventory2
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ItemInventarioDetalle(
                titulo = "Stock actual",
                valor = "10",
                subtitulo = "Piezas",
                modifier = Modifier.weight(1f)
            )

            SeparadorVertical()

            ItemInventarioDetalle(
                titulo = "Stock mínimo",
                valor = "5",
                subtitulo = "Piezas",
                modifier = Modifier.weight(1f)
            )

            SeparadorVertical()

            ItemInventarioDetalle(
                titulo = "Ubicación",
                valor = "MAT-001",
                subtitulo = "",
                modifier = Modifier.weight(1f)
            )

            SeparadorVertical()

            ItemInventarioDetalle(
                titulo = "Estado",
                valor = "En Stock",
                subtitulo = "",
                modifier = Modifier.weight(1f),
                mostrarIconoEstado = true
            )
        }
    }
}
@Composable
fun ItemInventarioDetalle(
    titulo: String,
    valor: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    mostrarIconoEstado: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (mostrarIconoEstado) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF1B7F3A)
                )

                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = valor,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (mostrarIconoEstado) Color(0xFF1B7F3A) else Color(0xFF111827)
            )
        }

        if (subtitulo.isNotEmpty()) {
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
fun SeparadorVertical() {
    Box(
        modifier = Modifier
            .height(48.dp)
            .width(1.dp)
            .background(Color(0xFFE5E7EB))
    )
}

@Composable
fun SeccionDetalleCostos() {
    DetalleCard(
        titulo = "Costos",
        icono = Icons.Default.AttachMoney
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoDetalle(
                titulo = "Costo unitario",
                valor = "$ 120.00"
            )

            Spacer(modifier = Modifier.weight(1f))

            CampoDetalle(
                titulo = "Costo total en inventario",
                valor = "$ 1,200.00"
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SeccionMovimientosRecientes() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Movimientos recientes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = {
                        // navController.navigate(Routes.MOVIMIENTOS_PRODUCTO)
                    }
                ) {
                    Text(
                        text = "Ver todo",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
            }

            Divider()

            Spacer(modifier = Modifier.height(6.dp))

            EncabezadoTablaMovimientos()

            MovimientoItem(
                fecha = "25/05/26",
                hora = "11:30",
                tipo = "Entrada",
                cantidad = "+ 5 piezas",
                usuario = "Admin",
                referencia = "OC-015",
                esEntrada = true
            )

            MovimientoItem(
                fecha = "24/05/26",
                hora = "16:45",
                tipo = "Salida",
                cantidad = "- 2 piezas",
                usuario = "Juan P.",
                referencia = "SAL-008",
                esEntrada = false
            )

            MovimientoItem(
                fecha = "20/05/26",
                hora = "09:10",
                tipo = "Entrada",
                cantidad = "+ 10 piezas",
                usuario = "Admin",
                referencia = "OC-012",
                esEntrada = true
            )
        }
    }
}

@Composable
fun EncabezadoTablaMovimientos() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        TextoTabla("Fecha", Modifier.weight(1.1f), esHeader = true)
        TextoTabla("Tipo", Modifier.weight(1f), esHeader = true)
        TextoTabla("Cantidad", Modifier.weight(1f), esHeader = true)
        TextoTabla("Usuario", Modifier.weight(1f), esHeader = true)
        TextoTabla("Referencia", Modifier.weight(1f), esHeader = true)
    }
}

@Composable
fun MovimientoItem(
    fecha: String,
    hora: String,
    tipo: String,
    cantidad: String,
    usuario: String,
    referencia: String,
    esEntrada: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1.1f)
        ) {
            Text(
                text = fecha,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF111827)
            )

            Text(
                text = hora,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        Text(
            text = tipo,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall,
            color = if (esEntrada) Color(0xFF1B7F3A) else Color(0xFFB42318),
            fontWeight = FontWeight.Medium
        )

        TextoTabla(cantidad, Modifier.weight(1f))
        TextoTabla(usuario, Modifier.weight(1f))
        TextoTabla(referencia, Modifier.weight(1f))
    }

    Divider(color = Color(0xFFF1F1F1))
}

@Composable
fun TextoTabla(
    texto: String,
    modifier: Modifier = Modifier,
    esHeader: Boolean = false
) {
    Text(
        text = texto,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = if (esHeader) Color.Gray else Color(0xFF111827),
        fontWeight = if (esHeader) FontWeight.Bold else FontWeight.Normal
    )
}
@Composable
fun SeccionAccionesRapidas(
    onEditar: () -> Unit,
    onAgregarStock: () -> Unit,
    onReportarSalida: () -> Unit,
    onEliminar: () -> Unit
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
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Acciones rápidas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BotonAccionRapida(
                    texto = "Editar\nProducto",
                    icono = Icons.Default.Edit,
                    onClick = onEditar,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionRapida(
                    texto = "Agregar\nStock",
                    icono = Icons.Default.AddCircleOutline,
                    onClick = onAgregarStock,
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFF1B7F3A)
                )

                BotonAccionRapida(
                    texto = "Reportar\nSalida",
                    icono = Icons.Default.RemoveCircleOutline,
                    onClick = onReportarSalida,
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFFB42318)
                )

                BotonAccionRapida(
                    texto = "Eliminar\nProducto",
                    icono = Icons.Default.Delete,
                    onClick = onEliminar,
                    modifier = Modifier.weight(1f),
                    iconTint = Color(0xFF111827)
                )
            }
        }
    }
}

@Composable
fun BotonAccionRapida(
    texto: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color(0xFF111827)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF111827)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
