package com.example.arcshiftwelding.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import com.example.arcshiftwelding.data.local.entity.IngresoEntity
import com.example.arcshiftwelding.data.local.entity.PagoProgramadoEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class PeriodoReporte(val etiqueta: String) {
    TODO("Todo"),
    HOY("Hoy"),
    SEMANA("Semana"),
    MES("Mes"),
    ANIO("Año")
}

data class ResumenGeneralReportes(
    val ingresos: Double = 0.0,
    val gastos: Double = 0.0,
    val utilidad: Double = 0.0,
    val porCobrar: Double = 0.0,
    val cotizacionesPendientes: Int = 0,
    val productosBajoStock: Int = 0
)

data class MetricaReporteUi(
    val titulo: String,
    val valor: String
)

data class RegistroReporteUi(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val valor: String,
    val fecha: String,
    val estado: String = ""
)

data class ReporteDetalleUi(
    val tipo: String,
    val titulo: String,
    val descripcion: String,
    val etiquetaPrincipal: String,
    val valorPrincipal: String,
    val etiquetaSecundaria: String,
    val valorSecundario: String,
    val estado: String,
    val metricas: List<MetricaReporteUi> = emptyList(),
    val registros: List<RegistroReporteUi> = emptyList()
)

data class ReportesUiState(
    val cargando: Boolean = true,
    val periodo: PeriodoReporte = PeriodoReporte.MES,
    val textoPeriodo: String = "Mes actual",
    val resumen: ResumenGeneralReportes = ResumenGeneralReportes(),
    val reportes: List<ReporteDetalleUi> = emptyList()
) {
    fun reportePorTipo(tipo: String): ReporteDetalleUi? =
        reportes.firstOrNull { it.tipo.equals(tipo, ignoreCase = true) }
}

private data class DatosFinancieros(
    val ingresos: List<IngresoEntity>,
    val gastos: List<GastoEntity>,
    val pagos: List<PagoProgramadoEntity>
)

private data class DatosOperativos(
    val productos: List<ProductoEntity>,
    val cotizaciones: List<CotizacionEntity>,
    val clientes: List<ClienteEntity>,
    val proyectos: List<ProyectoEntity>,
    val empleados: List<EmpleadoEntity>
)

