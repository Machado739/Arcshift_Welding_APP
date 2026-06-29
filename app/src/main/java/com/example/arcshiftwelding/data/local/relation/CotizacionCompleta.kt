package com.example.arcshiftwelding.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity

data class CotizacionCompleta(
    @Embedded
    val cotizacion: CotizacionEntity,

    @Relation(
        parentColumn = "clienteId",
        entityColumn = "id"
    )
    val cliente: ClienteEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "cotizacionId"
    )
    val detalles: List<DetalleCotizacionEntity>
)