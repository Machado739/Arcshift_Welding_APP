package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proyectos")
data class ProyectoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val clienteId: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val estado: String
)