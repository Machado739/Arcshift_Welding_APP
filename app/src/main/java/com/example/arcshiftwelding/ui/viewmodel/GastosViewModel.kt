package com.example.arcshiftwelding.ui.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.GastoDao
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch



class GastosViewModel(
    private val gastoDao: GastoDao
) : ViewModel() {

    val gastos: StateFlow<List<GastoUi>> =
        gastoDao.obtenerGastosActivos()
            .map { lista ->
                lista.map { gastoEntity ->
                    gastoEntity.toUi()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun obtenerDetalleGasto(gastoId: Int): StateFlow<GastoUi?> {
        return gastoDao.obtenerGastoPorIdFlow(gastoId)
            .map { gastoEntity ->
                gastoEntity?.toUi()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun guardarGasto(
        concepto: String,
        categoria: String,
        fecha: String,
        proveedor: String,
        subtotal: Double,
        ivaPorcentaje: Double,
        iva: Double,
        total: Double,
        metodoPago: String,
        formaPago: String,
        telefonoProveedor: String,
        correoProveedor: String,
        rfcProveedor: String,
        observaciones: String,
        proyecto: String,
        cotizacion: String,
        cliente: String
    ) {
        viewModelScope.launch {
            val nuevoGasto = GastoEntity(
                concepto = concepto,
                categoria = categoria,
                fecha = fecha,
                proveedor = proveedor,
                subtotal = subtotal,
                ivaPorcentaje = ivaPorcentaje,
                iva = iva,
                total = total,
                metodoPago = metodoPago,
                formaPago = formaPago,
                telefonoProveedor = telefonoProveedor,
                correoProveedor = correoProveedor,
                rfcProveedor = rfcProveedor,
                observaciones = observaciones,
                proyecto = proyecto,
                cotizacion = cotizacion,
                cliente = cliente
            )

            gastoDao.insertarGasto(nuevoGasto)
        }
    }

    fun obtenerGastoPorId(gastoId: Int): Flow<GastoEntity?> {
        return gastoDao.obtenerGastoPorId(gastoId)
    }

    fun actualizarGasto(
        gasto: GastoEntity,
        onFinalizado: () -> Unit
    ) {
        viewModelScope.launch {
            gastoDao.actualizarGasto(gasto)
            onFinalizado()
        }
    }

    fun eliminarGasto(
        gastoId: Int,
        onFinalizado: () -> Unit
    ) {
        viewModelScope.launch {
            gastoDao.eliminarGasto(gastoId)
            onFinalizado()
        }
    }

}

class GastosViewModelFactory(
    private val gastoDao: GastoDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GastosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GastosViewModel(gastoDao) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}

fun GastoEntity.toUi(): GastoUi {
    return GastoUi(
        id = id,
        concepto = concepto,
        categoria = categoria,
        fecha = fecha,
        proveedor = proveedor,
        subtotal = subtotal,
        ivaPorcentaje = ivaPorcentaje,
        iva = iva,
        total = total,
        metodoPago = metodoPago,
        formaPago = formaPago,
        telefonoProveedor = telefonoProveedor ?: "",
        correoProveedor = correoProveedor ?: "",
        rfcProveedor = rfcProveedor ?: "",
        observaciones = observaciones ?: "",
        proyecto = proyecto ?: "",
        cotizacion = cotizacion ?: "",
        cliente = cliente ?: ""
    )
}
