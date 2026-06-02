package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entities.GastoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    @Query("SELECT * FROM gastos ORDER BY id DESC")
    fun obtenerGastos(): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE id = :id")
    suspend fun obtenerGastoPorId(id: Int): GastoEntity?

    @Insert
    suspend fun insertarGasto(gasto: GastoEntity)

    @Update
    suspend fun actualizarGasto(gasto: GastoEntity)

    @Delete
    suspend fun eliminarGasto(gasto: GastoEntity)
}