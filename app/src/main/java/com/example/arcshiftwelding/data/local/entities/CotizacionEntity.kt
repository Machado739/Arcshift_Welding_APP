package com.example.arcshiftwelding.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cotizaciones")
data class CotizacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val folio: String,
    val cliente: String,
    val descripcionTrabajo: String,
    val subtotal: Double,
    val iva: Double,
    val total: Double,
    val fecha: String,
    val estado: String // Pendiente, Aprobada, Rechazada
)