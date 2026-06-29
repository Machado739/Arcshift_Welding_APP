package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimientos_inventario",
    foreignKeys = [
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        ),
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
        Index(value = ["productoId"]),
        Index(value = ["clienteId"]),
        Index(value = ["cotizacionId"])
    ]
)
data class MovimientoInventarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val productoId: Int,
    val cotizacionId: Int?,
    val clienteId: Int?,

    val tipo: String,

    val cantidad: Int,

    val stockAnterior: Int,
    val stockNuevo: Int,

    val unidad: String,

    val fecha: String,
    val hora: String,

    val usuario: String = "Admin",
    val referencia: String = "",
    val observaciones: String = ""
)