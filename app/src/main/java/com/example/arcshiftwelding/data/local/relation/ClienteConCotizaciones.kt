package com.example.arcshiftwelding.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity

data class ClienteConCotizaciones(
    @Embedded
    val cliente: ClienteEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "clienteId"
    )
    val cotizaciones: List<CotizacionEntity>
)