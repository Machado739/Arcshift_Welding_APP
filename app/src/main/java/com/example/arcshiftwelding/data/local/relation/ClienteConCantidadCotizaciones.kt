package com.example.arcshiftwelding.data.local.relation

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.arcshiftwelding.data.local.entity.ClienteEntity

data class ClienteConCantidadCotizaciones(
    @Embedded
    val cliente: ClienteEntity,

    @ColumnInfo(name = "cantidadCotizaciones")
    val cantidadCotizaciones: Int
)