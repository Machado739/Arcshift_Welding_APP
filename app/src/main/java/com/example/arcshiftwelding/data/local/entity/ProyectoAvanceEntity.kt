package com.example.arcshiftwelding.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "proyecto_avances",
    foreignKeys = [
        ForeignKey(
            entity = ProyectoEntity::class,
            parentColumns = ["id"],
            childColumns = ["proyectoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("proyectoId")]
)
data class ProyectoAvanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val proyectoId: Int,
    val porcentaje: Int,
    val fecha: String,
    @ColumnInfo(defaultValue = "''")
    val comentario: String = "",
    @ColumnInfo(defaultValue = "'[]'")
    val fotosJson: String = "[]"
)
