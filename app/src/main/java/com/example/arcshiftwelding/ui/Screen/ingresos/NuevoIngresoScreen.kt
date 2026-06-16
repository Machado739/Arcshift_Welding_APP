package com.example.arcshiftwelding.ui.Screen.ingresos

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoIngresoScreen(
    navController: NavController
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
                    text = "Nuevo Ingreso",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones"
                    )
                }

                TextButton(onClick = { }) {
                    Text(
                        text = "Log\nOut",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black
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
            SeccionNuevoIngresoInformacionGeneral()

            SeccionNuevoIngresoInformacionFinanciera()

            SeccionNuevoIngresoComprobante()

            SeccionNuevoIngresoRelacionado()

            BotonesNuevoIngreso(
                onCancelar = {
                    navController.popBackStack()
                },
                onGuardar = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionNuevoIngresoInformacionGeneral() {
    var concepto by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("19/05/2026") }
    var cliente by remember { mutableStateOf("") }
    var proyecto by remember { mutableStateOf("") }

    TarjetaNuevoIngreso(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        CampoTextoIngreso(
            titulo = "Concepto / Descripción *",
            valor = concepto,
            placeholder = "Ej. Pago por fabricación de estructura metálica",
            onValueChange = { concepto = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoSeleccionIngreso(
                titulo = "Cliente *",
                valor = if (cliente.isEmpty()) "Seleccionar cliente" else cliente,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFF1F1F1),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo cliente"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTextoIngreso(
                titulo = "Fecha *",
                valor = fecha,
                placeholder = "Fecha",
                onValueChange = { fecha = it },
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )

            CampoSeleccionIngreso(
                titulo = "Proyecto opcional",
                valor = if (proyecto.isEmpty()) "Seleccionar proyecto" else proyecto,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionNuevoIngresoInformacionFinanciera() {
    var subtotal by remember { mutableStateOf("") }
    var ivaPorcentaje by remember { mutableStateOf("16") }
    var iva by remember { mutableStateOf("") }
    var metodoPago by remember { mutableStateOf("") }
    var formaPago by remember { mutableStateOf("Contado") }
    var folio by remember { mutableStateOf("") }

    TarjetaNuevoIngreso(
        titulo = "Información financiera",
        icono = Icons.Default.AttachMoney
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTextoIngreso(
                titulo = "Subtotal *",
                valor = subtotal,
                placeholder = "$ 0.00",
                onValueChange = { subtotal = it },
                modifier = Modifier.weight(1f)
            )

            CampoSeleccionIngreso(
                titulo = "IVA (%)",
                valor = ivaPorcentaje,
                modifier = Modifier.weight(1f)
            )

            CampoTextoIngreso(
                titulo = "IVA",
                valor = iva,
                placeholder = "$ 0.00",
                onValueChange = { iva = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F3E6)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Total *",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$ 0.00",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoSeleccionIngreso(
                titulo = "Método de pago *",
                valor = if (metodoPago.isEmpty()) "Seleccionar método" else metodoPago,
                modifier = Modifier.weight(1f)
            )

            CampoSeleccionIngreso(
                titulo = "Forma de pago",
                valor = formaPago,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Referencia / Folio opcional",
            valor = folio,
            placeholder = "Ej. FACT-001",
            onValueChange = { folio = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SeccionNuevoIngresoComprobante() {
    var observaciones by remember { mutableStateOf("") }

    TarjetaNuevoIngreso(
        titulo = "Comprobante / Evidencia",
        icono = Icons.Default.AttachFile
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BotonArchivoIngreso(
                titulo = "Generar factura",
                subtitulo = "Crear factura PDF",
                icono = Icons.Default.ReceiptLong,
                modifier = Modifier.weight(1f)
            )

            BotonArchivoIngreso(
                titulo = "Subir PDF",
                subtitulo = "Factura o recibo",
                icono = Icons.Default.Description,
                modifier = Modifier.weight(1f)
            )

            BotonArchivoIngreso(
                titulo = "Adjuntar archivo",
                subtitulo = "Máx. 10 MB",
                icono = Icons.Default.AttachFile,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Observaciones opcional",
            valor = observaciones,
            placeholder = "Agrega notas u observaciones opcionales",
            onValueChange = { observaciones = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            singleLine = false
        )
    }
}


@Composable
fun SeccionNuevoIngresoRelacionado() {
    TarjetaNuevoIngreso(
        titulo = "Relacionado con opcional",
        icono = Icons.Default.Link
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoSeleccionIngreso(
                titulo = "Cotización",
                valor = "Seleccionar",
                modifier = Modifier.weight(1f)
            )

            CampoSeleccionIngreso(
                titulo = "Orden de trabajo",
                valor = "Seleccionar",
                modifier = Modifier.weight(1f)
            )

            CampoSeleccionIngreso(
                titulo = "Cliente",
                valor = "Seleccionar",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BotonesNuevoIngreso(
    onCancelar: () -> Unit,
    onGuardar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onCancelar,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.DarkGray
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Cancelar",
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onGuardar,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B8F3A)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Guardar Ingreso",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TarjetaNuevoIngreso(
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
fun CampoTextoIngreso(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    singleLine: Boolean = true
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

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
            singleLine = singleLine,
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun CampoSeleccionIngreso(
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

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.DarkGray
            )
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun BotonArchivoIngreso(
    titulo: String,
    subtitulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(6.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFFFAFAFA),
            contentColor = Color.Black
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}