package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val proveedor: String,
    val categoria: String,
    val monto: Double,
    val fecha: String,
    val metodoPago: String,
    val formaPago: String = "",
    val descripcion: String = "",
    val proyecto: String = "",
    val cotizacion: String = "",
    val cliente: String = "",
    val activo: Boolean = true
)