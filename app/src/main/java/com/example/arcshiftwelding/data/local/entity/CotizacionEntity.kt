package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cotizaciones",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["clienteId"])
    ]
)
data class CotizacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val folio: String,
    val clienteId: Int,
    val descripcionTrabajo: String,
    val proyecto: String = "",

    val subtotal: Double,

    val descuentoPorcentaje: Double = 0.0,
    val descuento: Double = 0.0,

    val ivaPorcentaje: Double = 16.0,
    val iva: Double,

    val total: Double,

    val anticipoPorcentaje: Double = 50.0,
    val anticipo: Double = 0.0,
    val saldo: Double = 0.0,

    val fecha: String,
    val vigencia: String = "",
    val observaciones: String = "",
    val estado: String,

    val fechaAprobacion: String = "",
    val fechaActualizacion: String = "",
    val archivosAdjuntosJson: String = "[]"
)