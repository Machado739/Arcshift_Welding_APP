package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    @Query("SELECT * FROM gastos WHERE activo = 1 ORDER BY id DESC")
    fun obtenerGastosActivos(): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    fun obtenerGastoPorIdFlow(id: Int): Flow<GastoEntity?>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    suspend fun obtenerGastoPorId(id: Int): GastoEntity?

    @Insert
    suspend fun insertarGasto(gasto: GastoEntity)

    @Update
    suspend fun actualizarGasto(gasto: GastoEntity)

    @Query("UPDATE gastos SET activo = 0 WHERE id = :id")
    suspend fun desactivarGasto(id: Int)
}