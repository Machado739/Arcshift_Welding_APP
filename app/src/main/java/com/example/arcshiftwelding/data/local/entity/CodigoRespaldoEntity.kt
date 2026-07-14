package com.example.arcshiftwelding.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "codigos_respaldo",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuarioId"]),
        Index(value = ["usuarioId", "codigoHash"], unique = true)
    ]
)
data class CodigoRespaldoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val codigoHash: String,
    val usado: Boolean = false,
    val fechaCreacion: String,
    val fechaUso: String = ""
)
