package com.example.arcshiftwelding.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.dao.ClienteDao
import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.IngresoDao
import com.example.arcshiftwelding.data.local.dao.ProyectoDao
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.IngresoEntity
import com.example.arcshiftwelding.data.local.entity.PagoProgramadoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import com.example.arcshiftwelding.data.local.relation.IngresoConRelaciones
import com.example.arcshiftwelding.utils.ComprobanteArchivoSeleccionado
import com.example.arcshiftwelding.utils.deserializarComprobantes
import com.example.arcshiftwelding.utils.serializarComprobantes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.map
import com.example.arcshiftwelding.data.local.dao.PagoProgramadoDao
import kotlinx.coroutines.flow.map
import kotlin.collections.emptyList
import com.example.arcshiftwelding.data.local.relation.PagoProgramadoConRelaciones
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class IngresoUI(
    val id: Int,
    val concepto: String,

    val cliente: String,
    val clienteId: Int?,

    val cotizacion: String,
    val cotizacionId: Int?,

    val proyectoId: Int?,
    val proyecto: String,

    val trabajo: String,
    val folio: String,
    val fecha: String,

    val subtotal: String,
    val subtotalNumero: Double,

    val iva: String,
    val ivaNumero: Double,

    val total: String,
    val totalNumero: Double,

    val montoTotalProyecto: String,
    val montoTotalProyectoNumero: Double,

    val anticipo: String,
    val anticipoNumero: Double,

    val pendiente: String,
    val pendienteNumero: Double,

    val metodoPago: String,
    val formaPago: String,
    val categoria: String,

    val comprobanteUri: String,
    val tipoComprobante: String,
    val comprobantes: List<ComprobanteArchivoSeleccionado>,

    val observaciones: String,
    val ordenTrabajo: String
)

data class PagoPorCobrarUI(
    val id: Int,
    val ingresoAnticipoId: Int?,
    val proyectoId: Int?,

    val cliente: String,
    val proyecto: String,
    val trabajo: String,

    val fechaProgramada: String,

    val monto: String,
    val montoNumero: Double,

    val estado: String,
    val observaciones: String,

    val cantidadPagosPendientes: Int,
    val cantidadPagosPosteriores: Int,

    val totalPendienteProgramado: String,
    val totalPendienteProgramadoNumero: Double,

    val totalPagosPosteriores: String,
    val totalPagosPosterioresNumero: Double
)

data class IngresoFormState(
    val id: Int = 0,
    val concepto: String = "",

    val clienteId: Int? = null,
    val cotizacionId: Int? = null,
    val proyectoId: Int? = null,

    val trabajo: String = "",
    val folio: String = "",

    val comprobanteUri: String = "",
    val tipoComprobante: String = "",
    val comprobantes: List<ComprobanteArchivoSeleccionado> = emptyList(),

    val fecha: String = fechaActual(),

    val subtotal: String = "",
    val ivaPorcentaje: String = "16",
    val iva: String = "",

    val montoTotalProyecto: String = "",
    val anticipo: String = "",

    val metodoPago: String = "",
    val formaPago: String = "Pago",
    val montoTotalProyectoNumero: Double = 0.0,
    val observaciones: String = "",
    val ordenTrabajo: String = "",
    val proyecto: String = ""
)

data class PagoProgramadoForm(
    val id: Int = 0,
    val fecha: String = "",
    val monto: String = "",
    val observaciones: String = "",

    val estado: String = "Pendiente",
    val fechaPago: String = "",
    val montoPagado: String = "",
    val metodoPago: String = "",
    val comprobanteUri: String = "",
    val tipoComprobante: String = ""
)

