package com.example.arcshiftwelding.utils

data class ResumenCotizacionCalculado(
    val subtotal: Double,
    val descuento: Double,
    val iva: Double,
    val total: Double,
    val anticipo: Double,
    val saldo: Double
)

fun calcularResumenCotizacion(
    subtotalConceptos: Double,
    descuentoPorcentaje: Double,
    ivaPorcentaje: Double,
    anticipoPorcentaje: Double
): ResumenCotizacionCalculado {
    val descuento = subtotalConceptos * (descuentoPorcentaje / 100.0)
    val baseIva = subtotalConceptos - descuento
    val iva = baseIva * (ivaPorcentaje / 100.0)
    val total = baseIva + iva
    val anticipo = total * (anticipoPorcentaje / 100.0)
    val saldo = total - anticipo

    return ResumenCotizacionCalculado(
        subtotal = subtotalConceptos,
        descuento = descuento,
        iva = iva,
        total = total,
        anticipo = anticipo,
        saldo = saldo
    )
}