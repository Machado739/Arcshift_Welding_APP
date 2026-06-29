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
    val subtotal: Double,
    val iva: Double,
    val total: Double,
    val estado: String,
    val fecha: String
)