package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity

@Entity(
    tableName = "gastos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = CotizacionEntity::class,
            parentColumns = ["id"],
            childColumns = ["cotizacionId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ProyectoEntity::class,
            parentColumns = ["id"],
            childColumns = ["proyectoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["clienteId"]),
        Index(value = ["cotizacionId"]),
        Index(value = ["proyectoId"])
    ]
)
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val proyectoId: Int? = null,
    val proyectoNombre: String? = "",
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

    val clienteId: Int?,
    val cotizacionId: Int?

)