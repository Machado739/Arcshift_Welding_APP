package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CotizacionDao {

    @Query("SELECT * FROM cotizaciones ORDER BY id DESC")
    fun obtenerCotizaciones(): Flow<List<CotizacionEntity>>

    @Query("SELECT * FROM cotizaciones WHERE id = :id LIMIT 1")
    fun observarCotizacionPorId(id: Int): Flow<CotizacionEntity?>

    @Query("SELECT * FROM cotizaciones WHERE id = :id LIMIT 1")
    suspend fun obtenerCotizacionPorId(id: Int): CotizacionEntity?

    @Insert
    suspend fun insertarCotizacion(cotizacion: CotizacionEntity): Long

    @Update
    suspend fun actualizarCotizacion(cotizacion: CotizacionEntity)

    @Delete
    suspend fun eliminarCotizacion(cotizacion: CotizacionEntity)

    @Query("DELETE FROM cotizaciones WHERE id = :id")
    suspend fun eliminarCotizacionPorId(id: Int)

    @Query("UPDATE cotizaciones SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)
}