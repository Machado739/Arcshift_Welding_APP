package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "proyecto_empleados",
    primaryKeys = ["proyectoId", "empleadoId"],
    foreignKeys = [
        ForeignKey(
            entity = ProyectoEntity::class,
            parentColumns = ["id"],
            childColumns = ["proyectoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EmpleadoEntity::class,
            parentColumns = ["id"],
            childColumns = ["empleadoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("proyectoId"),
        Index("empleadoId")
    ]
)
data class ProyectoEmpleadoEntity(
    val proyectoId: Int,
    val empleadoId: Int,
    val rol: String = "",
    val tipoPago: String = "",
    val pagoAcordado: Double = 0.0,
    val fechaAsignacion: String = ""
)