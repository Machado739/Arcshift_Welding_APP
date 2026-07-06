package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.DetalleCotizacionDao
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity
import com.example.arcshiftwelding.data.local.relation.CotizacionCompleta
import com.example.arcshiftwelding.data.local.relation.CotizacionConCliente
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
    private val detalleCotizacionDao: DetalleCotizacionDao,
    private val clienteDao: ClienteDao
) : ViewModel() {

    val clientesActivos: Flow<List<ClienteEntity>> =
        clienteDao.obtenerClientesActivos()

    val cotizaciones: StateFlow<List<CotizacionUI>> =
        cotizacionDao.obtenerCotizacionesConCliente()
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
        clienteId: Int,
        descripcionTrabajo: String,
        proyecto: String = "",
        subtotal: Double,
        iva: Double,
        total: Double,
        fecha: String,
        vigencia: String = "",
        observaciones: String = "",
        estado: String = "Pendiente",
        detalles: List<DetalleCotizacionEntity> = emptyList(),
        onFinish: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val cotizacionId = cotizacionDao.insertarCotizacion(
                CotizacionEntity(
                    folio = "",
                    clienteId = clienteId,
                    descripcionTrabajo = descripcionTrabajo,
                    proyecto = proyecto,
                    subtotal = subtotal,
                    iva = iva,
                    total = total,
                    fecha = fecha,
                    vigencia = vigencia,
                    observaciones = observaciones,
                    estado = estado
                )
            ).toInt()

            val folioGenerado = generarFolioCotizacion(cotizacionId)

            cotizacionDao.actualizarFolioCotizacion(
                id = cotizacionId,
                folio = folioGenerado
            )

            if (detalles.isNotEmpty()) {
                val detallesConId = detalles.map { detalle ->
                    detalle.copy(
                        id = 0,
                        cotizacionId = cotizacionId
                    )
                }

                detalleCotizacionDao.insertarDetalles(detallesConId)
            }

            onFinish()
        }
    }

    private fun generarFolioCotizacion(id: Int): String {
        return "COT-${id.toString().padStart(4, '0')}"
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
                val detallesActualizados = detalles.map { detalle ->
                    detalle.copy(
                        id = 0,
                        cotizacionId = cotizacion.id
                    )
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

    fun aprobarCotizacion(
        id: Int,
        onFinish: () -> Unit = {}
    ) {
        viewModelScope.launch {
            cotizacionDao.actualizarEstado(id, "Aprobada")
            onFinish()
        }
    }

    fun rechazarCotizacion(id: Int) {
        viewModelScope.launch {
            cotizacionDao.actualizarEstado(id, "Rechazada")
        }
    }

    fun obtenerCotizacionCompleta(cotizacionId: Int): Flow<CotizacionCompleta?> {
        return cotizacionDao.obtenerCotizacionCompleta(cotizacionId)
    }
}

private fun CotizacionConCliente.toUi(): CotizacionUI {
    return CotizacionUI(
        id = cotizacion.id,
        cliente = cliente?.nombre ?: "Cliente no encontrado",
        trabajo = cotizacion.descripcionTrabajo,
        folio = cotizacion.folio,
        total = cotizacion.total.formatoMoneda(),
        estado = cotizacion.estado,
        fecha = cotizacion.fecha,
        vence = cotizacion.fecha
    )
}

fun Double.formatoMoneda(): String {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return formato.format(this)
}



class CotizacionesViewModelFactory(
    private val cotizacionDao: CotizacionDao,
    private val detalleCotizacionDao: DetalleCotizacionDao,
    private val clienteDao: ClienteDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CotizacionesViewModel(
            cotizacionDao = cotizacionDao,
            detalleCotizacionDao = detalleCotizacionDao,
            clienteDao = clienteDao
        ) as T
    }
}