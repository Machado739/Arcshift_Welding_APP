package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pagos_programados",
    foreignKeys = [
        ForeignKey(
            entity = ProyectoEntity::class,
            parentColumns = ["id"],
            childColumns = ["proyectoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = IngresoEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingresoPagadoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["proyectoId"]),
        Index(value = ["clienteId"]),
        Index(value = ["ingresoPagadoId"])
    ]
)
data class PagoProgramadoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val proyectoId: Int,
    val clienteId: Int?,

    val fechaProgramada: String,
    val montoProgramado: Double,

    val estado: String = "Pendiente",
    val observaciones: String = "",

    val ingresoPagadoId: Int? = null,

    val fechaRegistro: String = "",
    val activo: Boolean = true
)