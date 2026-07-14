package com.example.arcshiftwelding.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import com.example.arcshiftwelding.data.local.entity.IngresoEntity
import com.example.arcshiftwelding.data.local.entity.PagoProgramadoEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import com.example.arcshiftwelding.data.local.relation.ClienteConCantidadCotizaciones
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Datos compactos utilizados por la pantalla principal. */
data class ClienteRecienteDashboardUi(
    val id: Int,
    val nombre: String,
    val detalle: String
)

data class ProductoBajoStockDashboardUi(
    val id: Int,
    val nombre: String,
    val stock: Int,
    val stockMinimo: Int,
    val unidad: String,
    val agotado: Boolean
)

data class PagoPendienteDashboardUi(
    val id: Int,
    val descripcion: String,
    val fecha: String,
    val monto: Double,
    val vencido: Boolean
)

data class DashboardUiState(
    val cargando: Boolean = true,
    val periodoTexto: String = "Mes actual",
    val ingresos: Double = 0.0,
    val gastos: Double = 0.0,
    val utilidad: Double = 0.0,
    val cotizaciones: Int = 0,
    val variacionIngresos: Double? = null,
    val variacionGastos: Double? = null,
    val cotizacionesPendientes: Int = 0,
    val cotizacionesAprobadas: Int = 0,
    val cotizacionesRechazadas: Int = 0,
    val ingresosUltimos7Dias: List<Float> = List(7) { 0f },
    val gastosUltimos7Dias: List<Float> = List(7) { 0f },
    val etiquetasUltimos7Dias: List<String> = emptyList(),
    val clientesRecientes: List<ClienteRecienteDashboardUi> = emptyList(),
    val productosBajoStock: List<ProductoBajoStockDashboardUi> = emptyList(),
    val cantidadProductosBajoStock: Int = 0,
    val cantidadPagosPendientes: Int = 0,
    val totalPorCobrar: Double = 0.0,
    val proximoCobro: PagoPendienteDashboardUi? = null
)

private data class DatosFinancierosDashboard(
    val ingresos: List<IngresoEntity>,
    val gastos: List<GastoEntity>,
    val pagos: List<PagoProgramadoEntity>
)

private data class DatosOperativosDashboard(
    val productos: List<ProductoEntity>,
    val cotizaciones: List<com.example.arcshiftwelding.data.local.entity.CotizacionEntity>,
    val clientes: List<ClienteConCantidadCotizaciones>,
    val proyectos: List<ProyectoEntity>
)

