package com.example.arcshiftwelding.data.repository

data class ResumenCostosProyecto(
    val precioCotizado: Double = 0.0,
    val costoMateriales: Double = 0.0,
    val costoManoObra: Double = 0.0,
    val costosAdicionales: Double = 0.0
) {
    val costoTotal: Double
        get() = costoMateriales + costoManoObra + costosAdicionales

    val utilidad: Double
        get() = precioCotizado - costoTotal
}