class ReportesViewModel(
    database: ArcshiftWeldingDatabase
) : ViewModel() {

    private val periodoSeleccionado = MutableStateFlow(PeriodoReporte.MES)

    private val datosFinancieros = combine(
        database.ingresoDao().obtenerIngresosActivos(),
        database.gastoDao().obtenerGastosActivos(),
        database.pagoProgramadoDao().obtenerTodosLosPagosActivos()
    ) { ingresos, gastos, pagos ->
        DatosFinancieros(
            ingresos = ingresos,
            gastos = gastos,
            pagos = pagos
        )
    }

    private val datosOperativos = combine(
        database.productoDao().obtenerProductos(),
        database.cotizacionDao().obtenerCotizaciones(),
        database.clienteDao().obtenerClientesConCantidadCotizaciones(),
        database.proyectoDao().obtenerProyectos(),
        database.empleadoDao().obtenerEmpleados()
    ) { productos, cotizaciones, clientesConCantidad, proyectos, empleados ->
        DatosOperativos(
            productos = productos,
            cotizaciones = cotizaciones,
            clientes = clientesConCantidad.map { it.cliente },
            proyectos = proyectos,
            empleados = empleados
        )
    }

    val uiState: StateFlow<ReportesUiState> = combine(
        periodoSeleccionado,
        datosFinancieros,
        datosOperativos
    ) { periodo, financieros, operativos ->
        construirEstado(periodo, financieros, operativos)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReportesUiState()
    )

    fun seleccionarPeriodo(periodo: PeriodoReporte) {
        periodoSeleccionado.value = periodo
    }

    private fun construirEstado(
        periodo: PeriodoReporte,
        financieros: DatosFinancieros,
        operativos: DatosOperativos
    ): ReportesUiState {
        val hoy = LocalDate.now()

        val ingresos = financieros.ingresos.filter {
            fechaEnPeriodo(it.fecha, periodo, hoy)
        }
        val gastos = financieros.gastos.filter {
            fechaEnPeriodo(it.fecha, periodo, hoy)
        }
        val cotizaciones = operativos.cotizaciones.filter {
            fechaEnPeriodo(it.fecha, periodo, hoy)
        }
        val clientesNuevos = operativos.clientes.filter {
            epochEnPeriodo(it.fechaRegistro, periodo, hoy)
        }
        val proyectos = operativos.proyectos.filter {
            fechaEnPeriodo(it.fechaInicio, periodo, hoy)
        }
        val empleadosNuevos = operativos.empleados.filter {
            fechaEnPeriodo(it.fechaIngreso, periodo, hoy)
        }

        val pagosPendientes = financieros.pagos.filter {
            it.activo && it.estado.equals("Pendiente", ignoreCase = true)
        }

        val totalIngresos = ingresos.sumOf { it.total }
        val totalGastos = gastos.sumOf { it.total }
        val utilidad = totalIngresos - totalGastos
        val porCobrar = pagosPendientes.sumOf { it.montoProgramado }
        val bajoStock = operativos.productos.count { it.stock <= it.stockMinimo }
        val pendientes = cotizaciones.count {
            it.estado.equals("Pendiente", ignoreCase = true)
        }

        val reportes = listOf(
            construirReporteIngresos(ingresos, pagosPendientes),
            construirReporteGastos(gastos),
            construirReporteInventario(operativos.productos),
            construirReporteCotizaciones(cotizaciones),
            construirReporteClientes(clientesNuevos, operativos.clientes, operativos.cotizaciones),
            construirReporteProyectos(proyectos, operativos.proyectos),
            construirReporteEmpleados(empleadosNuevos, operativos.empleados)
        )

        return ReportesUiState(
            cargando = false,
            periodo = periodo,
            textoPeriodo = textoPeriodo(periodo, hoy),
            resumen = ResumenGeneralReportes(
                ingresos = totalIngresos,
                gastos = totalGastos,
                utilidad = utilidad,
                porCobrar = porCobrar,
                cotizacionesPendientes = pendientes,
                productosBajoStock = bajoStock
            ),
            reportes = reportes
        )
    }

    private fun construirReporteIngresos(
        ingresos: List<IngresoEntity>,
        pagosPendientes: List<PagoProgramadoEntity>
    ): ReporteDetalleUi {
        val total = ingresos.sumOf { it.total }
        val promedio = if (ingresos.isEmpty()) 0.0 else total / ingresos.size
        val mayor = ingresos.maxOfOrNull { it.total } ?: 0.0
        val metodoPrincipal = valorMasFrecuente(ingresos.map { it.metodoPago })
        val porCobrar = pagosPendientes.sumOf { it.montoProgramado }

        val registros = ingresos
            .sortedByDescending { fechaOrdenable(it.fecha) }
            .map {
                RegistroReporteUi(
                    id = it.id,
                    titulo = it.trabajo.ifBlank { it.concepto.ifBlank { "Ingreso ${it.folio}" } },
                    descripcion = listOf(it.metodoPago, it.proyecto)
                        .filter { valor -> valor.isNotBlank() }
                        .joinToString(" · ")
                        .ifBlank { "Ingreso registrado" },
                    valor = moneda(it.total),
                    fecha = it.fecha,
                    estado = "Recibido"
                )
            }

        return ReporteDetalleUi(
            tipo = "Ingresos",
            titulo = "Ingresos",
            descripcion = "Cobros e ingresos registrados en el periodo.",
            etiquetaPrincipal = "Total recibido",
            valorPrincipal = moneda(total),
            etiquetaSecundaria = "Registros",
            valorSecundario = ingresos.size.toString(),
            estado = "Actualizado",
            metricas = listOf(
                MetricaReporteUi("Ingreso promedio", moneda(promedio)),
                MetricaReporteUi("Mayor ingreso", moneda(mayor)),
                MetricaReporteUi("Método más usado", metodoPrincipal),
                MetricaReporteUi("Total por cobrar", moneda(porCobrar))
            ),
            registros = registros
        )
    }

    private fun construirReporteGastos(gastos: List<GastoEntity>): ReporteDetalleUi {
        val total = gastos.sumOf { it.total }
        val promedio = if (gastos.isEmpty()) 0.0 else total / gastos.size
        val mayor = gastos.maxOfOrNull { it.total } ?: 0.0
        val categoriaPrincipal = valorMasFrecuente(gastos.map { it.categoria })
        val proveedorPrincipal = valorMasFrecuente(gastos.map { it.proveedor })

        val registros = gastos
            .sortedByDescending { fechaOrdenable(it.fecha) }
            .map {
                RegistroReporteUi(
                    id = it.id,
                    titulo = it.concepto.ifBlank { "Gasto ${it.id}" },
                    descripcion = listOf(it.categoria, it.proveedor)
                        .filter { valor -> valor.isNotBlank() }
                        .joinToString(" · ")
                        .ifBlank { "Gasto registrado" },
                    valor = moneda(it.total),
                    fecha = it.fecha,
                    estado = it.metodoPago
                )
            }

        return ReporteDetalleUi(
            tipo = "Gastos",
            titulo = "Gastos",
            descripcion = "Compras, servicios y egresos registrados.",
            etiquetaPrincipal = "Total gastado",
            valorPrincipal = moneda(total),
            etiquetaSecundaria = "Registros",
            valorSecundario = gastos.size.toString(),
            estado = "Actualizado",
            metricas = listOf(
                MetricaReporteUi("Gasto promedio", moneda(promedio)),
                MetricaReporteUi("Mayor gasto", moneda(mayor)),
                MetricaReporteUi("Categoría principal", categoriaPrincipal),
                MetricaReporteUi("Proveedor principal", proveedorPrincipal)
            ),
            registros = registros
        )
    }

    private fun construirReporteInventario(productos: List<ProductoEntity>): ReporteDetalleUi {
        val bajoStock = productos.count { it.stock <= it.stockMinimo }
        val agotados = productos.count { it.stock <= 0 }
        val unidades = productos.sumOf { it.stock }
        val valorInventario = productos.sumOf { it.stock * it.precioCompra }

        val registros = productos
            .sortedWith(
                compareBy<ProductoEntity> { it.stock > it.stockMinimo }
                    .thenBy { it.stock }
                    .thenBy { it.nombre }
            )
            .map {
                val estado = when {
                    it.stock <= 0 -> "Agotado"
                    it.stock <= it.stockMinimo -> "Bajo stock"
                    else -> "Disponible"
                }
                RegistroReporteUi(
                    id = it.id,
                    titulo = it.nombre,
                    descripcion = "${it.stock} ${it.unidad} · Mínimo ${it.stockMinimo}",
                    valor = moneda(it.stock * it.precioCompra),
                    fecha = it.fechaRegistro,
                    estado = estado
                )
            }

        return ReporteDetalleUi(
            tipo = "Inventario",
            titulo = "Inventario",
            descripcion = "Existencias y valor actual del inventario.",
            etiquetaPrincipal = "Valor estimado",
            valorPrincipal = moneda(valorInventario),
            etiquetaSecundaria = "Productos",
            valorSecundario = productos.size.toString(),
            estado = if (bajoStock > 0) "Requiere atención" else "Correcto",
            metricas = listOf(
                MetricaReporteUi("Productos bajo stock", bajoStock.toString()),
                MetricaReporteUi("Productos agotados", agotados.toString()),
                MetricaReporteUi("Unidades registradas", unidades.toString()),
                MetricaReporteUi("Productos activos", productos.size.toString())
            ),
            registros = registros
        )
    }

    private fun construirReporteCotizaciones(
        cotizaciones: List<CotizacionEntity>
    ): ReporteDetalleUi {
        val total = cotizaciones.sumOf { it.total }
        val aprobadas = cotizaciones.count { it.estado.equals("Aprobada", true) }
        val pendientes = cotizaciones.count { it.estado.equals("Pendiente", true) }
        val rechazadas = cotizaciones.count { it.estado.equals("Rechazada", true) }
        val conversion = if (cotizaciones.isEmpty()) 0.0 else aprobadas * 100.0 / cotizaciones.size

        val registros = cotizaciones
            .sortedByDescending { fechaOrdenable(it.fecha) }
            .map {
                RegistroReporteUi(
                    id = it.id,
                    titulo = it.folio.ifBlank { "Cotización ${it.id}" },
                    descripcion = it.descripcionTrabajo.ifBlank { "Sin descripción" },
                    valor = moneda(it.total),
                    fecha = it.fecha,
                    estado = it.estado
                )
            }

        return ReporteDetalleUi(
            tipo = "Cotizaciones",
            titulo = "Cotizaciones",
            descripcion = "Propuestas comerciales creadas en el periodo.",
            etiquetaPrincipal = "Importe cotizado",
            valorPrincipal = moneda(total),
            etiquetaSecundaria = "Cotizaciones",
            valorSecundario = cotizaciones.size.toString(),
            estado = "Actualizado",
            metricas = listOf(
                MetricaReporteUi("Aprobadas", aprobadas.toString()),
                MetricaReporteUi("Pendientes", pendientes.toString()),
                MetricaReporteUi("Rechazadas", rechazadas.toString()),
                MetricaReporteUi("Conversión", porcentaje(conversion))
            ),
            registros = registros
        )
    }

    private fun construirReporteClientes(
        clientesPeriodo: List<ClienteEntity>,
        todosLosClientes: List<ClienteEntity>,
        cotizaciones: List<CotizacionEntity>
    ): ReporteDetalleUi {
        val activos = todosLosClientes.count { it.clienteActivo && !it.eliminado }
        val inactivos = todosLosClientes.count { !it.clienteActivo && !it.eliminado }
        val conCotizaciones = cotizaciones.map { it.clienteId }.distinct().size
        val clienteConMasCotizaciones = cotizaciones
            .groupingBy { it.clienteId }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
            ?.let { id -> todosLosClientes.firstOrNull { it.id == id }?.nombre }
            ?: "Sin datos"

        val registros = clientesPeriodo
            .sortedByDescending { it.fechaRegistro }
            .map {
                RegistroReporteUi(
                    id = it.id,
                    titulo = it.nombre,
                    descripcion = it.empresa.ifBlank { it.tipoCliente },
                    valor = if (it.clienteActivo) "Activo" else "Inactivo",
                    fecha = fechaDesdeEpoch(it.fechaRegistro),
                    estado = it.estatus
                )
            }

        return ReporteDetalleUi(
            tipo = "Clientes",
            titulo = "Clientes",
            descripcion = "Altas de clientes y actividad comercial.",
            etiquetaPrincipal = "Nuevos clientes",
            valorPrincipal = clientesPeriodo.size.toString(),
            etiquetaSecundaria = "Activos",
            valorSecundario = activos.toString(),
            estado = "Actualizado",
            metricas = listOf(
                MetricaReporteUi("Clientes registrados", todosLosClientes.size.toString()),
                MetricaReporteUi("Clientes inactivos", inactivos.toString()),
                MetricaReporteUi("Con cotizaciones", conCotizaciones.toString()),
                MetricaReporteUi("Más cotizaciones", clienteConMasCotizaciones)
            ),
            registros = registros
        )
    }

    private fun construirReporteProyectos(
        proyectosPeriodo: List<ProyectoEntity>,
        todosLosProyectos: List<ProyectoEntity>
    ): ReporteDetalleUi {
        val activos = todosLosProyectos.count {
            it.estado.equals("En trabajo", true) || it.estado.equals("Pendiente", true)
        }
        val terminadosPeriodo = proyectosPeriodo.count { it.estado.equals("Terminado", true) }
        val presupuesto = proyectosPeriodo.sumOf { it.presupuestoEstimado }
        val costo = proyectosPeriodo.sumOf { it.costoTotal }
        val avancePromedio = if (proyectosPeriodo.isEmpty()) 0.0 else {
            proyectosPeriodo.map { it.avance }.average()
        }

        val registros = proyectosPeriodo
            .sortedByDescending { fechaOrdenable(it.fechaInicio) }
            .map {
                RegistroReporteUi(
                    id = it.id,
                    titulo = it.nombre,
                    descripcion = "Avance ${it.avance}% · ${it.estado}",
                    valor = moneda(it.costoTotal),
                    fecha = it.fechaInicio,
                    estado = it.estado
                )
            }

        return ReporteDetalleUi(
            tipo = "Proyectos",
            titulo = "Proyectos",
            descripcion = "Proyectos iniciados y costos registrados.",
            etiquetaPrincipal = "Proyectos del periodo",
            valorPrincipal = proyectosPeriodo.size.toString(),
            etiquetaSecundaria = "Activos",
            valorSecundario = activos.toString(),
            estado = "Actualizado",
            metricas = listOf(
                MetricaReporteUi("Presupuesto estimado", moneda(presupuesto)),
                MetricaReporteUi("Costo registrado", moneda(costo)),
                MetricaReporteUi("Terminados", terminadosPeriodo.toString()),
                MetricaReporteUi("Avance promedio", porcentaje(avancePromedio))
            ),
            registros = registros
        )
    }

    private fun construirReporteEmpleados(
        empleadosPeriodo: List<EmpleadoEntity>,
        todosLosEmpleados: List<EmpleadoEntity>
    ): ReporteDetalleUi {
        val activos = todosLosEmpleados.count { it.activo }
        val asignados = todosLosEmpleados.count { it.trabajoActual.isNotBlank() }
        val pagoActivo = todosLosEmpleados.filter { it.activo }.sumOf { it.salario }
        val modalidadPrincipal = valorMasFrecuente(todosLosEmpleados.map { it.tipoPago })

        val registros = empleadosPeriodo
            .sortedByDescending { fechaOrdenable(it.fechaIngreso) }
            .map {
                RegistroReporteUi(
                    id = it.id,
                    titulo = it.nombre,
                    descripcion = listOf(it.puesto, it.trabajoActual)
                        .filter { valor -> valor.isNotBlank() }
                        .joinToString(" · ")
                        .ifBlank { "Empleado registrado" },
                    valor = moneda(it.salario),
                    fecha = it.fechaIngreso,
                    estado = if (it.activo) "Activo" else "Inactivo"
                )
            }

        return ReporteDetalleUi(
            tipo = "Empleados",
            titulo = "Empleados",
            descripcion = "Personal registrado y esquema de pago.",
            etiquetaPrincipal = "Nuevos empleados",
            valorPrincipal = empleadosPeriodo.size.toString(),
            etiquetaSecundaria = "Activos",
            valorSecundario = activos.toString(),
            estado = "Actualizado",
            metricas = listOf(
                MetricaReporteUi("Empleados registrados", todosLosEmpleados.size.toString()),
                MetricaReporteUi("Pago total activo", moneda(pagoActivo)),
                MetricaReporteUi("Con trabajo asignado", asignados.toString()),
                MetricaReporteUi("Modalidad principal", modalidadPrincipal)
            ),
            registros = registros
        )
    }
}

