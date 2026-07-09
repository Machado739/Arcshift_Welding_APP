package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "proyecto_costos",
    indices = [
        Index(value = ["proyectoId"])
    ]
)
data class ProyectoCostoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val proyectoId: Int,

    val tipo: String = "",
    // Transporte, Servicio, Herramienta, Viáticos, Otro

    val descripcion: String,
    val monto: Double,

    val fecha: String = "",

    val comprobanteUri: String? = null,
    val observaciones: String = ""
)