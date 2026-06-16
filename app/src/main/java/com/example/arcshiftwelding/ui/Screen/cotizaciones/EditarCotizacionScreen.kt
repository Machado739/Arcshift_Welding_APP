package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes

@Composable
fun EditarCotizacionScreen(
    navController: NavController,
    cotizacionId: Int
) {
    var cliente by remember { mutableStateOf("Constructora del Bajío S.A. de C.V.") }
    var proyecto by remember { mutableStateOf("Nave Industrial") }
    var fecha by remember { mutableStateOf("19/05/2026") }
    var vigencia by remember { mutableStateOf("02/06/2026") }
    var folio by remember { mutableStateOf("COT-00025") }
    var descripcion by remember {
        mutableStateOf("Fabricación e instalación de estructura metálica para nave industrial.")
    }

    var descuento by remember { mutableStateOf("0") }
    var iva by remember { mutableStateOf("16") }
    var anticipo by remember { mutableStateOf("50") }
    var observaciones by remember {
        mutableStateOf("Los precios incluyen materiales, mano de obra y acabado final. Tiempo estimado de entrega: 15 días hábiles.")
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(
                start = 8.dp,
                top = 0.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            HeaderEditarCotizacion(navController = navController)
        }

        item {
            CardAvisoEditarCotizacion()
        }

        item {
            SeccionInformacionGeneralEditarCotizacion(
                cliente = cliente,
                onClienteChange = { cliente = it },
                proyecto = proyecto,
                onProyectoChange = { proyecto = it },
                fecha = fecha,
                onFechaChange = { fecha = it },
                vigencia = vigencia,
                onVigenciaChange = { vigencia = it },
                folio = folio,
                onFolioChange = { folio = it },
                descripcion = descripcion,
                onDescripcionChange = { descripcion = it }
            )
        }

        item {
            SeccionConceptosEditarCotizacion()
        }

        item {
            SeccionResumenEditarCotizacion(
                descuento = descuento,
                onDescuentoChange = { descuento = it },
                iva = iva,
                onIvaChange = { iva = it },
                anticipo = anticipo,
                onAnticipoChange = { anticipo = it }
            )
        }

        item {
            SeccionArchivosEditarCotizacion()
        }

        item {
            SeccionObservacionesEditarCotizacion(
                observaciones = observaciones,
                onObservacionesChange = { observaciones = it }
            )
        }

        item {
            BotonesEditarCotizacion(
                onCancelarClick = {
                    navController.popBackStack()
                },
                onActualizarClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun HeaderEditarCotizacion(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
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
            text = "Editar Cotización",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones"
            )
        }

        TextButton(
            onClick = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        ) {
            Text(
                text = "Log\nOut",
                fontSize = 9.sp,
                lineHeight = 10.sp
            )
        }
    }
}

@Composable
fun CardAvisoEditarCotizacion() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFBEB)
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
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = "Modificación de cotización",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )

                Text(
                    text = "Revisa los datos antes de guardar los cambios.",
                    fontSize = 10.sp,
                    color = Color(0xFF92400E)
                )
            }
        }
    }
}

@Composable
fun SeccionInformacionGeneralEditarCotizacion(
    cliente: String,
    onClienteChange: (String) -> Unit,
    proyecto: String,
    onProyectoChange: (String) -> Unit,
    fecha: String,
    onFechaChange: (String) -> Unit,
    vigencia: String,
    onVigenciaChange: (String) -> Unit,
    folio: String,
    onFolioChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit
) {
    CardSeccionEditarCotizacion(
        titulo = "Información general",
        icono = Icons.Default.Info
    ) {
        CampoEditarCotizacion(
            titulo = "Cliente *",
            valor = cliente,
            placeholder = "Seleccionar cliente",
            onValueChange = onClienteChange,
            trailingIcon = Icons.Default.KeyboardArrowDown
        )

        CampoEditarCotizacion(
            titulo = "Proyecto (opcional)",
            valor = proyecto,
            placeholder = "Seleccionar proyecto",
            onValueChange = onProyectoChange,
            trailingIcon = Icons.Default.KeyboardArrowDown
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoEditarCotizacion(
                titulo = "Fecha *",
                valor = fecha,
                placeholder = "Fecha",
                onValueChange = onFechaChange,
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.CalendarMonth
            )

            CampoEditarCotizacion(
                titulo = "Vigencia *",
                valor = vigencia,
                placeholder = "Vigencia",
                onValueChange = onVigenciaChange,
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.CalendarMonth
            )
        }

        CampoEditarCotizacion(
            titulo = "Folio / Número",
            valor = folio,
            placeholder = "COT-00025",
            onValueChange = onFolioChange
        )

        CampoTextoLargoEditarCotizacion(
            titulo = "Descripción del trabajo *",
            valor = descripcion,
            placeholder = "Describe el trabajo o proyecto que se va a cotizar...",
            onValueChange = onDescripcionChange
        )
    }
}