class ReportesViewModelFactory(
    private val database: ArcshiftWeldingDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportesViewModel(database) as T
        }
        throw IllegalArgumentException(
            "ReportesViewModelFactory no puede crear: ${modelClass.name}"
        )
    }
}

private val FORMATOS_FECHA = listOf(
    DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    DateTimeFormatter.ofPattern("d/M/yyyy"),
    DateTimeFormatter.ISO_LOCAL_DATE,
    DateTimeFormatter.ofPattern("dd-MM-yyyy"),
    DateTimeFormatter.ofPattern("d-M-yyyy")
)

private fun parseFecha(texto: String): LocalDate? {
    val limpio = texto.trim()
        .substringBefore(' ')
        .substringBefore('T')

    if (limpio.isBlank()) return null

    for (formato in FORMATOS_FECHA) {
        try {
            return LocalDate.parse(limpio, formato)
        } catch (_: Exception) {
            // Se intenta con el siguiente formato compatible.
        }
    }
    return null
}

private fun fechaEnPeriodo(
    texto: String,
    periodo: PeriodoReporte,
    hoy: LocalDate
): Boolean {
    if (periodo == PeriodoReporte.TODO) return true
    val fecha = parseFecha(texto) ?: return false
    return fechaEnPeriodo(fecha, periodo, hoy)
}

