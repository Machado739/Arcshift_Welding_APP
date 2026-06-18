package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos_inventario")
data class MovimientoInventarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val productoId: Int,

    val tipo: String, // "Entrada", "Salida", "Registro inicial", "Ajuste"

    val cantidad: Int,

    val stockAnterior: Int,
    val stockNuevo: Int,

    val unidad: String,

    val fecha: String,
    val hora: String,

    val usuario: String = "Admin",
    val referencia: String = "",
    val observaciones: String = ""
)