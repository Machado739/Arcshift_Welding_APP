package com.example.arcshiftwelding.notifications

import java.time.LocalDate

enum class TipoNotificacion {
    PAGO,
    COTIZACION,
    STOCK
}

enum class PrioridadNotificacion(val orden: Int) {
    CRITICA(0),
    ALTA(1),
    MEDIA(2)
}

data class NotificacionApp(
    val id: String,
    val tipo: TipoNotificacion,
    val prioridad: PrioridadNotificacion,
    val titulo: String,
    val descripcion: String,
    val fechaReferencia: LocalDate? = null,
    val textoFecha: String = "",
    val rutaDestino: String,
    val diasRestantes: Long? = null
)

data class ResumenNotificaciones(
    val pagos: Int,
    val cotizaciones: Int,
    val stock: Int
) {
    val total: Int
        get() = pagos + cotizaciones + stock

    companion object {
        val VACIO = ResumenNotificaciones(
            pagos = 0,
            cotizaciones = 0,
            stock = 0
        )
    }
}
