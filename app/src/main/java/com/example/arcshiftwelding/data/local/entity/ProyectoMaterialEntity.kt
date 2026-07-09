package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "proyecto_materiales",
    foreignKeys = [
        ForeignKey(
            entity = ProyectoEntity::class,
            parentColumns = ["id"],
            childColumns = ["proyectoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductoEntity::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index("proyectoId"),
        Index("productoId")
    ]
)
data class ProyectoMaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val proyectoId: Int,
    val productoId: Int,

    val nombreProducto: String,
    val codigoProducto: String = "",
    val categoria: String = "",

    val cantidadUsada: Double,
    val unidad: String = "",

    val costoUnitario: Double,
    val subtotal: Double,

    val fechaUso: String = "",
    val observaciones: String = ""
)