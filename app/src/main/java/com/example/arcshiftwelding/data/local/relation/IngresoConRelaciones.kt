package com.example.arcshiftwelding.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.IngresoEntity

data class IngresoConRelaciones(
    @Embedded
    val ingreso: IngresoEntity,

    @Relation(
        parentColumn = "clienteId",
        entityColumn = "id"
    )
    val cliente: ClienteEntity?,

    @Relation(
        parentColumn = "cotizacionId",
        entityColumn = "id"
    )
    val cotizacion: CotizacionEntity?
)