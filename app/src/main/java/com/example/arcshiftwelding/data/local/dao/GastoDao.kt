package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    @Query("SELECT * FROM gastos ORDER BY id DESC")
    fun obtenerGastosActivos(): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    fun obtenerGastoPorIdFlow(id: Int): Flow<GastoEntity?>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    fun obtenerGastoPorId(id: Int): Flow<GastoEntity?>

    @Insert
    suspend fun insertarGasto(gasto: GastoEntity)

    @Update
    suspend fun actualizarGasto(gasto: GastoEntity)

    @Query("DELETE FROM gastos WHERE id = :id")
    suspend fun eliminarGasto(id: Int)
}