@Composable
fun SeccionConceptosEditarCotizacion() {
    CardSeccionEditarCotizacion(
        titulo = "Conceptos",
        icono = Icons.Default.FormatListBulleted
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TabEditarCotizacion(
                texto = "Materiales",
                seleccionado = true,
                modifier = Modifier.weight(1f)
            )

            TabEditarCotizacion(
                texto = "Mano de obra",
                seleccionado = false,
                modifier = Modifier.weight(1f)
            )

            TabEditarCotizacion(
                texto = "Gastos adicionales",
                seleccionado = false,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        EncabezadoConceptosEditarCotizacion()

        ConceptoEditarCotizacionItem(
            concepto = "PTR 2x2 Cal. 14",
            cantidad = "12",
            unidad = "Pza",
            precio = "$450.00",
            importe = "$5,400.00"
        )

        ConceptoEditarCotizacionItem(
            concepto = "Soldadura",
            cantidad = "1",
            unidad = "Servicio",
            precio = "$2,000.00",
            importe = "$2,000.00"
        )

        ConceptoEditarCotizacionItem(
            concepto = "Pintura anticorrosiva",
            cantidad = "1",
            unidad = "Servicio",
            precio = "$1,500.00",
            importe = "$1,500.00"
        )

        TextButton(
            onClick = { }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF16A34A)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Agregar concepto",
                fontSize = 11.sp,
                color = Color(0xFF16A34A)
            )
        }
    }
}

@Composable
fun TabEditarCotizacion(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .background(
                color = if (seleccionado) Color.White else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 9.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            color = if (seleccionado) Color(0xFF15803D) else Color.Gray,
            maxLines = 1
        )
    }
}


@Composable
fun EncabezadoConceptosEditarCotizacion() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Concepto",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1.3f)
        )

        Text(
            text = "Cant.",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.5f)
        )

        Text(
            text = "Unidad",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.7f)
        )

        Text(
            text = "Precio",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.8f)
        )

        Text(
            text = "Importe",
            fontSize = 8.sp,
            color = Color.Gray,
            modifier = Modifier.weight(0.8f)
        )

        Spacer(modifier = Modifier.width(24.dp))
    }

    Divider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = Color(0xFFE2E8F0)
    )
}

@Composable
fun ConceptoEditarCotizacionItem(
    concepto: String,
    cantidad: String,
    unidad: String,
    precio: String,
    importe: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CajaTextoEditarCotizacion(
            texto = concepto,
            modifier = Modifier.weight(1.3f)
        )

        CajaTextoEditarCotizacion(
            texto = cantidad,
            modifier = Modifier.weight(0.5f)
        )

        CajaTextoEditarCotizacion(
            texto = unidad,
            modifier = Modifier.weight(0.7f)
        )

        CajaTextoEditarCotizacion(
            texto = precio,
            modifier = Modifier.weight(0.8f)
        )

        CajaTextoEditarCotizacion(
            texto = importe,
            modifier = Modifier.weight(0.8f)
        )

        IconButton(
            onClick = { },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Eliminar",
                modifier = Modifier.size(15.dp),
                tint = Color(0xFFDC2626)
            )
        }
    }
}

@Composable
fun CajaTextoEditarCotizacion(
    texto: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(28.dp)
            .padding(horizontal = 2.dp)
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 8.sp,
            color = Color.Black,
            maxLines = 1
        )
    }
}

