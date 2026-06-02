package com.example.arcshiftwelding.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalle_cotizacion")
data class DetalleCotizacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cotizacionId: Int,
    val concepto: String,
    val cantidad: Double,
    val precioUnitario: Double,
    val importe: Double
)