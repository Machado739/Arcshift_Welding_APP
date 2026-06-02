package com.example.arcshiftwelding.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empresa")
data class EmpresaEntity(
    @PrimaryKey
    val id: Int = 1,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val logo: String = ""
)