class DashboardViewModel(
    database: ArcshiftWeldingDatabase
) : ViewModel() {

    private val datosFinancieros = combine(
        database.ingresoDao().obtenerIngresosActivos(),
        database.gastoDao().obtenerGastosActivos(),
        database.pagoProgramadoDao().obtenerTodosLosPagosActivos()
    ) { ingresos, gastos, pagos ->
        DatosFinancierosDashboard(
            ingresos = ingresos,
            gastos = gastos,
            pagos = pagos
        )
    }

    private val datosOperativos = combine(
        database.productoDao().obtenerProductos(),
        database.cotizacionDao().obtenerCotizaciones(),
        database.clienteDao().obtenerClientesConCantidadCotizaciones(),
        database.proyectoDao().obtenerProyectos()
    ) { productos, cotizaciones, clientes, proyectos ->
        DatosOperativosDashboard(
            productos = productos,
            cotizaciones = cotizaciones,
            clientes = clientes,
            proyectos = proyectos
        )
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        datosFinancieros,
        datosOperativos
    ) { financieros, operativos ->
        construirEstado(financieros, operativos)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    private fun construirEstado(
        financieros: DatosFinancierosDashboard,
        operativos: DatosOperativosDashboard
    ): DashboardUiState {
        val hoy = LocalDate.now()
        val inicioMes = hoy.withDayOfMonth(1)
        val finMes = hoy.withDayOfMonth(hoy.lengthOfMonth())
        val inicioMesAnterior = inicioMes.minusMonths(1)
        val finMesAnterior = inicioMes.minusDays(1)

        // Las fechas se convierten una sola vez para mantener fluido el Dashboard
        // incluso con las cargas masivas de prueba.
        val ingresosFechados = financieros.ingresos.mapNotNull { ingreso ->
            parseFecha(ingreso.fecha)?.let { fecha -> fecha to ingreso.total }
        }
        val gastosFechados = financieros.gastos.mapNotNull { gasto ->
            parseFecha(gasto.fecha)?.let { fecha -> fecha to gasto.total }
        }

        val totalIngresos = ingresosFechados
            .asSequence()
            .filter { (fecha, _) -> fecha.estaEntre(inicioMes, finMes) }
            .sumOf { (_, total) -> total }
        val totalGastos = gastosFechados
            .asSequence()
            .filter { (fecha, _) -> fecha.estaEntre(inicioMes, finMes) }
            .sumOf { (_, total) -> total }
        val totalIngresosAnterior = ingresosFechados
            .asSequence()
            .filter { (fecha, _) -> fecha.estaEntre(inicioMesAnterior, finMesAnterior) }
            .sumOf { (_, total) -> total }
        val totalGastosAnterior = gastosFechados
            .asSequence()
            .filter { (fecha, _) -> fecha.estaEntre(inicioMesAnterior, finMesAnterior) }
            .sumOf { (_, total) -> total }

        val cotizacionesMes = operativos.cotizaciones.filter {
            parseFecha(it.fecha)?.estaEntre(inicioMes, finMes) == true
        }

        val pendientes = cotizacionesMes.count { estadoPendiente(it.estado) }
        val aprobadas = cotizacionesMes.count { estadoAprobado(it.estado) }
        val rechazadas = cotizacionesMes.count { estadoRechazado(it.estado) }

        val inicioGrafica = hoy.minusDays(6)
        val diasGrafica = (0L..6L).map { inicioGrafica.plusDays(it) }
        val ingresosAgrupados = ingresosFechados
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, importes) -> importes.sum() }
        val gastosAgrupados = gastosFechados
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, importes) -> importes.sum() }
        val ingresosPorDia = diasGrafica.map { dia ->
            (ingresosAgrupados[dia] ?: 0.0).toFloat()
        }
        val gastosPorDia = diasGrafica.map { dia ->
            (gastosAgrupados[dia] ?: 0.0).toFloat()
        }

        val clientesRecientes = operativos.clientes
            .asSequence()
            .filter { !it.cliente.eliminado }
            .sortedByDescending { it.cliente.fechaRegistro }
            .take(4)
            .map {
                val cantidad = it.cantidadCotizaciones
                ClienteRecienteDashboardUi(
                    id = it.cliente.id,
                    nombre = it.cliente.nombre,
                    detalle = when (cantidad) {
                        0 -> "Sin cotizaciones"
                        1 -> "1 cotización"
                        else -> "$cantidad cotizaciones"
                    }
                )
            }
            .toList()

        val productosBajoStockTodos = operativos.productos
            .filter { it.activo && it.stock <= it.stockMinimo }
            .sortedWith(
                compareBy<ProductoEntity> { it.stock > 0 }
                    .thenBy { it.stock }
                    .thenBy { it.nombre }
            )

        val productosBajoStock = productosBajoStockTodos.take(4).map {
            ProductoBajoStockDashboardUi(
                id = it.id,
                nombre = it.nombre,
                stock = it.stock,
                stockMinimo = it.stockMinimo,
                unidad = it.unidad,
                agotado = it.stock <= 0
            )
        }

        val clientesPorId = operativos.clientes.associateBy { it.cliente.id }
        val proyectosPorId = operativos.proyectos.associateBy { it.id }
        val pagosPendientes = financieros.pagos
            .asSequence()
            .filter { it.activo && it.estado.equals("Pendiente", ignoreCase = true) }
            .sortedWith(
                compareBy<PagoProgramadoEntity> {
                    parseFecha(it.fechaProgramada) ?: LocalDate.MAX
                }.thenBy { it.id }
            )
            .toList()

        val proximoPago = pagosPendientes.firstOrNull()?.let { pago ->
            val fecha = parseFecha(pago.fechaProgramada)
            val descripcion = when {
                pago.proyectoId != null ->
                    proyectosPorId[pago.proyectoId]?.nombre ?: "Pago de proyecto"
                pago.clienteId != null ->
                    clientesPorId[pago.clienteId]?.cliente?.nombre ?: "Pago de cliente"
                else -> "Pago programado"
            }

            PagoPendienteDashboardUi(
                id = pago.id,
                descripcion = descripcion,
                fecha = pago.fechaProgramada,
                monto = pago.montoProgramado,
                vencido = fecha?.isBefore(hoy) == true
            )
        }

        return DashboardUiState(
            cargando = false,
            periodoTexto = periodoMes(inicioMes, finMes),
            ingresos = totalIngresos,
            gastos = totalGastos,
            utilidad = totalIngresos - totalGastos,
            cotizaciones = cotizacionesMes.size,
            variacionIngresos = calcularVariacion(totalIngresos, totalIngresosAnterior),
            variacionGastos = calcularVariacion(totalGastos, totalGastosAnterior),
            cotizacionesPendientes = pendientes,
            cotizacionesAprobadas = aprobadas,
            cotizacionesRechazadas = rechazadas,
            ingresosUltimos7Dias = ingresosPorDia,
            gastosUltimos7Dias = gastosPorDia,
            etiquetasUltimos7Dias = diasGrafica.map {
                it.format(DateTimeFormatter.ofPattern("EEE", Locale("es", "MX")))
                    .replaceFirstChar { it.uppercase() }
            },
            clientesRecientes = clientesRecientes,
            productosBajoStock = productosBajoStock,
            cantidadProductosBajoStock = productosBajoStockTodos.size,
            cantidadPagosPendientes = pagosPendientes.size,
            totalPorCobrar = pagosPendientes.sumOf { it.montoProgramado },
            proximoCobro = proximoPago
        )
    }
}

