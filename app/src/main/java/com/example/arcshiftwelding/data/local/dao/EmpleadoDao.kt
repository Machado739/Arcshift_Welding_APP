package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.EmpleadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpleadoDao {

    @Query("SELECT * FROM empleados ORDER BY id DESC")
    fun obtenerEmpleados(): Flow<List<EmpleadoEntity>>

    @Query("SELECT * FROM empleados WHERE id = :id")
    suspend fun obtenerEmpleadoPorId(id: Int): EmpleadoEntity?

    @Insert
    suspend fun insertarEmpleado(empleado: EmpleadoEntity)

    @Update
    suspend fun actualizarEmpleado(empleado: EmpleadoEntity)

    @Delete
    suspend fun eliminarEmpleado(empleado: EmpleadoEntity)
}