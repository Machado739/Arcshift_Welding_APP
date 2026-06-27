package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.DetalleCotizacionDao
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CotizacionesViewModel(
    private val cotizacionDao: CotizacionDao,
    private val detalleCotizacionDao: DetalleCotizacionDao
) : ViewModel() {

    val cotizaciones: StateFlow<List<CotizacionUI>> =
        cotizacionDao.obtenerCotizaciones()
            .map { lista ->
                lista.map { it.toUi() }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun observarCotizacion(id: Int): Flow<CotizacionEntity?> {
        return cotizacionDao.observarCotizacionPorId(id)
    }

    fun observarDetalles(cotizacionId: Int): Flow<List<DetalleCotizacionEntity>> {
        return detalleCotizacionDao.obtenerDetallesPorCotizacion(cotizacionId)
    }

    fun guardarCotizacion(
        folio: String,
        cliente: String,
        descripcionTrabajo: String,
        subtotal: Double,
        iva: Double,
        total: Double,
        fecha: String,
        estado: String = "Pendiente",
        detalles: List<DetalleCotizacionEntity> = emptyList(),
        onFinish: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val cotizacionId = cotizacionDao.insertarCotizacion(
                CotizacionEntity(
                    folio = folio,
                    cliente = cliente,
                    descripcionTrabajo = descripcionTrabajo,
                    subtotal = subtotal,
                    iva = iva,
                    total = total,
                    fecha = fecha,
                    estado = estado
                )
            ).toInt()

            if (detalles.isNotEmpty()) {
                val detallesConId = detalles.map {
                    it.copy(cotizacionId = cotizacionId)
                }

                detalleCotizacionDao.insertarDetalles(detallesConId)
            }

            onFinish()
        }
    }

    fun actualizarCotizacion(
        cotizacion: CotizacionEntity,
        detalles: List<DetalleCotizacionEntity> = emptyList(),
        onFinish: () -> Unit = {}
    ) {
        viewModelScope.launch {
            cotizacionDao.actualizarCotizacion(cotizacion)

            detalleCotizacionDao.eliminarDetallesCotizacion(cotizacion.id)

            if (detalles.isNotEmpty()) {
                val detallesActualizados = detalles.map {
                    it.copy(cotizacionId = cotizacion.id)
                }

                detalleCotizacionDao.insertarDetalles(detallesActualizados)
            }

            onFinish()
        }
    }

    fun eliminarCotizacion(
        cotizacionId: Int,
        onFinish: () -> Unit = {}
    ) {
        viewModelScope.launch {
            detalleCotizacionDao.eliminarDetallesCotizacion(cotizacionId)
            cotizacionDao.eliminarCotizacionPorId(cotizacionId)
            onFinish()
        }
    }

    fun aprobarCotizacion(id: Int) {
        viewModelScope.launch {
            cotizacionDao.actualizarEstado(id, "Aprobada")
        }
    }

    fun rechazarCotizacion(id: Int) {
        viewModelScope.launch {
            cotizacionDao.actualizarEstado(id, "Rechazada")
        }
    }
}

private fun CotizacionEntity.toUi(): CotizacionUI {
    return CotizacionUI(
        id = id,
        cliente = cliente,
        trabajo = descripcionTrabajo,
        folio = folio,
        total = total.formatoMoneda(),
        estado = estado,
        fecha = fecha,
        vence = fecha
    )
}

fun Double.formatoMoneda(): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return formato.format(this)
}

class CotizacionesViewModelFactory(
    private val cotizacionDao: CotizacionDao,
    private val detalleCotizacionDao: DetalleCotizacionDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CotizacionesViewModel(
            cotizacionDao = cotizacionDao,
            detalleCotizacionDao = detalleCotizacionDao
        ) as T
    }
}