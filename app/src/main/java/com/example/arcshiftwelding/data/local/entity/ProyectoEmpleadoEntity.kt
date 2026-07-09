package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val proyectoId: Int,
    val empleadoId: Int,

    val nombreEmpleado: String,
    val puesto: String = "",

    val tipoPago: String = "",
    // Día, Semana, Porcentaje, Trabajo, Hora

    val pagoAcordado: Double = 0.0,

    val diasTrabajados: Double = 0.0,
    val horasTrabajadas: Double = 0.0,
    val porcentaje: Double = 0.0,

    val costoCalculado: Double = 0.0,

    val fechaAsignacion: String = "",
    val estado: String = "Asignado",
    // Asignado, En trabajo, Finalizado

    val observaciones: String = ""
)