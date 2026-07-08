package com.example.arcshiftwelding.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.entity.IngresoEntity
import com.example.arcshiftwelding.data.local.entity.PagoProgramadoEntity
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity

data class PagoProgramadoConRelaciones(
    @Embedded
    val pago: PagoProgramadoEntity,

    @Relation(
        parentColumn = "clienteId",
        entityColumn = "id"
    )
    val cliente: ClienteEntity?,

    @Relation(
        parentColumn = "proyectoId",
        entityColumn = "id"
    )
    val proyecto: ProyectoEntity?,

    @Relation(
        parentColumn = "ingresoAnticipoId",
        entityColumn = "id"
    )
    val ingresoAnticipo: IngresoEntity?
)