package com.example.arcshiftwelding.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "proyectos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CotizacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["cotizacionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("clienteId"),
        Index("cotizacionId")
    ]
)
data class ProyectoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val clienteId: Int,
    val cotizacionId: Int? = null,
    val descripcion: String = "",
    val estado: String = "Pendiente",
    val fechaInicio: String = "",
    val fechaEstimadaFin: String = "",
    val fechaFinReal: String = "",
    val avance: Int = 0,
    val presupuestoEstimado: Double = 0.0,
    val costoMaterial: Double = 0.0,
    val costoManoObra: Double = 0.0,
    val costoTotal: Double = 0.0,
    val observaciones: String = "",
    @ColumnInfo(defaultValue = "'[]'")
    val imagenesJson: String = "[]"
)