class DashboardViewModelFactory(
    private val database: ArcshiftWeldingDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(database) as T
        }
        throw IllegalArgumentException(
            "DashboardViewModelFactory no puede crear: ${modelClass.name}"
        )
    }
}

private val FORMATOS_FECHA_DASHBOARD = listOf(
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ofPattern("d/M/yyyy"),
    DateTimeFormatter.ISO_LOCAL_DATE,
    DateTimeFormatter.ofPattern("dd-MM-yyyy"),
    DateTimeFormatter.ofPattern("d-M-yyyy")
)

private fun parseFecha(valor: String): LocalDate? {
    val limpio = valor.trim()
        .substringBefore(' ')
        .substringBefore('T')

    if (limpio.isBlank()) return null

    FORMATOS_FECHA_DASHBOARD.forEach { formato ->
        try {
            return LocalDate.parse(limpio, formato)
        } catch (_: Exception) {
            // Se intenta con el siguiente formato.
        }
    }
    return null
}

private fun LocalDate.estaEntre(inicio: LocalDate, fin: LocalDate): Boolean =
    !isBefore(inicio) && !isAfter(fin)

private fun calcularVariacion(actual: Double, anterior: Double): Double? {
    if (anterior == 0.0) return null
    return ((actual - anterior) / anterior) * 100.0
}

private fun periodoMes(inicio: LocalDate, fin: LocalDate): String {
    val formatoInicio = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "MX"))
    val formatoFin = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "MX"))
    return "${inicio.format(formatoInicio)} - ${fin.format(formatoFin)}"
}

private fun estadoPendiente(estado: String): Boolean {
    val valor = estado.trim().lowercase()
    return valor.contains("pend") || valor.contains("espera")
}

private fun estadoAprobado(estado: String): Boolean {
    val valor = estado.trim().lowercase()
    return valor.contains("aprob") || valor.contains("acept")
}

private fun estadoRechazado(estado: String): Boolean {
    val valor = estado.trim().lowercase()
    return valor.contains("rechaz") || valor.contains("cancel")
}
