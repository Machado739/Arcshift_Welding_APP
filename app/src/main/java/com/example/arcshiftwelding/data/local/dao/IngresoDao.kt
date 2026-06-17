package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.IngresoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDao {

    @Query("SELECT * FROM ingresos ORDER BY id DESC")
    fun obtenerIngresos(): Flow<List<IngresoEntity>>

    @Query("SELECT * FROM ingresos WHERE id = :id")
    suspend fun obtenerIngresoPorId(id: Int): IngresoEntity?

    @Insert
    suspend fun insertarIngreso(ingreso: IngresoEntity)

    @Update
    suspend fun actualizarIngreso(ingreso: IngresoEntity)

    @Delete
    suspend fun eliminarIngreso(ingreso: IngresoEntity)
}