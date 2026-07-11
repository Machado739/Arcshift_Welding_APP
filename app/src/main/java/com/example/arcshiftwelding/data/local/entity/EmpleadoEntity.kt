package com.example.arcshiftwelding.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empleados")
data class EmpleadoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val puesto: String,
    val salario: Double,
    val fechaIngreso: String,
    val activo: Boolean = true,
    val tipoPago: String = "Día",
    val direccion: String = "",
    val porcentajeContrato: String = "",
    val trabajoActual: String = "",
    val notas: String = "",
    @ColumnInfo(defaultValue = "''")
    val fotoUri: String = ""
)