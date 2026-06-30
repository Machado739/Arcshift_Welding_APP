package com.example.arcshiftwelding.ui.Screen.ingresos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.IngresoDao
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.IngresoEntity
import com.example.arcshiftwelding.data.local.relation.IngresoConRelaciones
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class IngresoUI(
    val id: Int,
    val cliente: String,
    val clienteId: Int?,
    val trabajo: String,
    val folio: String,
    val total: String,
    val anticipo: String,
    val pendiente: String,
    val categoria: String,
    val fecha: String,
    val concepto: String,
    val subtotal: String,
    val iva: String,
    val ivaPorcentaje: String,
    val metodoPago: String,
    val formaPago: String,
    val observaciones: String,
    val cotizacion: String,
    val cotizacionId: Int?,
    val ordenTrabajo: String,
    val proyecto: String,
    val totalNumero: Double,
    val anticipoNumero: Double,
    val pendienteNumero: Double
)

data class IngresoFormState(
    val id: Int = 0,
    val concepto: String = "",
    val clienteId: Int? = null,
    val cotizacionId: Int? = null,

    val trabajo: String = "",
    val folio: String = "",
    val fecha: String = fechaActual(),

    val subtotal: String = "",
    val ivaPorcentaje: String = "16",
    val iva: String = "",
    val anticipo: String = "",

    val metodoPago: String = "",
    val formaPago: String = "Contado",

    val observaciones: String = "",
    val ordenTrabajo: String = "",
    val proyecto: String = ""
)

