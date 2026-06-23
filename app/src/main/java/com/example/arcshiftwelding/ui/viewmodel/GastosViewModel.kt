import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.GastoDao
import com.example.arcshiftwelding.data.local.entity.GastoEntity
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
                lista.map { it.toUi() }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun obtenerDetalleGasto(gastoId: Int): StateFlow<GastoUi?> {
        return gastoDao.obtenerGastoPorIdFlow(gastoId)
            .map { gasto ->
                gasto?.toUi()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun guardarGasto(
        titulo: String,
        proveedor: String,
        categoria: String,
        monto: Double,
        fecha: String,
        metodoPago: String,
        formaPago: String,
        descripcion: String,
        proyecto: String,
        cotizacion: String,
        cliente: String
    ) {
        viewModelScope.launch {
            val nuevoGasto = GastoEntity(
                titulo = titulo,
                proveedor = proveedor,
                categoria = categoria,
                monto = monto,
                fecha = fecha,
                metodoPago = metodoPago,
                formaPago = formaPago,
                descripcion = descripcion,
                proyecto = proyecto,
                cotizacion = cotizacion,
                cliente = cliente,
                activo = true
            )

            gastoDao.insertarGasto(nuevoGasto)
        }
    }
}