data class PagoProgramadoDetalleUI(
    val id: Int,
    val fechaProgramada: String,
    val montoProgramado: String,
    val montoProgramadoNumero: Double,

    val estado: String,
    val observaciones: String,

    val fechaPago: String,
    val montoPagado: String,
    val montoPagadoNumero: Double,
    val metodoPago: String,
    val comprobanteUri: String,
    val tipoComprobante: String
)
data class ResumenCobroIngresoUI(
    val totalProyecto: Double = 0.0,
    val anticipoInicial: Double = 0.0,
    val pagosPagados: Double = 0.0,
    val totalRecibido: Double = 0.0,
    val pendiente: Double = 0.0,
    val cantidadPagosPendientes: Int = 0,
    val cantidadPagosPagados: Int = 0,
    val estadoCobro: String = "Parcial"
)
class IngresosViewModel(
    private val ingresoDao: IngresoDao,
    private val clienteDao: ClienteDao,
    private val cotizacionDao: CotizacionDao,
    private val proyectoDao: ProyectoDao,
    private val pagoProgramadoDao: PagoProgramadoDao
) : ViewModel() {


    val clientesActivos: Flow<List<ClienteEntity>> =
        clienteDao.obtenerClientesActivos()

    val cotizaciones: Flow<List<CotizacionEntity>> =
        cotizacionDao.obtenerCotizaciones()

    val ingresos = combine(
        ingresoDao.obtenerIngresosConRelaciones(),
        pagoProgramadoDao.obtenerTodosLosPagosActivos()
    ) { ingresosDb, pagosDb ->

        ingresosDb.map { ingresoConRelaciones ->
            val ingresoUiBase = ingresoConRelaciones.toUi()

            val pagosDelIngreso = pagosDb.filter {
                it.ingresoAnticipoId == ingresoUiBase.id
            }



            val pagosPagados = pagosDelIngreso
                .filter { it.estado == "Pagado" }
                .sumOf { pago ->
                    if (pago.montoPagado > 0.0) {
                        pago.montoPagado
                    } else {
                        pago.montoProgramado
                    }
                }

            val totalProyecto = ingresoUiBase.montoTotalProyectoNumero
                .takeIf { it > 0.0 }
                ?: ingresoUiBase.totalNumero

            val totalRecibidoReal = ingresoUiBase.totalNumero + pagosPagados

            val pendienteReal = (totalProyecto - totalRecibidoReal).coerceAtLeast(0.0)

            val categoriaReal = when {
                pendienteReal <= 0.0 && totalProyecto > 0.0 -> "Pagos"
                ingresoUiBase.formaPago == "Anticipo" -> "Anticipos"
                else -> "Pagos"
            }

            ingresoUiBase.copy(
                categoria = categoriaReal,
                pendiente = pendienteReal.formatoDinero(),
                pendienteNumero = pendienteReal
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val proyectos: Flow<List<ProyectoEntity>> =
        proyectoDao.obtenerProyectos()


    val pagosPorCobrar = pagoProgramadoDao.obtenerPagosPendientesConRelaciones()
        .map { pagos ->
            pagos.toPagosPorCobrarAgrupadosUi()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
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

    fun List<PagoProgramadoConRelaciones>.toPagosPorCobrarAgrupadosUi(): List<PagoPorCobrarUI> {
        return this
            .groupBy { pago ->
                claveAgrupacionPagoPorCobrar(pago)
            }
            .mapNotNull { (_, pagosDelProyecto) ->
                val pagosOrdenados = pagosDelProyecto.sortedWith(
                    compareBy<PagoProgramadoConRelaciones> {
                        fechaOrdenPagoProgramado(it.pago.fechaProgramada)
                    }.thenBy {
                        it.pago.id
                    }
                )

                val proximoPago = pagosOrdenados.firstOrNull()
                    ?: return@mapNotNull null

                val pagosPosteriores = pagosOrdenados.drop(1)

                val totalPendienteProgramado = pagosOrdenados.sumOf {
                    it.pago.montoProgramado
                }

                val totalPagosPosteriores = pagosPosteriores.sumOf {
                    it.pago.montoProgramado
                }

                PagoPorCobrarUI(
                    id = proximoPago.pago.id,
                    ingresoAnticipoId = proximoPago.pago.ingresoAnticipoId,
                    proyectoId = proximoPago.pago.proyectoId,

                    cliente = proximoPago.cliente?.nombre ?: "Sin cliente",
                    proyecto = proximoPago.proyecto?.nombre
                        ?: proximoPago.ingresoAnticipo?.proyecto
                        ?: "Sin proyecto",

                    trabajo = proximoPago.ingresoAnticipo?.trabajo
                        ?: proximoPago.proyecto?.nombre
                        ?: "Sin trabajo",

                    fechaProgramada = proximoPago.pago.fechaProgramada,

                    monto = proximoPago.pago.montoProgramado.formatoDinero(),
                    montoNumero = proximoPago.pago.montoProgramado,

                    estado = proximoPago.pago.estado,
                    observaciones = proximoPago.pago.observaciones,

                    cantidadPagosPendientes = pagosOrdenados.size,
                    cantidadPagosPosteriores = pagosPosteriores.size,

                    totalPendienteProgramado = totalPendienteProgramado.formatoDinero(),
                    totalPendienteProgramadoNumero = totalPendienteProgramado,

                    totalPagosPosteriores = totalPagosPosteriores.formatoDinero(),
                    totalPagosPosterioresNumero = totalPagosPosteriores
                )
            }
            .sortedWith(
                compareBy<PagoPorCobrarUI> {
                    fechaOrdenPagoProgramado(it.fechaProgramada)
                }.thenBy {
                    it.id
                }
            )
    }

    private fun claveAgrupacionPagoPorCobrar(
        pago: PagoProgramadoConRelaciones
    ): String {
        return when {
            pago.pago.proyectoId != null -> "PROYECTO-${pago.pago.proyectoId}"
            pago.pago.ingresoAnticipoId != null -> "INGRESO-${pago.pago.ingresoAnticipoId}"
            else -> "PAGO-${pago.pago.id}"
        }
    }

    private fun fechaOrdenPagoProgramado(
        fecha: String
    ): Long {
        return try {
            if (fecha.isBlank() || fecha == "Sin fecha") {
                Long.MAX_VALUE
            } else {
                val formato = java.text.SimpleDateFormat(
                    "dd/MM/yyyy",
                    java.util.Locale.getDefault()
                )

                formato.isLenient = false
                formato.parse(fecha)?.time ?: Long.MAX_VALUE
            }
        } catch (e: Exception) {
            Long.MAX_VALUE
        }
    }

    fun guardarIngreso(
        pagosProgramados: List<PagoProgramadoForm> = emptyList(),
        onGuardado: () -> Unit
    ) {
        val form = _formState.value

        if (form.trabajo.isBlank()) {
            return
        }

        if (form.concepto.isBlank()) {
            return
        }

        val ingresoEntity = form.toEntity()

        val sumaPagosCapturados = pagosProgramados
            .filter { it.estado != "Pagado" }
            .filter { it.monto.aDouble() > 0.0 }
            .sumOf { it.monto.aDouble() }

        if (
            form.formaPago == "Anticipo" &&
            sumaPagosCapturados > 0.0 &&
            sumaPagosCapturados > ingresoEntity.pendiente
        ) {
            return
        }

        viewModelScope.launch {
            val ingresoId = ingresoDao.insertarIngreso(ingresoEntity).toInt()

            val pagosEntities = construirPagosProgramados(
                form = form,
                ingresoId = ingresoId,
                ingresoEntity = ingresoEntity,
                pagosProgramados = pagosProgramados
            )

            if (pagosEntities.isNotEmpty()) {
                pagoProgramadoDao.insertarPagosProgramados(pagosEntities)
            }

            limpiarFormulario()
            onGuardado()
        }
    }

    fun actualizarIngreso(
        pagosProgramados: List<PagoProgramadoForm> = emptyList(),
        onActualizado: () -> Unit
    ) {
        val form = _formState.value

        if (form.trabajo.isBlank()) {
            return
        }

        if (form.concepto.isBlank()) {
            return
        }

        val ingresoEntity = form.toEntity()

        val totalPagadoAnteriormente = pagosProgramados
            .filter { it.estado == "Pagado" }
            .sumOf { pago ->
                val montoPagado = pago.montoPagado.aDouble()

                if (montoPagado > 0.0) {
                    montoPagado
                } else {
                    pago.monto.aDouble()
                }
            }

        val saldoDisponibleParaProgramar = (
                ingresoEntity.pendiente - totalPagadoAnteriormente
                ).coerceAtLeast(0.0)

        val sumaPagosCapturados = pagosProgramados
            .filter { it.estado != "Pagado" }
            .filter { it.monto.aDouble() > 0.0 }
            .sumOf { it.monto.aDouble() }

        if (
            form.formaPago == "Anticipo" &&
            sumaPagosCapturados > 0.0 &&
            sumaPagosCapturados > saldoDisponibleParaProgramar
        ) {
            return
        }

        viewModelScope.launch {
            ingresoDao.actualizarIngreso(ingresoEntity)

            pagoProgramadoDao.desactivarPagosPendientesPorIngresoAnticipo(form.id)
            val pagosEntities = construirPagosProgramados(
                form = form,
                ingresoId = form.id,
                ingresoEntity = ingresoEntity,
                pagosProgramados = pagosProgramados
            )

            if (pagosEntities.isNotEmpty()) {
                pagoProgramadoDao.insertarPagosProgramados(pagosEntities)
            }

            limpiarFormulario()
            onActualizado()
        }
    }

    fun eliminarIngreso(
        ingresoId: Int,
        onEliminado: () -> Unit
    ) {
        viewModelScope.launch {
            pagoProgramadoDao.desactivarTodosLosPagosPorIngresoAnticipo(ingresoId)

            ingresoDao.desactivarIngreso(ingresoId)

            onEliminado()
        }
    }

    fun obtenerPagosProgramadosPorIngreso(
        ingresoId: Int
    ) = pagoProgramadoDao.obtenerPagosPorIngreso(ingresoId)
        .map { pagos ->
            pagos
                .filter { it.activo }
                .map { pago ->
                    PagoProgramadoForm(
                        id = pago.id,
                        fecha = pago.fechaProgramada,
                        monto = pago.montoProgramado.sinDecimalesSiAplica(),
                        observaciones = pago.observaciones,

                        estado = pago.estado,
                        fechaPago = pago.fechaPago,
                        montoPagado = pago.montoPagado.sinDecimalesSiAplica(),
                        metodoPago = pago.metodoPago,
                        comprobanteUri = pago.comprobanteUri,
                        tipoComprobante = pago.tipoComprobante
                    )
                }
        }

    private fun construirPagosProgramados(
        form: IngresoFormState,
        ingresoId: Int,
        ingresoEntity: IngresoEntity,
        pagosProgramados: List<PagoProgramadoForm>
    ): List<PagoProgramadoEntity> {
        if (form.formaPago != "Anticipo") {
            return emptyList()
        }

        val totalPagadoAnteriormente = pagosProgramados
            .filter { pago ->
                pago.estado == "Pagado"
            }
            .sumOf { pago ->
                val montoPagado = pago.montoPagado.aDouble()

                if (montoPagado > 0.0) {
                    montoPagado
                } else {
                    pago.monto.aDouble()
                }
            }

        val saldoPendienteReal = (
                ingresoEntity.pendiente - totalPagadoAnteriormente
                ).coerceAtLeast(0.0)

        val pagosCapturados = pagosProgramados
            .filter { pago ->
                pago.estado != "Pagado" &&
                        pago.monto.aDouble() > 0.0
            }
            .map { pago ->
                PagoProgramadoEntity(
                    proyectoId = form.proyectoId,
                    clienteId = form.clienteId,
                    ingresoAnticipoId = ingresoId,
                    ingresoPagadoId = null,
                    fechaProgramada = pago.fecha.ifBlank { "Sin fecha" },
                    montoProgramado = pago.monto.aDouble(),
                    estado = "Pendiente",
                    observaciones = pago.observaciones.trim(),
                    fechaRegistro = fechaActual(),

                    fechaPago = "",
                    montoPagado = 0.0,
                    metodoPago = "",
                    comprobanteUri = "",
                    tipoComprobante = "",

                    activo = true
                )
            }

        if (pagosCapturados.isNotEmpty()) {
            return pagosCapturados
        }

        return if (saldoPendienteReal > 0.0) {
            listOf(
                PagoProgramadoEntity(
                    proyectoId = form.proyectoId,
                    clienteId = form.clienteId,
                    ingresoAnticipoId = ingresoId,
                    ingresoPagadoId = null,
                    fechaProgramada = "Sin fecha",
                    montoProgramado = saldoPendienteReal,
                    estado = "Pendiente",
                    observaciones = "Pago pendiente generado automáticamente",
                    fechaRegistro = fechaActual(),

                    fechaPago = "",
                    montoPagado = 0.0,
                    metodoPago = "",
                    comprobanteUri = "",
                    tipoComprobante = "",

                    activo = true
                )
            )
        } else {
            emptyList()
        }
    }
    fun obtenerPagosDetallePorIngreso(
        ingresoId: Int
    ) = pagoProgramadoDao.obtenerPagosPorIngreso(ingresoId)
        .map { pagos ->
            pagos.map { pago ->
                PagoProgramadoDetalleUI(
                    id = pago.id,
                    fechaProgramada = pago.fechaProgramada,
                    montoProgramado = pago.montoProgramado.formatoDinero(),
                    montoProgramadoNumero = pago.montoProgramado,

                    estado = pago.estado,
                    observaciones = pago.observaciones,

                    fechaPago = pago.fechaPago,
                    montoPagado = pago.montoPagado.formatoDinero(),
                    montoPagadoNumero = pago.montoPagado,
                    metodoPago = pago.metodoPago,
                    comprobanteUri = pago.comprobanteUri,
                    tipoComprobante = pago.tipoComprobante
                )
            }
        }

    fun marcarPagoProgramadoComoPagado(
        pagoId: Int,
        fechaPago: String,
        montoPagado: Double,
        metodoPago: String,
        comprobanteUri: String,
        tipoComprobante: String,
        onCompletado: () -> Unit
    ) {
        if (fechaPago.isBlank()) {
            return
        }

        if (montoPagado <= 0.0) {
            return
        }

        if (metodoPago.isBlank()) {
            return
        }

        viewModelScope.launch {
            pagoProgramadoDao.marcarPagoComoPagado(
                pagoId = pagoId,
                fechaPago = fechaPago,
                montoPagado = montoPagado,
                metodoPago = metodoPago,
                comprobanteUri = comprobanteUri,
                tipoComprobante = tipoComprobante
            )

            onCompletado()
        }
    }

    fun calcularResumenCobroIngreso(
        ingreso: IngresoUI,
        pagos: List<PagoProgramadoDetalleUI>
    ): ResumenCobroIngresoUI {
        val totalProyecto = ingreso.montoTotalProyectoNumero
            .takeIf { it > 0.0 }
            ?: ingreso.totalNumero

        val anticipoInicial = if (ingreso.formaPago == "Anticipo") {
            ingreso.totalNumero
        } else {
            0.0
        }

        val pagosPagados = pagos
            .filter { it.estado == "Pagado" }
            .sumOf { pago ->
                if (pago.montoPagadoNumero > 0.0) {
                    pago.montoPagadoNumero
                } else {
                    pago.montoProgramadoNumero
                }
            }

        val totalRecibido = ingreso.totalNumero + pagosPagados

        val pendiente = (totalProyecto - totalRecibido).coerceAtLeast(0.0)

        val estadoCobro = if (pendiente <= 0.0 && totalProyecto > 0.0) {
            "Pagado"
        } else {
            "Parcial"
        }

        return ResumenCobroIngresoUI(
            totalProyecto = totalProyecto,
            anticipoInicial = anticipoInicial,
            pagosPagados = pagosPagados,
            totalRecibido = totalRecibido,
            pendiente = pendiente,
            cantidadPagosPendientes = pagos.count { it.estado == "Pendiente" },
            cantidadPagosPagados = pagos.count { it.estado == "Pagado" },
            estadoCobro = estadoCobro
        )
    }

    fun IngresoEntity.toForm(): IngresoFormState {
        return IngresoFormState(
            id = id,
            concepto = concepto,

            clienteId = clienteId,
            cotizacionId = cotizacionId,
            proyectoId = proyectoId,

            trabajo = trabajo,
            folio = folio,

            comprobanteUri = comprobanteUri,
            tipoComprobante = tipoComprobante,
            comprobantes = deserializarComprobantes(
                comprobantesJson = comprobantesJson,
                comprobanteUriLegado = comprobanteUri,
                tipoComprobanteLegado = tipoComprobante
            ),

            fecha = fecha,

            subtotal = subtotal.sinDecimalesSiAplica(),
            ivaPorcentaje = ivaPorcentaje.sinDecimalesSiAplica(),
            iva = iva.sinDecimalesSiAplica(),

            montoTotalProyecto = montoTotalProyecto.sinDecimalesSiAplica(),
            anticipo = anticipo.sinDecimalesSiAplica(),

            metodoPago = metodoPago,
            formaPago = formaPago,

            observaciones = observaciones,
            ordenTrabajo = ordenTrabajo,
            proyecto = proyecto
        )
    }
}

class IngresosViewModelFactory(
    private val ingresoDao: IngresoDao,
    private val clienteDao: ClienteDao,
    private val cotizacionDao: CotizacionDao,
    private val proyectoDao: ProyectoDao,
    private val pagoProgramadoDao: PagoProgramadoDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngresosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IngresosViewModel(
                ingresoDao = ingresoDao,
                clienteDao = clienteDao,
                cotizacionDao = cotizacionDao,
                proyectoDao = proyectoDao,
                pagoProgramadoDao = pagoProgramadoDao
            ) as T
        }

        throw IllegalArgumentException("ViewModel desconocido")
    }
}

fun IngresoConRelaciones.toUi(): IngresoUI {

    val ingresoActual = ingreso

    val categoria = when {
        ingresoActual.formaPago == "Anticipo" -> "Anticipos"
        else -> "Pagos"
    }
    return IngresoUI(
        id = ingresoActual.id,
        concepto = ingresoActual.concepto,

        cliente = cliente?.nombre ?: "Sin cliente",
        clienteId = ingresoActual.clienteId,

        cotizacion = cotizacion?.folio ?: "Sin cotización",
        cotizacionId = ingresoActual.cotizacionId,

        proyectoId = ingresoActual.proyectoId,
        proyecto = proyecto?.nombre ?: ingresoActual.proyecto,

        trabajo = ingresoActual.trabajo.ifBlank {
            proyecto?.nombre ?: "Sin trabajo"
        },

        folio = ingresoActual.folio,
        fecha = ingresoActual.fecha,

        subtotal = ingresoActual.subtotal.formatoDinero(),
        subtotalNumero = ingresoActual.subtotal,

        iva = ingresoActual.iva.formatoDinero(),
        ivaNumero = ingresoActual.iva,

        total = ingresoActual.total.formatoDinero(),
        totalNumero = ingresoActual.total,

        montoTotalProyecto = ingresoActual.montoTotalProyecto.formatoDinero(),
        montoTotalProyectoNumero = ingresoActual.montoTotalProyecto,

        anticipo = ingresoActual.anticipo.formatoDinero(),
        anticipoNumero = ingresoActual.anticipo,

        pendiente = ingresoActual.pendiente.formatoDinero(),
        pendienteNumero = ingresoActual.pendiente,

        metodoPago = ingresoActual.metodoPago,
        formaPago = ingresoActual.formaPago,
        categoria = categoria,

        comprobanteUri = ingresoActual.comprobanteUri,
        tipoComprobante = ingresoActual.tipoComprobante,
        comprobantes = deserializarComprobantes(
            comprobantesJson = ingresoActual.comprobantesJson,
            comprobanteUriLegado = ingresoActual.comprobanteUri,
            tipoComprobanteLegado = ingresoActual.tipoComprobante
        ),

        observaciones = ingresoActual.observaciones,
        ordenTrabajo = ingresoActual.ordenTrabajo
    )
}

fun IngresoUI.toForm(): IngresoFormState {
    return IngresoFormState(
        id = id,
        concepto = concepto,

        clienteId = clienteId,
        cotizacionId = cotizacionId,
        proyectoId = proyectoId,

        trabajo = trabajo,
        folio = folio,

        comprobanteUri = comprobanteUri,
        tipoComprobante = tipoComprobante,
        comprobantes = comprobantes,

        fecha = fecha,

        subtotal = subtotalNumero.sinDecimalesSiAplica(),
        ivaPorcentaje = if (ivaNumero <= 0.0) "0" else "16",
        iva = ivaNumero.sinDecimalesSiAplica(),

        montoTotalProyecto = montoTotalProyectoNumero.sinDecimalesSiAplica(),
        anticipo = anticipoNumero.sinDecimalesSiAplica(),

        metodoPago = metodoPago,
        formaPago = formaPago,

        observaciones = observaciones,
        ordenTrabajo = ordenTrabajo,
        proyecto = proyecto
    )
}

fun IngresoFormState.toEntity(): IngresoEntity {
    val montoRecibidoNumero = subtotal.aDouble()

    val esIngresoDeProyecto = proyectoId != null
    val esAnticipo = formaPago == "Anticipo"

    val porcentajeIva = if (esIngresoDeProyecto || esAnticipo) {
        0.0
    } else {
        ivaPorcentaje.aDouble()
    }

    val ivaNumero = if (esIngresoDeProyecto || esAnticipo) {
        0.0
    } else if (iva.isBlank()) {
        montoRecibidoNumero * (porcentajeIva / 100.0)
    } else {
        iva.aDouble()
    }

    val totalRecibidoNumero = if (esIngresoDeProyecto || esAnticipo) {
        montoRecibidoNumero
    } else {
        montoRecibidoNumero + ivaNumero
    }

    val montoTotalProyectoNumero = if (esIngresoDeProyecto || esAnticipo) {
        montoTotalProyecto.aDouble()
    } else {
        totalRecibidoNumero
    }

    val anticipoNumero = if (esAnticipo) {
        totalRecibidoNumero
    } else {
        0.0
    }

    val pendienteNumero = if (esIngresoDeProyecto || esAnticipo) {
        (montoTotalProyectoNumero - totalRecibidoNumero).coerceAtLeast(0.0)
    } else {
        0.0
    }

    val comprobantePrincipal = comprobantes.firstOrNull()

    return IngresoEntity(
        id = id,
        concepto = concepto.trim(),

        clienteId = clienteId,
        cotizacionId = null,
        proyectoId = proyectoId,

        trabajo = trabajo.trim(),
        folio = folio.trim(),
        comprobanteUri = comprobantePrincipal?.uri.orEmpty(),
        tipoComprobante = comprobantePrincipal?.tipo.orEmpty(),
        fecha = fecha.trim(),

        subtotal = montoRecibidoNumero,
        ivaPorcentaje = porcentajeIva,
        iva = ivaNumero,
        total = totalRecibidoNumero,

        montoTotalProyecto = montoTotalProyectoNumero,
        anticipo = anticipoNumero,
        pendiente = pendienteNumero,

        metodoPago = metodoPago.trim(),
        formaPago = formaPago.trim(),

        observaciones = observaciones.trim(),
        ordenTrabajo = ordenTrabajo.trim(),
        proyecto = proyecto.trim(),
        activo = true,
        comprobantesJson = serializarComprobantes(comprobantes)
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