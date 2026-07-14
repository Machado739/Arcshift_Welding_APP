package com.example.arcshiftwelding.ui.Screen.notificaciones

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.arcshiftwelding.notifications.NotificacionesViewModel

@Stable
class NotificacionesUiController(
    val viewModel: NotificacionesViewModel
) {
    var solicitudApertura by mutableIntStateOf(0)
        private set

    private var ultimaSolicitudConsumida = 0

    fun actualizarSolicitudApertura(nuevaSolicitud: Int) {
        if (nuevaSolicitud > solicitudApertura) {
            solicitudApertura = nuevaSolicitud
        }
    }

    fun consumirSolicitudApertura(): Int {
        if (solicitudApertura <= ultimaSolicitudConsumida) return 0
        ultimaSolicitudConsumida = solicitudApertura
        return solicitudApertura
    }
}

val LocalNotificacionesUiController = staticCompositionLocalOf<NotificacionesUiController?> {
    null
}

/**
 * Campana compartida por todas las pantallas principales.
 * La solicitud proveniente de una notificación del teléfono se consume una sola
 * vez, por lo que el panel no vuelve a abrirse al navegar entre pantallas.
 */
@Composable
fun CampanaNotificacionesPrincipal(
    navController: NavController
) {
    val controller = LocalNotificacionesUiController.current ?: return
    val notificaciones by controller.viewModel.notificaciones.collectAsStateWithLifecycle()
    var solicitudLocal by remember { mutableIntStateOf(0) }

    androidx.compose.runtime.LaunchedEffect(controller.solicitudApertura) {
        val solicitudConsumida = controller.consumirSolicitudApertura()
        if (solicitudConsumida > 0) {
            solicitudLocal = solicitudConsumida
        }
    }

    CampanaConPanelNotificaciones(
        notificaciones = notificaciones,
        solicitudApertura = solicitudLocal,
        onNotificacionClick = { notificacion ->
            controller.viewModel.marcarComoLeida(notificacion.id)
            navController.navigate(notificacion.rutaDestino) {
                launchSingleTop = true
            }
        },
        onMarcarTodasComoLeidas = controller.viewModel::marcarTodasComoLeidas
    )
}
