package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val categoria: String,
    val codigo: String,
    val ubicacion: String,

    val stock: Int,
    val unidad: String,

    val stockMinimo: Int = 0,
    val precioCompra: Double = 0.0,
    val precioVenta: Double = 0.0,

    val descripcion: String = "",
    val proveedor: String = "",

    val fechaRegistro: String = "",
    val activo: Boolean = true
)