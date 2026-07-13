package com.example.arcshiftwelding.notifications

import com.example.arcshiftwelding.data.local.dao.CotizacionDao
import com.example.arcshiftwelding.data.local.dao.PagoProgramadoDao
import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.local.relation.CotizacionConCliente
import com.example.arcshiftwelding.data.local.relation.PagoProgramadoConRelaciones
import com.example.arcshiftwelding.navigation.AppRoutes
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class NotificacionesRepository(
    private val pagoProgramadoDao: PagoProgramadoDao,
    private val cotizacionDao: CotizacionDao,
    private val productoDao: ProductoDao
) {

    fun observarNotificaciones(): Flow<List<NotificacionApp>> {
        return combine(
            pagoProgramadoDao.observarPagosPendientesParaNotificaciones(),
            cotizacionDao.obtenerCotizacionesConCliente(),
            productoDao.obtenerProductosBajoStock(),
            relojFecha()
        ) { pagos, cotizaciones, productos, fechaActual ->
            NotificacionesBuilder.construir(
                pagos = pagos,
                cotizaciones = cotizaciones,
                productos = productos,
                fechaActual = fechaActual
            )
        }
    }

    suspend fun obtenerNotificacionesActuales(
        fechaActual: LocalDate = LocalDate.now()
    ): List<NotificacionApp> {
        val pagos = pagoProgramadoDao
            .observarPagosPendientesParaNotificaciones()
            .first()

        val cotizaciones = cotizacionDao
            .obtenerCotizacionesConCliente()
            .first()

        val productos = productoDao
            .obtenerProductosBajoStock()
            .first()

        return NotificacionesBuilder.construir(
            pagos = pagos,
            cotizaciones = cotizaciones,
            productos = productos,
            fechaActual = fechaActual
        )
    }

    private fun relojFecha(): Flow<LocalDate> = flow {
        while (currentCoroutineContext().isActive) {
            emit(LocalDate.now())
            delay(60L * 60L * 1000L)
        }
    }
}

object NotificacionesBuilder {

    private const val DIAS_AVISO_PAGO = 7L
    private const val DIAS_AVISO_COTIZACION = 7L
    private const val MAX_DIAS_COTIZACION_VENCIDA = 30L

    private val localeMexico = Locale.forLanguageTag("es-MX")
    private val formatoMoneda = NumberFormat.getCurrencyInstance(localeMexico)
    private val formatoSalida = DateTimeFormatter.ofPattern("dd/MM/yyyy", localeMexico)

    private val formatosFecha = listOf(
        DateTimeFormatter.ofPattern("d/M/uuuu", localeMexico),
        DateTimeFormatter.ofPattern("dd/MM/uuuu", localeMexico),
        DateTimeFormatter.ofPattern("uuuu-MM-dd", localeMexico),
        DateTimeFormatter.ofPattern("d-M-uuuu", localeMexico),
        DateTimeFormatter.ofPattern("dd-MM-uuuu", localeMexico)
    )

    private val formatosFechaHora = listOf(
        DateTimeFormatter.ofPattern("d/M/uuuu H:mm", localeMexico),
        DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm", localeMexico),
        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", localeMexico),
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    )

    fun construir(
        pagos: List<PagoProgramadoConRelaciones>,
        cotizaciones: List<CotizacionConCliente>,
        productos: List<ProductoEntity>,
        fechaActual: LocalDate = LocalDate.now()
    ): List<NotificacionApp> {
        return buildList {
            addAll(construirPagos(pagos, fechaActual))
            addAll(construirCotizaciones(cotizaciones, fechaActual))
            addAll(construirStock(productos))
        }.sortedWith(
            compareBy<NotificacionApp> { it.prioridad.orden }
                .thenBy { it.fechaReferencia ?: LocalDate.MAX }
                .thenBy { it.titulo.lowercase(localeMexico) }
        )
    }

    fun resumir(notificaciones: List<NotificacionApp>): ResumenNotificaciones {
        return ResumenNotificaciones(
            pagos = notificaciones.count { it.tipo == TipoNotificacion.PAGO },
            cotizaciones = notificaciones.count { it.tipo == TipoNotificacion.COTIZACION },
            stock = notificaciones.count { it.tipo == TipoNotificacion.STOCK }
        )
    }

