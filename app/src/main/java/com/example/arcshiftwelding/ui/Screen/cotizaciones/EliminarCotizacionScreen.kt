package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.theme.arcshiftColors

data class CotizacionEliminarUI(
    val id: Int,
    val folio: String,
    val cliente: String,
    val trabajo: String,
    val total: String,
    val estado: String,
    val fecha: String,
    val vigencia: String,
    val conceptos: String
)


@Composable
fun EliminarCotizacionScreen(
    navController: NavController,
    cotizacionId: Int,
    viewModel: CotizacionesViewModel
) {
    val cotizacionCompleta by viewModel
        .obtenerCotizacionCompleta(cotizacionId)
        .collectAsState(initial = null)

    if (cotizacionCompleta == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("Cotización no encontrada")
        }
        return
    }

    val cotizacionActual = cotizacionCompleta!!.cotizacion
    val clienteActual = cotizacionCompleta!!.cliente
    val detalles = cotizacionCompleta!!.detalles

    val cotizacion = CotizacionEliminarUI(
        id = cotizacionActual.id,
        folio = cotizacionActual.folio,
        cliente = clienteActual?.nombre ?: "Cliente no encontrado",
        trabajo = cotizacionActual.descripcionTrabajo,
        total = cotizacionActual.total.formatoMoneda(),
        estado = cotizacionActual.estado,
        fecha = cotizacionActual.fecha,
        vigencia = cotizacionActual.fecha,
        conceptos = if (detalles.isEmpty()) {
            "Sin conceptos registrados"
        } else {
            "${detalles.size} concepto(s) relacionado(s)"
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = 8.dp,
                top = 0.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            HeaderEliminarCotizacion(navController = navController)
        }

        item {
            CardAdvertenciaEliminarCotizacion()
        }

        item {
            CardResumenEliminarCotizacion(cotizacion = cotizacion)
        }

        item {
            SeccionDatosEliminarCotizacion(cotizacion = cotizacion)
        }

        item {
            SeccionConsecuenciasEliminarCotizacion()
        }

        item {
            BotonesEliminarCotizacion(
                onCancelarClick = {
                    navController.popBackStack()
                },
                onEliminarClick = {
                    viewModel.eliminarCotizacion(
                        cotizacionId = cotizacionId,
                        onFinish = {
                            navController.popBackStack()
                        }
                    )
                }
            )
        }
    }
}


@Composable
fun HeaderEliminarCotizacion(
    navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
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
            text = "Eliminar Cotización",
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
fun CardAdvertenciaEliminarCotizacion() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "¿Eliminar esta cotización?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Esta acción no se puede deshacer. Revisa la información antes de confirmar.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onErrorContainer,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun CardResumenEliminarCotizacion(
    cotizacion: CotizacionEliminarUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cotizacion.folio,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = cotizacion.estado,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.arcshiftColors.warning,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.arcshiftColors.warningContainer,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = cotizacion.trabajo,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cotizacion.total,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.arcshiftColors.success
                )

                Text(
                    text = cotizacion.cliente,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun SeccionDatosEliminarCotizacion(
    cotizacion: CotizacionEliminarUI
) {
    CardSeccionEliminarCotizacion(
        titulo = "Información de la cotización",
        icono = Icons.Default.Info
    ) {
        FilaDatoEliminarCotizacion(
            icono = Icons.Default.Person,
            titulo = "Cliente",
            valor = cotizacion.cliente
        )

        FilaDatoEliminarCotizacion(
            icono = Icons.Default.Description,
            titulo = "Trabajo",
            valor = cotizacion.trabajo
        )

        FilaDatoEliminarCotizacion(
            icono = Icons.Default.AttachMoney,
            titulo = "Total",
            valor = cotizacion.total,
            colorValor = MaterialTheme.arcshiftColors.success
        )

        FilaDatoEliminarCotizacion(
            icono = Icons.Default.CalendarMonth,
            titulo = "Fecha",
            valor = cotizacion.fecha
        )

        FilaDatoEliminarCotizacion(
            icono = Icons.Default.CalendarMonth,
            titulo = "Vigencia",
            valor = cotizacion.vigencia
        )

        FilaDatoEliminarCotizacion(
            icono = Icons.Default.Description,
            titulo = "Conceptos",
            valor = cotizacion.conceptos
        )
    }
}

@Composable
fun FilaDatoEliminarCotizacion(
    icono: ImageVector,
    titulo: String,
    valor: String,
    colorValor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(17.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = titulo,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(70.dp)
        )

        Text(
            text = valor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorValor,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
    }
}

@Composable
fun SeccionConsecuenciasEliminarCotizacion() {
    CardSeccionEliminarCotizacion(
        titulo = "Se eliminará la siguiente información",
        icono = Icons.Default.Warning
    ) {
        ItemConsecuenciaEliminarCotizacion(
            texto = "Datos generales de la cotización."
        )

        ItemConsecuenciaEliminarCotizacion(
            texto = "Conceptos, materiales, mano de obra y gastos adicionales."
        )

        ItemConsecuenciaEliminarCotizacion(
            texto = "Archivos adjuntos relacionados con la cotización."
        )

        ItemConsecuenciaEliminarCotizacion(
            texto = "Historial de cambios de esta cotización."
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Si esta cotización ya fue aprobada o convertida a ingreso, se recomienda revisar primero la información relacionada antes de eliminarla.",
            fontSize = 10.sp,
            color = MaterialTheme.arcshiftColors.onWarningContainer,
            lineHeight = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.arcshiftColors.warningContainer,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        )
    }
}

@Composable
fun ItemConsecuenciaEliminarCotizacion(
    texto: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = texto,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun BotonesEliminarCotizacion(
    onCancelarClick: () -> Unit,
    onEliminarClick: () -> Unit
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
                imageVector = Icons.Default.Cancel,
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
            onClick = onEliminarClick,
            modifier = Modifier
                .weight(1f)
                .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Eliminar",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun CardSeccionEliminarCotizacion(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                    tint = if (icono == Icons.Default.Warning) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(6.dp))

            contenido()
        }
    }
}