class IngresosViewModel(
    private val ingresoDao: IngresoDao,
    private val clienteDao: ClienteDao,
    private val cotizacionDao: CotizacionDao
) : ViewModel() {

    val clientesActivos: Flow<List<ClienteEntity>> =
        clienteDao.obtenerClientesActivos()

    val cotizaciones: Flow<List<CotizacionEntity>> =
        cotizacionDao.obtenerCotizaciones()

    val ingresos: StateFlow<List<IngresoUI>> =
        ingresoDao.obtenerIngresosConRelaciones()
            .map { lista ->
                lista.map { ingresoConRelaciones ->
                    ingresoConRelaciones.toUi()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _formState = MutableStateFlow(IngresoFormState())
    val formState: StateFlow<IngresoFormState> = _formState

    fun obtenerIngreso(ingresoId: Int): Flow<IngresoUI?> {
        return ingresoDao.obtenerIngresoConRelaciones(ingresoId)
            .map { ingresoConRelaciones ->
                ingresoConRelaciones?.toUi()
            }
    }

    fun actualizarFormulario(nuevoEstado: IngresoFormState) {
        _formState.value = nuevoEstado
    }

    fun limpiarFormulario() {
        _formState.value = IngresoFormState()
    }

    fun cargarIngresoParaEditar(ingresoId: Int) {
        viewModelScope.launch {
            val ingreso = ingresoDao.obtenerIngresoPorIdDirecto(ingresoId)

            if (ingreso != null) {
                _formState.value = ingreso.toForm()
            }
        }
    }

    fun guardarIngreso(onGuardado: () -> Unit) {
        val form = _formState.value

        if (form.concepto.isBlank()) {
            return
        }

        viewModelScope.launch {
            ingresoDao.insertarIngreso(form.toEntity())
            limpiarFormulario()
            onGuardado()
        }
    }

    fun actualizarIngreso(onActualizado: () -> Unit) {
        val form = _formState.value

        if (form.id == 0 || form.concepto.isBlank()) {
            return
        }

        viewModelScope.launch {
            ingresoDao.actualizarIngreso(form.toEntity())
            limpiarFormulario()
            onActualizado()
        }
    }

    fun eliminarIngreso(
        ingresoId: Int,
        onEliminado: () -> Unit
    ) {
        viewModelScope.launch {
            ingresoDao.desactivarIngreso(ingresoId)
            onEliminado()
        }
    }
}

class IngresosViewModelFactory(
    private val ingresoDao: IngresoDao,
    private val clienteDao: ClienteDao,
    private val cotizacionDao: CotizacionDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngresosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IngresosViewModel(
                ingresoDao = ingresoDao,
                clienteDao = clienteDao,
                cotizacionDao = cotizacionDao
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}

fun IngresoConRelaciones.toUi(): IngresoUI {
    val ingresoActual = ingreso

    val categoria = when {
        ingresoActual.pendiente <= 0.0 -> "Pagados"
        ingresoActual.anticipo > 0.0 -> "Anticipos"
        else -> "Pendientes"
    }

    return IngresoUI(
        id = ingresoActual.id,
        cliente = cliente?.nombre ?: "Sin cliente",
        clienteId = ingresoActual.clienteId,
        trabajo = ingresoActual.trabajo.ifBlank { ingresoActual.concepto },
        folio = ingresoActual.folio,
        total = ingresoActual.total.formatoDinero(),
        anticipo = ingresoActual.anticipo.formatoDinero(),
        pendiente = ingresoActual.pendiente.formatoDinero(),
        categoria = categoria,
        fecha = ingresoActual.fecha,
        concepto = ingresoActual.concepto,
        subtotal = ingresoActual.subtotal.formatoDinero(),
        iva = ingresoActual.iva.formatoDinero(),
        ivaPorcentaje = ingresoActual.ivaPorcentaje.toString(),
        metodoPago = ingresoActual.metodoPago,
        formaPago = ingresoActual.formaPago,
        observaciones = ingresoActual.observaciones,
        cotizacion = cotizacion?.folio ?: "Sin cotización",
        cotizacionId = ingresoActual.cotizacionId,
        ordenTrabajo = ingresoActual.ordenTrabajo,
        proyecto = ingresoActual.proyecto,
        totalNumero = ingresoActual.total,
        anticipoNumero = ingresoActual.anticipo,
        pendienteNumero = ingresoActual.pendiente
    )
}

fun IngresoEntity.toForm(): IngresoFormState {
    return IngresoFormState(
        id = id,
        concepto = concepto,
        clienteId = clienteId,
        cotizacionId = cotizacionId,
        trabajo = trabajo,
        folio = folio,
        fecha = fecha,
        subtotal = subtotal.sinDecimalesSiAplica(),
        ivaPorcentaje = ivaPorcentaje.sinDecimalesSiAplica(),
        iva = iva.sinDecimalesSiAplica(),
        anticipo = anticipo.sinDecimalesSiAplica(),
        metodoPago = metodoPago,
        formaPago = formaPago,
        observaciones = observaciones,
        ordenTrabajo = ordenTrabajo,
        proyecto = proyecto
    )
}

fun IngresoFormState.toEntity(): IngresoEntity {
    val subtotalNumero = subtotal.aDouble()
    val porcentajeIva = ivaPorcentaje.aDouble()

    val ivaNumero = if (iva.isBlank()) {
        subtotalNumero * (porcentajeIva / 100)
    } else {
        iva.aDouble()
    }

    val totalNumero = subtotalNumero + ivaNumero
    val anticipoNumero = anticipo.aDouble()
    val pendienteNumero = totalNumero - anticipoNumero

    return IngresoEntity(
        id = id,
        concepto = concepto.trim(),
        clienteId = clienteId,
        cotizacionId = cotizacionId,
        trabajo = trabajo.trim(),
        folio = folio.trim(),
        fecha = fecha.trim(),
        subtotal = subtotalNumero,
        ivaPorcentaje = porcentajeIva,
        iva = ivaNumero,
        total = totalNumero,
        anticipo = anticipoNumero,
        pendiente = pendienteNumero,
        metodoPago = metodoPago.trim(),
        formaPago = formaPago.trim(),
        observaciones = observaciones.trim(),
        ordenTrabajo = ordenTrabajo.trim(),
        proyecto = proyecto.trim(),
        activo = true
    )
}

fun String.aDouble(): Double {
    return this
        .replace("$", "")
        .replace(",", "")
        .replace(" ", "")
        .trim()
        .toDoubleOrNull() ?: 0.0
}

fun Double.formatoDinero(): String {
    val formato = DecimalFormat("$#,##0.00")
    return formato.format(this)
}

fun Double.sinDecimalesSiAplica(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}

fun fechaActual(): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
}