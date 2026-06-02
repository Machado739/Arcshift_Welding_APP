package com.example.arcshiftwelding.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val cantidad: Int,
    val unidad: String,
    val precioCompra: Double,
    val precioVenta: Double,
    val stockMinimo: Int
)