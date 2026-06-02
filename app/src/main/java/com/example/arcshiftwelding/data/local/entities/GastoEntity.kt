package com.example.arcshiftwelding.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val concepto: String,
    val categoria: String,
    val monto: Double,
    val fecha: String,
    val observaciones: String = ""
)