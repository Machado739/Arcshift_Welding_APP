package com.example.arcshiftwelding.ui.Screen.ingresos

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleIngresoScreen(
    navController: NavController,
    ingresoId: Int = 0
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
                    text = "Detalle de Ingreso",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                      //  navController.navigate(AppRoutes.editarIngreso(ingresoId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Ingreso"
                    )
                }
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaPrincipalIngreso()

            SeccionDetalleInformacionGeneralIngreso()

            SeccionDetalleClienteIngreso()

            SeccionDetalleInformacionFinancieraIngreso()

            SeccionDetalleComprobanteIngreso()

            SeccionDetalleObservacionesIngreso()

            SeccionDetalleRelacionadoIngreso()

            SeccionHistorialPagosIngreso()

            SeccionAccionesRapidasIngreso(
                onEditar = {
                  //  navController.navigate(AppRoutes.editarIngreso(ingresoId = ingresoId))
                },
                onEnviarFactura = {
                    // Pendiente: enviar factura
                },
                onDescargarPDF = {
                    // Pendiente: descargar PDF
                },
                onEliminar = {
                 //   navController.navigate(AppRoutes.eliminarIngreso(ingresoId = ingresoId))
                }
            )
        }
    }
}

@Composable
fun TarjetaPrincipalIngreso() {
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
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Pago por fabricación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "\$ 15,080.00",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                Text(
                    text = "Cliente: Constructora del Bajío",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DatoIconoPequenoIngreso(
                        icono = Icons.Default.DateRange,
                        texto = "19/05/2026"
                    )

                    DatoIconoPequenoIngreso(
                        icono = Icons.Default.Payment,
                        texto = "Transferencia"
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Más opciones"
                    )
                }

                AssistChip(
                    onClick = { },
                    label = {
                        Text("Pagado")
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

@Composable
fun SeccionDetalleInformacionGeneralIngreso() {
    TarjetaDetalleIngreso(
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
                ItemDatoDetalleIngreso(
                    titulo = "Concepto",
                    valor = "Pago por fabricación"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ItemDatoDetalleIngreso(
                    titulo = "Trabajo",
                    valor = "Estructura metálica"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ItemDatoDetalleIngreso(
                    titulo = "Registrado por",
                    valor = "Administrador"
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                ItemDatoDetalleIngreso(
                    titulo = "Folio",
                    valor = "FACT-0258"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ItemDatoDetalleIngreso(
                    titulo = "Método de pago",
                    valor = "Transferencia"
                )

                Spacer(modifier = Modifier.height(10.dp))

                ItemDatoDetalleIngreso(
                    titulo = "Estado",
                    valor = "Pagado"
                )
            }
        }
    }
}

@Composable
fun SeccionDetalleClienteIngreso() {
    TarjetaDetalleIngreso(
        titulo = "Cliente",
        icono = Icons.Default.Person
    ) {
        ItemDatoDetalleIngreso(
            titulo = "Constructora del Bajío S.A. de C.V.",
            valor = ""
        )

        Spacer(modifier = Modifier.height(8.dp))

        ItemDatoDetalleIngreso(
            titulo = "Contacto:",
            valor = "Ing. Juan Pérez"
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoDetalleIngreso(
            titulo = "Teléfono:",
            valor = "477 123 4567"
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoDetalleIngreso(
            titulo = "Correo:",
            valor = "jperez@cbajio.com"
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoDetalleIngreso(
            titulo = "RFC:",
            valor = "CDB2105314K7"
        )
    }
}

@Composable
fun SeccionDetalleInformacionFinancieraIngreso() {
    TarjetaDetalleIngreso(
        titulo = "Información financiera",
        icono = Icons.Default.AttachMoney
    ) {
        FilaMontoDetalleIngreso(
            titulo = "Subtotal",
            valor = "\$ 12,982.76"
        )

        Spacer(modifier = Modifier.height(8.dp))

        FilaMontoDetalleIngreso(
            titulo = "IVA (16%)",
            valor = "\$ 2,077.24"
        )

        Divider(
            modifier = Modifier.padding(vertical = 8.dp)
        )

        FilaMontoDetalleIngreso(
            titulo = "Total",
            valor = "\$ 15,080.00",
            destacar = true
        )
    }
}

@Composable
fun SeccionDetalleComprobanteIngreso() {
    TarjetaDetalleIngreso(
        titulo = "Comprobante / Evidencia",
        icono = Icons.Default.AttachFile
    ) {
        ArchivoComprobanteIngresoCard(
            nombre = "FACTURA_FACT-0258.pdf",
            peso = "156 KB",
            icono = Icons.Default.PictureAsPdf
        )
    }
}

@Composable
fun SeccionDetalleObservacionesIngreso() {
    TarjetaDetalleIngreso(
        titulo = "Observaciones",
        icono = Icons.Default.Edit
    ) {
        Text(
            text = "Pago correspondiente al 50% del proyecto de fabricación de estructura metálica.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SeccionDetalleRelacionadoIngreso() {
    TarjetaDetalleIngreso(
        titulo = "Relacionado con",
        icono = Icons.Default.Link
    ) {
        ItemDatoConLinkIngreso(
            titulo = "Cotización:",
            valor = "COT-0148"
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoConLinkIngreso(
            titulo = "Orden de trabajo:",
            valor = "OT-0095"
        )

        Spacer(modifier = Modifier.height(6.dp))

        ItemDatoConLinkIngreso(
            titulo = "Proyecto:",
            valor = "Nave Industrial"
        )
    }
}

@Composable
fun SeccionHistorialPagosIngreso() {
    TarjetaDetalleIngreso(
        titulo = "Historial de pagos",
        icono = Icons.Default.History
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFAFAFA)
            ),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Fecha",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.4f)
                    )

                    Text(
                        text = "Monto",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "Estado",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "19/05/2026",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "Pago inicial",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(1.4f)
                    )

                    Text(
                        text = "\$ 15,080.00",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    AssistChip(
                        onClick = { },
                        label = {
                            Text("Pagado")
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFFDFF3E3),
                            labelColor = Color(0xFF2E7D32)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SeccionAccionesRapidasIngreso(
    onEditar: () -> Unit,
    onEnviarFactura: () -> Unit,
    onDescargarPDF: () -> Unit,
    onEliminar: () -> Unit
) {
    TarjetaDetalleIngreso(
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
                texto = "Enviar",
                icono = Icons.Default.Send,
                onClick = onEnviarFactura,
                modifier = Modifier.weight(1f),
                iconTint = Color(0xFF2563EB)
            )

            BotonAccionRapida(
                texto = "PDF",
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
fun TarjetaDetalleIngreso(
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
fun ItemDatoDetalleIngreso(
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

@Composable
fun DatoIconoPequenoIngreso(
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
fun FilaMontoDetalleIngreso(
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
fun ItemDatoConLinkIngreso(
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
fun ArchivoComprobanteIngresoCard(
    nombre: String,
    peso: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(95.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAFAFA)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                tint = Color(0xFFE53935)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Text(
                    text = "PDF · $peso",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.RemoveRedEye,
                contentDescription = "Ver archivo",
                modifier = Modifier.size(18.dp),
                tint = Color.DarkGray
            )
        }
    }
}
@Composable
fun BotonAccionRapida(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = Color(0xFF2563EB)
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = iconTint
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = iconTint,
                maxLines = 1
            )
        }
    }
}