package com.example.arcshiftwelding.ui.Screen.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arcshiftwelding.notifications.NotificacionApp
import com.example.arcshiftwelding.notifications.PrioridadNotificacion
import com.example.arcshiftwelding.notifications.TipoNotificacion
import kotlinx.coroutines.delay
import com.example.arcshiftwelding.ui.theme.arcshiftColors

private const val TIEMPO_VISIBLE_PANEL_MS = 10_000L

/**
 * Campana con un panel emergente anclado al encabezado.
 *
 * El panel se cierra al volver a presionar la campana, tocar fuera de él o después
 * de unos segundos. DropdownMenu incluye una animación de entrada y salida con
 * desvanecimiento, por lo que no se necesita una pantalla independiente.
 */
@Composable
fun CampanaConPanelNotificaciones(
    notificaciones: List<NotificacionApp>,
    solicitudApertura: Int,
    onNotificacionClick: (NotificacionApp) -> Unit,
    onMarcarTodasComoLeidas: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by rememberSaveable { mutableStateOf(false) }
    var ultimaSolicitudAtendida by rememberSaveable { mutableIntStateOf(0) }
    var versionApertura by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(solicitudApertura) {
        if (solicitudApertura > ultimaSolicitudAtendida) {
            ultimaSolicitudAtendida = solicitudApertura
            expandido = true
            versionApertura++
        }
    }

    LaunchedEffect(expandido, versionApertura) {
        if (expandido) {
            delay(TIEMPO_VISIBLE_PANEL_MS)
            expandido = false
        }
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = {
                expandido = !expandido
                if (expandido) {
                    versionApertura++
                }
            }
        ) {
            BadgedBox(
                badge = {
                    if (notificaciones.isNotEmpty()) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (notificaciones.size > 99) {
                                    "99+"
                                } else {
                                    notificaciones.size.toString()
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (notificaciones.isNotEmpty()) {
                        Icons.Default.NotificationsActive
                    } else {
                        Icons.Default.NotificationsNone
                    },
                    contentDescription = if (notificaciones.isNotEmpty()) {
                        "${notificaciones.size} notificaciones pendientes"
                    } else {
                        "Sin notificaciones pendientes"
                    },
                    tint = if (notificaciones.isNotEmpty()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier
                .widthIn(min = 290.dp, max = 350.dp)
                .heightIn(max = 460.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            EncabezadoPanelNotificaciones(
                cantidad = notificaciones.size,
                onMarcarTodas = {
                    onMarcarTodasComoLeidas()
                    expandido = false
                }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            if (notificaciones.isEmpty()) {
                EstadoPanelSinNotificaciones()
            } else {
                notificaciones.forEachIndexed { indice, notificacion ->
                    ElementoPanelNotificacion(
                        notificacion = notificacion,
                        onClick = {
                            onNotificacionClick(notificacion)
                            expandido = false
                        }
                    )

                    if (indice < notificaciones.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EncabezadoPanelNotificaciones(
    cantidad: Int,
    onMarcarTodas: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = when (cantidad) {
                    0 -> "No tienes alertas pendientes"
                    1 -> "1 alerta pendiente"
                    else -> "$cantidad alertas pendientes"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (cantidad > 0) {
            TextButton(onClick = onMarcarTodas) {
                Text(
                    text = "Limpiar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ElementoPanelNotificacion(
    notificacion: NotificacionApp,
    onClick: () -> Unit
) {
    val estilo = estiloNotificacion(notificacion)

    DropdownMenuItem(
        onClick = onClick,
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = estilo.fondo,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = estilo.icono,
                    contentDescription = null,
                    tint = estilo.contenido,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = notificacion.titulo,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(
                                color = colorPrioridad(notificacion.prioridad),
                                shape = CircleShape
                            )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = notificacion.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (notificacion.textoFecha.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = notificacion.textoFecha,
                        style = MaterialTheme.typography.labelSmall,
                        color = estilo.contenido,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EstadoPanelSinNotificaciones() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Todo está al día",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Las nuevas alertas aparecerán aquí.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class EstiloNotificacion(
    val icono: ImageVector,
    val fondo: Color,
    val contenido: Color
)

@Composable

private fun estiloNotificacion(
    notificacion: NotificacionApp
): EstiloNotificacion {
    return when (notificacion.tipo) {
        TipoNotificacion.PAGO -> EstiloNotificacion(
            icono = Icons.Default.Payments,
            fondo = MaterialTheme.arcshiftColors.warningContainer,
            contenido = MaterialTheme.arcshiftColors.warning
        )

        TipoNotificacion.COTIZACION -> EstiloNotificacion(
            icono = Icons.Default.Description,
            fondo = MaterialTheme.colorScheme.primaryContainer,
            contenido = MaterialTheme.colorScheme.primary
        )

        TipoNotificacion.STOCK -> EstiloNotificacion(
            icono = Icons.Default.Inventory2,
            fondo = MaterialTheme.colorScheme.errorContainer,
            contenido = MaterialTheme.colorScheme.error
        )
    }
}

@Composable

private fun colorPrioridad(
    prioridad: PrioridadNotificacion
): Color {
    return when (prioridad) {
        PrioridadNotificacion.CRITICA -> MaterialTheme.colorScheme.error
        PrioridadNotificacion.ALTA -> MaterialTheme.arcshiftColors.warning
        PrioridadNotificacion.MEDIA -> MaterialTheme.arcshiftColors.warning
    }
}