    private fun construirPagos(
        pagos: List<PagoProgramadoConRelaciones>,
        fechaActual: LocalDate
    ): List<NotificacionApp> {
        return pagos.mapNotNull { relacion ->
            val pago = relacion.pago
            if (!pago.activo || !pago.estado.equals("Pendiente", ignoreCase = true)) {
                return@mapNotNull null
            }

            val fecha = parsearFecha(pago.fechaProgramada) ?: return@mapNotNull null
            val dias = ChronoUnit.DAYS.between(fechaActual, fecha)

            if (dias > DIAS_AVISO_PAGO) {
                return@mapNotNull null
            }

            val referencia = when {
                !relacion.cliente?.nombre.isNullOrBlank() -> relacion.cliente?.nombre.orEmpty()
                !relacion.proyecto?.nombre.isNullOrBlank() -> relacion.proyecto?.nombre.orEmpty()
                else -> "Pago programado #${pago.id}"
            }

            val titulo = when {
                dias < 0 -> "Pago vencido"
                dias == 0L -> "Pago vence hoy"
                dias == 1L -> "Pago vence mañana"
                else -> "Pago vence en $dias días"
            }

            val prioridad = when {
                dias <= 0 -> PrioridadNotificacion.CRITICA
                dias <= 3 -> PrioridadNotificacion.ALTA
                else -> PrioridadNotificacion.MEDIA
            }

            val ruta = pago.ingresoAnticipoId
                ?.takeIf { it > 0 }
                ?.let { ingresoId -> AppRoutes.detalleIngreso(ingresoId) }
                ?: pago.proyectoId
                    ?.takeIf { it > 0 }
                    ?.let { proyectoId -> AppRoutes.detalleProyecto(proyectoId) }
                ?: AppRoutes.INGRESOS

            NotificacionApp(
                id = "pago_${pago.id}",
                tipo = TipoNotificacion.PAGO,
                prioridad = prioridad,
                titulo = titulo,
                descripcion = "$referencia · ${formatoMoneda.format(pago.montoProgramado)}",
                fechaReferencia = fecha,
                textoFecha = "Programado para ${fecha.format(formatoSalida)}",
                rutaDestino = ruta,
                diasRestantes = dias
            )
        }
    }

    private fun construirCotizaciones(
        cotizaciones: List<CotizacionConCliente>,
        fechaActual: LocalDate
    ): List<NotificacionApp> {
        return cotizaciones.mapNotNull { relacion ->
            val cotizacion = relacion.cotizacion

            if (!estadoCotizacionPendiente(cotizacion.estado)) {
                return@mapNotNull null
            }

            val vigencia = parsearFecha(cotizacion.vigencia) ?: return@mapNotNull null
            val dias = ChronoUnit.DAYS.between(fechaActual, vigencia)

            if (dias > DIAS_AVISO_COTIZACION || dias < -MAX_DIAS_COTIZACION_VENCIDA) {
                return@mapNotNull null
            }

            val titulo = when {
                dias < 0 -> "Cotización vencida"
                dias == 0L -> "Cotización vence hoy"
                dias == 1L -> "Cotización vence mañana"
                else -> "Cotización vence en $dias días"
            }

            val prioridad = when {
                dias <= 0 -> PrioridadNotificacion.CRITICA
                dias <= 3 -> PrioridadNotificacion.ALTA
                else -> PrioridadNotificacion.MEDIA
            }

            val cliente = relacion.cliente?.nombre
                ?.takeIf { it.isNotBlank() }
                ?: "Cliente sin nombre"

            val folio = cotizacion.folio.ifBlank { "COT-${cotizacion.id}" }

            NotificacionApp(
                id = "cotizacion_${cotizacion.id}",
                tipo = TipoNotificacion.COTIZACION,
                prioridad = prioridad,
                titulo = titulo,
                descripcion = "$folio · $cliente · ${formatoMoneda.format(cotizacion.total)}",
                fechaReferencia = vigencia,
                textoFecha = "Vigencia ${vigencia.format(formatoSalida)}",
                rutaDestino = AppRoutes.detalleCotizacion(cotizacion.id),
                diasRestantes = dias
            )
        }
    }

    private fun construirStock(
        productos: List<ProductoEntity>
    ): List<NotificacionApp> {
        return productos
            .asSequence()
            .filter { it.activo && it.stock <= it.stockMinimo }
            .map { producto ->
                val agotado = producto.stock <= 0
                NotificacionApp(
                    id = "stock_${producto.id}",
                    tipo = TipoNotificacion.STOCK,
                    prioridad = if (agotado) {
                        PrioridadNotificacion.CRITICA
                    } else {
                        PrioridadNotificacion.ALTA
                    },
                    titulo = if (agotado) "Producto agotado" else "Stock bajo",
                    descripcion = "${producto.nombre} · ${producto.stock} ${producto.unidad}",
                    textoFecha = "Mínimo configurado: ${producto.stockMinimo} ${producto.unidad}",
                    rutaDestino = AppRoutes.detalleProducto(producto.id)
                )
            }
            .toList()
    }

    private fun estadoCotizacionPendiente(estado: String): Boolean {
        val normalizado = estado
            .trim()
            .lowercase(localeMexico)

        return normalizado !in setOf(
            "aprobada",
            "aprobado",
            "rechazada",
            "rechazado",
            "cancelada",
            "cancelado"
        )
    }

    fun parsearFecha(valor: String): LocalDate? {
        val texto = valor.trim()
        if (texto.isBlank()) return null

        formatosFechaHora.forEach { formato ->
            try {
                return LocalDateTime.parse(texto, formato).toLocalDate()
            } catch (_: DateTimeParseException) {
                // Se prueban los demás formatos.
            }
        }

        val candidatos = buildList {
            add(texto)
            if (texto.contains('T')) add(texto.substringBefore('T'))
            if (texto.contains(' ')) add(texto.substringBefore(' '))
            if (texto.length >= 10) add(texto.take(10))
        }.distinct()

        candidatos.forEach { candidato ->
            formatosFecha.forEach { formato ->
                try {
                    return LocalDate.parse(candidato, formato)
                } catch (_: DateTimeParseException) {
                    // Se prueban los demás formatos.
                }
            }
        }

        return null
    }
}
