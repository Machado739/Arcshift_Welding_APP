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
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.ProyectoDao
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.relation.GastoConRelaciones


class GastosViewModel(
    private val gastoDao: GastoDao,
    private val clienteDao: ClienteDao,
    private val cotizacionDao: CotizacionDao,
    private val proyectoDao: ProyectoDao
) : ViewModel() {

    val clientesActivos: Flow<List<ClienteEntity>> =
        clienteDao.obtenerClientesActivos()

    val cotizaciones: Flow<List<CotizacionEntity>> =
        cotizacionDao.obtenerCotizaciones()

    val gastos: StateFlow<List<GastoUi>> =
        gastoDao.obtenerGastosConRelaciones()
            .map { lista ->
                lista.map { gastoConRelaciones ->
                    gastoConRelaciones.toUi()
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    val proyectos = proyectoDao.obtenerProyectos()

    fun obtenerDetalleGasto(gastoId: Int): StateFlow<GastoUi?> {
        return gastoDao.obtenerGastoConRelaciones(gastoId)
            .map { gastoConRelaciones ->
                gastoConRelaciones?.toUi()
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
        telefonoProveedor: String?,
        correoProveedor: String?,
        rfcProveedor: String?,
        observaciones: String?,
        proyecto: String?,
        clienteId: Int?,
        proyectoNombre: String?,
        proyectoId: Int?,
        cotizacionId: Int?,
        onFinish: () -> Unit = {}
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
                clienteId = clienteId,
                cotizacionId = cotizacionId,
                proyectoId = proyectoId,
                proyectoNombre = proyectoNombre,
            )

            gastoDao.insertarGasto(nuevoGasto)
            onFinish()
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
            gastoDao.eliminarGastoPorId(gastoId)
            onFinalizado()
        }
    }

}

class GastosViewModelFactory(
    private val gastoDao: GastoDao,
    private val clienteDao: ClienteDao,
    private val cotizacionDao: CotizacionDao,
    private val proyectoDao: ProyectoDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GastosViewModel(
            gastoDao = gastoDao,
            clienteDao = clienteDao,
            cotizacionDao = cotizacionDao,
            proyectoDao = proyectoDao
        ) as T
    }
}

fun GastoConRelaciones.toUi(): GastoUi {
    return GastoUi(
        id = gasto.id,
        concepto = gasto.concepto,
        categoria = gasto.categoria,
        fecha = gasto.fecha,
        proveedor = gasto.proveedor,
        subtotal = gasto.subtotal,
        ivaPorcentaje = gasto.ivaPorcentaje,
        iva = gasto.iva,
        total = gasto.total,
        metodoPago = gasto.metodoPago,
        formaPago = gasto.formaPago,
        telefonoProveedor = gasto.telefonoProveedor ?: "",
        correoProveedor = gasto.correoProveedor ?: "",
        rfcProveedor = gasto.rfcProveedor ?: "",
        observaciones = gasto.observaciones ?: "",
        proyecto = gasto.proyecto ?: "",
        cotizacion = cotizacion?.folio ?: "Sin cotización",
        cliente = cliente?.nombre ?: "Sin cliente"
    )
}
