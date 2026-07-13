package com.example.arcshiftwelding.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.PagoProgramadoDao
import com.example.arcshiftwelding.data.local.dao.ProductoDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class NotificacionesViewModel(
    context: Context,
    pagoProgramadoDao: PagoProgramadoDao,
    cotizacionDao: CotizacionDao,
    productoDao: ProductoDao
) : ViewModel() {

    private val repository = NotificacionesRepository(
        pagoProgramadoDao = pagoProgramadoDao,
        cotizacionDao = cotizacionDao,
        productoDao = productoDao
    )

    private val leidasStore = NotificacionesLeidasStore(
        context = context.applicationContext
    )

    private val notificacionesActivas: StateFlow<List<NotificacionApp>> = repository
        .observarNotificaciones()
        .onEach { activas ->
            leidasStore.sincronizarConActivas(
                idsActivas = activas.map(NotificacionApp::id).toSet()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Solo expone alertas que todavía no han sido revisadas. */
    val notificaciones: StateFlow<List<NotificacionApp>> = combine(
        notificacionesActivas,
        leidasStore.idsLeidas
    ) { activas, leidas ->
        activas.filterNot { notificacion ->
            notificacion.id in leidas
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val resumen: StateFlow<ResumenNotificaciones> = notificaciones
        .map(NotificacionesBuilder::resumir)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ResumenNotificaciones.VACIO
        )

    fun marcarComoLeida(notificacionId: String) {
        leidasStore.marcarComoLeida(notificacionId)
    }

    fun marcarTodasComoLeidas() {
        leidasStore.marcarTodasComoLeidas(
            ids = notificaciones.value.map(NotificacionApp::id)
        )
    }
}

class NotificacionesViewModelFactory(
    private val context: Context,
    private val pagoProgramadoDao: PagoProgramadoDao,
    private val cotizacionDao: CotizacionDao,
    private val productoDao: ProductoDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificacionesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificacionesViewModel(
                context = context.applicationContext,
                pagoProgramadoDao = pagoProgramadoDao,
                cotizacionDao = cotizacionDao,
                productoDao = productoDao
            ) as T
        }

        throw IllegalArgumentException(
            "NotificacionesViewModelFactory no puede crear: ${modelClass.name}"
        )
    }
}