@Composable
fun SeccionResumenEditarCotizacion(
    descuento: String,
    onDescuentoChange: (String) -> Unit,
    iva: String,
    onIvaChange: (String) -> Unit,
    anticipo: String,
    onAnticipoChange: (String) -> Unit
) {
    CardSeccionEditarCotizacion(
        titulo = "Resumen de la cotización",
        icono = Icons.Default.ReceiptLong
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CampoEditarCotizacion(
                    titulo = "Subtotal",
                    valor = "$8,900.00",
                    placeholder = "",
                    onValueChange = { }
                )

                CampoEditarCotizacion(
                    titulo = "Anticipo requerido (%)",
                    valor = anticipo,
                    placeholder = "50",
                    onValueChange = onAnticipoChange
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                CampoEditarCotizacion(
                    titulo = "Descuento (%)",
                    valor = descuento,
                    placeholder = "0",
                    onValueChange = onDescuentoChange
                )

                CampoEditarCotizacion(
                    titulo = "IVA (%)",
                    valor = iva,
                    placeholder = "16",
                    onValueChange = onIvaChange
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Total",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Text(
                    text = "$10,324.00",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF16A34A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFEAF7EE),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Anticipo sugerido",
                            fontSize = 8.sp,
                            color = Color(0xFF15803D)
                        )

                        Text(
                            text = "$5,162.00",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Saldo restante",
                    fontSize = 8.sp,
                    color = Color.Gray
                )

                Text(
                    text = "$5,162.00",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun SeccionArchivosEditarCotizacion() {
    CardSeccionEditarCotizacion(
        titulo = "Archivos adjuntos",
        icono = Icons.Default.AttachFile
    ) {
        ArchivoEditarCotizacionItem(
            nombre = "plano_estructura.pdf",
            detalle = "245 KB",
            icono = Icons.Default.PictureAsPdf,
            color = Color(0xFFDC2626),
            fondo = Color(0xFFFEE2E2)
        )

        Spacer(modifier = Modifier.height(6.dp))

        ArchivoEditarCotizacionItem(
            nombre = "referencia.jpg",
            detalle = "1.2 MB",
            icono = Icons.Default.Image,
            color = Color(0xFF2563EB),
            fondo = Color(0xFFEFF6FF)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BotonArchivoEditarCotizacion(
                texto = "Agregar imagen",
                icono = Icons.Default.Image,
                modifier = Modifier.weight(1f)
            )

            BotonArchivoEditarCotizacion(
                texto = "Adjuntar archivo",
                icono = Icons.Default.AttachFile,
                modifier = Modifier.weight(1f)
            )

            BotonArchivoEditarCotizacion(
                texto = "Subir PDF",
                icono = Icons.Default.Description,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ArchivoEditarCotizacionItem(
    nombre: String,
    detalle: String,
    icono: ImageVector,
    color: Color,
    fondo: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(fondo, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = nombre,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1
            )

            Text(
                text = detalle,
                fontSize = 8.sp,
                color = Color.Gray
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier.size(26.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Eliminar archivo",
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
fun BotonArchivoEditarCotizacion(
    texto: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = { },
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = texto,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF334155)
            )

            Text(
                text = texto,
                fontSize = 8.sp,
                color = Color.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SeccionObservacionesEditarCotizacion(
    observaciones: String,
    onObservacionesChange: (String) -> Unit
) {
    CardSeccionEditarCotizacion(
        titulo = "Observaciones",
        icono = Icons.Default.Search
    ) {
        CampoTextoLargoEditarCotizacion(
            titulo = "Observaciones opcional",
            valor = observaciones,
            placeholder = "Agrega notas u observaciones adicionales...",
            onValueChange = onObservacionesChange
        )
    }
}

@Composable
fun BotonesEditarCotizacion(
    onCancelarClick: () -> Unit,
    onActualizarClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCancelarClick,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Cancelar",
                fontSize = 12.sp
            )
        }

        Button(
            onClick = onActualizarClick,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Actualizar",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CardSeccionEditarCotizacion(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF334155),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            contenido()
        }
    }
}

@Composable
fun CampoEditarCotizacion(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    Column(
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 10.sp
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
            trailingIcon = if (trailingIcon != null) {
                {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp
            ),
            shape = RoundedCornerShape(7.dp)
        )
    }
}

@Composable
fun CampoTextoLargoEditarCotizacion(
    titulo: String,
    valor: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 10.sp
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp
            ),
            shape = RoundedCornerShape(7.dp),
            maxLines = 3
        )
    }
}