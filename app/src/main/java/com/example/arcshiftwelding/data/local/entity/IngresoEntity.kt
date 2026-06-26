package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingresos")
data class IngresoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val concepto: String = "",
    val cliente: String = "",
    val trabajo: String = "",
    val folio: String = "",
    val fecha: String = "",

    val subtotal: Double = 0.0,
    val ivaPorcentaje: Double = 0.0,
    val iva: Double = 0.0,
    val total: Double = 0.0,
    val anticipo: Double = 0.0,
    val pendiente: Double = 0.0,

    val metodoPago: String = "",
    val formaPago: String = "",

    val observaciones: String = "",
    val cotizacion: String = "",
    val ordenTrabajo: String = "",
    val proyecto: String = "",

    val activo: Boolean = true
)