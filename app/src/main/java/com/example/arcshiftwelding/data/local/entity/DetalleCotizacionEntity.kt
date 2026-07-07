package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "detalle_cotizacion",
    foreignKeys = [
        ForeignKey(
            entity = CotizacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["cotizacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["cotizacionId"])
    ]
)
data class DetalleCotizacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cotizacionId: Int,

    val tipo: String = "Materiales",
    val descripcion: String,

    val cantidad: Double,
    val unidad: String = "Pza",
    val precioUnitario: Double,
    val total: Double
)