package com.example.arcshiftwelding.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import com.example.arcshiftwelding.data.local.entity.ProductoEntity

data class ProductoConMovimientos(
    @Embedded
    val producto: ProductoEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "productoId"
    )
    val movimientos: List<MovimientoInventarioEntity>
)