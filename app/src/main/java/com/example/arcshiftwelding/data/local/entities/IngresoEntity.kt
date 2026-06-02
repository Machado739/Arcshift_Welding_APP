package com.example.arcshiftwelding.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingresos")
data class IngresoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val concepto: String,
    val cliente: String,
    val monto: Double,
    val fecha: String,
    val observaciones: String = ""
)