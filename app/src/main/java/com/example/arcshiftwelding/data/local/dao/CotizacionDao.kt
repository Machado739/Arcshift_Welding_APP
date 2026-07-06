package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.CotizacionEntity
import com.example.arcshiftwelding.data.local.entity.DetalleCotizacionEntity
import com.example.arcshiftwelding.data.local.relation.CotizacionCompleta
import com.example.arcshiftwelding.data.local.relation.CotizacionConCliente
import kotlinx.coroutines.flow.Flow


@Dao
interface CotizacionDao {

    @Query("SELECT * FROM cotizaciones WHERE id = :id LIMIT 1")
    fun observarCotizacionPorId(id: Int): Flow<CotizacionEntity?>

    @Query("SELECT * FROM cotizaciones WHERE id = :id LIMIT 1")
    suspend fun obtenerCotizacionPorId(id: Int): CotizacionEntity?

    @Query("DELETE FROM cotizaciones WHERE id = :id")
    suspend fun eliminarCotizacionPorId(id: Int)

    @Query("UPDATE cotizaciones SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE cotizaciones SET folio = :folio WHERE id = :id")
    suspend fun actualizarFolioCotizacion(id: Int, folio: String)

    @Query("SELECT * FROM cotizaciones ORDER BY id DESC")
    fun obtenerCotizaciones(): Flow<List<CotizacionEntity>>

    @Transaction
    @Query("SELECT * FROM cotizaciones ORDER BY id DESC")
    fun obtenerCotizacionesConCliente(): Flow<List<CotizacionConCliente>>

    @Transaction
    @Query("SELECT * FROM cotizaciones WHERE id = :cotizacionId")
    fun obtenerCotizacionCompleta(cotizacionId: Int): Flow<CotizacionCompleta?>

    @Query("SELECT * FROM cotizaciones WHERE clienteId = :clienteId ORDER BY id DESC")
    fun obtenerCotizacionesPorCliente(clienteId: Int): Flow<List<CotizacionEntity>>

    @Insert
    suspend fun insertarCotizacion(cotizacion: CotizacionEntity): Long

    @Update
    suspend fun actualizarCotizacion(cotizacion: CotizacionEntity)

    @Delete
    suspend fun eliminarCotizacion(cotizacion: CotizacionEntity)

    @Insert
    suspend fun insertarDetalleCotizacion(detalle: DetalleCotizacionEntity): Long

    @Insert
    suspend fun insertarDetallesCotizacion(detalles: List<DetalleCotizacionEntity>)

    @Query("DELETE FROM detalle_cotizacion WHERE cotizacionId = :cotizacionId")
    suspend fun eliminarDetallesDeCotizacion(cotizacionId: Int)

    @Transaction
    suspend fun insertarCotizacionConDetalles(
        cotizacion: CotizacionEntity,
        detalles: List<DetalleCotizacionEntity>
    ) {
        val cotizacionId = insertarCotizacion(cotizacion).toInt()

        val detallesConCotizacion = detalles.map { detalle ->
            detalle.copy(
                id = 0,
                cotizacionId = cotizacionId
            )
        }

        insertarDetallesCotizacion(detallesConCotizacion)
    }

    @Transaction
    suspend fun actualizarCotizacionConDetalles(
        cotizacion: CotizacionEntity,
        detalles: List<DetalleCotizacionEntity>
    ) {
        actualizarCotizacion(cotizacion)
        eliminarDetallesDeCotizacion(cotizacion.id)

        val detallesActualizados = detalles.map { detalle ->
            detalle.copy(
                id = 0,
                cotizacionId = cotizacion.id
            )
        }

        insertarDetallesCotizacion(detallesActualizados)
    }


}