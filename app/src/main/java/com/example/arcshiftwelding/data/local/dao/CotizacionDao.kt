package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CotizacionDao {

    @Query("SELECT * FROM cotizaciones ORDER BY id DESC")
    fun obtenerCotizaciones(): Flow<List<CotizacionEntity>>

    @Query("SELECT * FROM cotizaciones WHERE id = :id")
    suspend fun obtenerCotizacionPorId(id: Int): CotizacionEntity?

    @Insert
    suspend fun insertarCotizacion(cotizacion: CotizacionEntity)

    @Update
    suspend fun actualizarCotizacion(cotizacion: CotizacionEntity)

    @Delete
    suspend fun eliminarCotizacion(cotizacion: CotizacionEntity)
}