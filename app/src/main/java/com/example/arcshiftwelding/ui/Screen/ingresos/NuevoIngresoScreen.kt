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

@Composable
fun NuevoIngresoScreen(
    navController: NavController,
    viewModel: IngresosViewModel
) {
    val form by viewModel.formState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.limpiarFormulario()
    }

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
            SeccionIngresoInformacionGeneral(
                form = form,
                onChange = viewModel::actualizarFormulario
            )

            SeccionIngresoInformacionFinanciera(
                form = form,
                onChange = viewModel::actualizarFormulario
            )

            SeccionIngresoComprobante(
                form = form,
                onChange = viewModel::actualizarFormulario
            )

            SeccionIngresoRelacionado(
                form = form,
                onChange = viewModel::actualizarFormulario
            )

            BotonesNuevoIngreso(
                onCancelar = {
                    navController.popBackStack()
                },
                onGuardar = {
                    viewModel.guardarIngreso {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

@Composable
fun SeccionIngresoInformacionFinanciera(
    form: IngresoFormState,
    onChange: (IngresoFormState) -> Unit
) {
    val subtotalNumero = form.subtotal.aDouble()
    val ivaNumero = if (form.iva.isBlank()) {
        subtotalNumero * (form.ivaPorcentaje.aDouble() / 100)
    } else {
        form.iva.aDouble()
    }

    val totalNumero = subtotalNumero + ivaNumero
    val pendienteNumero = totalNumero - form.anticipo.aDouble()

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
                valor = form.subtotal,
                placeholder = "$ 0.00",
                onValueChange = {
                    onChange(form.copy(subtotal = it))
                },
                modifier = Modifier.weight(1f)
            )

            CampoTextoIngreso(
                titulo = "IVA (%)",
                valor = form.ivaPorcentaje,
                placeholder = "16",
                onValueChange = {
                    onChange(form.copy(ivaPorcentaje = it))
                },
                modifier = Modifier.weight(1f)
            )

            CampoTextoIngreso(
                titulo = "IVA",
                valor = form.iva,
                placeholder = "Auto",
                onValueChange = {
                    onChange(form.copy(iva = it))
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Anticipo / Pago recibido",
            valor = form.anticipo,
            placeholder = "$ 0.00",
            onValueChange = {
                onChange(form.copy(anticipo = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

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
                    text = "Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = totalNumero.formatoDinero(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                Text(
                    text = "Pendiente: ${pendienteNumero.formatoDinero()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTextoIngreso(
                titulo = "Método de pago *",
                valor = form.metodoPago,
                placeholder = "Efectivo, transferencia, tarjeta",
                onValueChange = {
                    onChange(form.copy(metodoPago = it))
                },
                modifier = Modifier.weight(1f)
            )

            CampoTextoIngreso(
                titulo = "Forma de pago",
                valor = form.formaPago,
                placeholder = "Contado / crédito",
                onValueChange = {
                    onChange(form.copy(formaPago = it))
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Referencia / Folio",
            valor = form.folio,
            placeholder = "Ej. FACT-001",
            onValueChange = {
                onChange(form.copy(folio = it))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SeccionIngresoComprobante(
    form: IngresoFormState,
    onChange: (IngresoFormState) -> Unit
) {
    TarjetaNuevoIngreso(
        titulo = "Comprobante / Evidencia",
        icono = Icons.Default.AttachFile
    ) {
        CampoTextoIngreso(
            titulo = "Observaciones opcional",
            valor = form.observaciones,
            placeholder = "Agrega notas u observaciones opcionales",
            onValueChange = {
                onChange(form.copy(observaciones = it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            singleLine = false
        )
    }
}

@Composable
fun SeccionIngresoRelacionado(
    form: IngresoFormState,
    onChange: (IngresoFormState) -> Unit
) {
    TarjetaNuevoIngreso(
        titulo = "Relacionado con opcional",
        icono = Icons.Default.Link
    ) {
        CampoTextoIngreso(
            titulo = "Cotización",
            valor = form.cotizacion,
            placeholder = "Ej. COT-001",
            onValueChange = {
                onChange(form.copy(cotizacion = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Orden de trabajo",
            valor = form.ordenTrabajo,
            placeholder = "Ej. OT-001",
            onValueChange = {
                onChange(form.copy(ordenTrabajo = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Proyecto",
            valor = form.proyecto,
            placeholder = "Nombre del proyecto",
            onValueChange = {
                onChange(form.copy(proyecto = it))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SeccionIngresoInformacionGeneral(
    form: IngresoFormState,
    onChange: (IngresoFormState) -> Unit
) {
    TarjetaNuevoIngreso(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        CampoTextoIngreso(
            titulo = "Concepto / Descripción *",
            valor = form.concepto,
            placeholder = "Ej. Pago por fabricación de estructura metálica",
            onValueChange = {
                onChange(form.copy(concepto = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CampoTextoIngreso(
            titulo = "Cliente *",
            valor = form.cliente,
            placeholder = "Nombre del cliente",
            onValueChange = {
                onChange(form.copy(cliente = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CampoTextoIngreso(
                titulo = "Fecha *",
                valor = form.fecha,
                placeholder = "dd/mm/aaaa",
                onValueChange = {
                    onChange(form.copy(fecha = it))
                },
                leadingIcon = Icons.Default.CalendarToday,
                modifier = Modifier.weight(1f)
            )

            CampoTextoIngreso(
                titulo = "Trabajo",
                valor = form.trabajo,
                placeholder = "Ej. Tejaban 6x4m",
                onValueChange = {
                    onChange(form.copy(trabajo = it))
                },
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