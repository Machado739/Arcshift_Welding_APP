package com.example.arcshiftwelding.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val unidadMedida: String,
    val codigoSku: String,
    val ubicacion: String,
    val descripcion: String,
    val stockActual: Int,
    val stockMinimo: Int,
    val stockMaximo: Int?,
    val estado: String,
    val costoUnitario: Double,
    val proveedor: String?,
    val notas: String?,
    val permitirStockNegativo: Boolean,
    val productoActivo: Boolean,
    val imagenUri: String?
)