private fun epochEnPeriodo(
    epoch: Long,
    periodo: PeriodoReporte,
    hoy: LocalDate
): Boolean {
    if (periodo == PeriodoReporte.TODO) return true
    val fecha = Instant.ofEpochMilli(epoch)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return fechaEnPeriodo(fecha, periodo, hoy)
}

private fun fechaEnPeriodo(
    fecha: LocalDate,
    periodo: PeriodoReporte,
    hoy: LocalDate
): Boolean {
    return when (periodo) {
        PeriodoReporte.TODO -> true
        PeriodoReporte.HOY -> fecha == hoy
        PeriodoReporte.SEMANA -> {
            val inicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val fin = inicio.plusDays(6)
            !fecha.isBefore(inicio) && !fecha.isAfter(fin)
        }
        PeriodoReporte.MES ->
            fecha.year == hoy.year && fecha.month == hoy.month
        PeriodoReporte.ANIO -> fecha.year == hoy.year
    }
}

private fun textoPeriodo(periodo: PeriodoReporte, hoy: LocalDate): String {
    return when (periodo) {
        PeriodoReporte.TODO -> "Todos los registros"
        PeriodoReporte.HOY -> "Hoy ${hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
        PeriodoReporte.SEMANA -> {
            val inicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val fin = inicio.plusDays(6)
            "${inicio.format(DateTimeFormatter.ofPattern("dd/MM"))} - ${fin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
        }
        PeriodoReporte.MES -> hoy.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "MX")))
        PeriodoReporte.ANIO -> hoy.year.toString()
    }
}

private fun fechaOrdenable(texto: String): Long {
    return parseFecha(texto)
        ?.atStartOfDay(ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()
        ?: 0L
}

private fun fechaDesdeEpoch(epoch: Long): String {
    return Instant.ofEpochMilli(epoch)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

private fun moneda(valor: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "MX")).format(valor)
}

private fun porcentaje(valor: Double): String {
    return String.format(Locale("es", "MX"), "%.1f%%", valor)
}

private fun valorMasFrecuente(valores: List<String>): String {
    return valores
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
        ?: "Sin datos"
}
