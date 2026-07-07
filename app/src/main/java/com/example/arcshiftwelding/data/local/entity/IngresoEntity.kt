package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingresos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CotizacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["cotizacionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["clienteId"]),
        Index(value = ["cotizacionId"])
    ]
)
data class IngresoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val concepto: String,
    val clienteId: Int?,
    val cotizacionId: Int?,
    val trabajo: String,
    val folio: String,
    val fecha: String,

    val subtotal: Double,
    val ivaPorcentaje: Double,
    val iva: Double,
    val total: Double,
    val anticipo: Double,
    val pendiente: Double,

    val metodoPago: String,
    val formaPago: String,
    val observaciones: String,
    val ordenTrabajo: String,

    val proyecto: String = "",
    val activo: Boolean = true
)