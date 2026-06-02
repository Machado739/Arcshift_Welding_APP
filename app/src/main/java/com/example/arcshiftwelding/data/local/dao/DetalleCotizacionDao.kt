package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entities.DetalleCotizacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleCotizacionDao {

    @Query("SELECT * FROM detalle_cotizacion WHERE cotizacionId = :cotizacionId")
    fun obtenerDetallesPorCotizacion(
        cotizacionId: Int
    ): Flow<List<DetalleCotizacionEntity>>

    @Insert
    suspend fun insertarDetalle(
        detalle: DetalleCotizacionEntity
    )

    @Insert
    suspend fun insertarDetalles(
        detalles: List<DetalleCotizacionEntity>
    )

    @Update
    suspend fun actualizarDetalle(
        detalle: DetalleCotizacionEntity
    )

    @Delete
    suspend fun eliminarDetalle(
        detalle: DetalleCotizacionEntity
    )

    @Query("DELETE FROM detalle_cotizacion WHERE cotizacionId = :cotizacionId")
    suspend fun eliminarDetallesCotizacion(
        cotizacionId: Int
    )
}