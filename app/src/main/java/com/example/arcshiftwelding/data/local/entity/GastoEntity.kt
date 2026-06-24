package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val concepto: String,
    val categoria: String,
    val fecha: String,
    val proveedor: String,
    val subtotal: Double,
    val ivaPorcentaje: Double,
    val iva: Double,
    val total: Double,
    val metodoPago: String,
    val formaPago: String,
    val telefonoProveedor: String? = null,
    val correoProveedor: String? = null,
    val rfcProveedor: String? = null,
    val observaciones: String? = null,
    val proyecto: String? = null,
    val cotizacion: String? = null,
    val cliente: String? = null
)