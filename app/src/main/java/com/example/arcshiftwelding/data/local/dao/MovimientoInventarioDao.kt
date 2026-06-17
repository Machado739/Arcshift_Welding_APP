package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoInventarioDao {

    @Query("""
        SELECT * 
        FROM movimientos_inventario
        ORDER BY fecha DESC
    """)
    fun obtenerMovimientos(): Flow<List<MovimientoInventarioEntity>>

    @Query("""
        SELECT *
        FROM movimientos_inventario
        WHERE productoId = :productoId
        ORDER BY fecha DESC
    """)
    fun obtenerMovimientosProducto(
        productoId: Int
    ): Flow<List<MovimientoInventarioEntity>>

    @Insert
    suspend fun insertarMovimiento(
        movimiento: MovimientoInventarioEntity
    )

    @Delete
    suspend fun eliminarMovimiento(
        movimiento: MovimientoInventarioEntity
    )
}