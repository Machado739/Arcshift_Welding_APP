package com.example.arcshiftwelding.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos_inventario")
data class MovimientoInventarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val tipo: String, // Entrada o Salida
    val cantidad: Int,
    val fecha: String,
    val observacion: String = ""
)