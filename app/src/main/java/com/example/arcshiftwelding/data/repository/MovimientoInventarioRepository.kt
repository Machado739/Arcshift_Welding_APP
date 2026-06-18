package com.example.arcshiftwelding.data.repository

import com.example.arcshiftwelding.data.local.dao.MovimientoInventarioDao
import com.example.arcshiftwelding.data.local.entity.MovimientoInventarioEntity
import kotlinx.coroutines.flow.Flow

class MovimientoInventarioRepository(
    private val movimientoInventarioDao: MovimientoInventarioDao
) {

    fun obtenerMovimientosRecientes(productoId: Int): Flow<List<MovimientoInventarioEntity>> {
        return movimientoInventarioDao.obtenerMovimientosRecientes(productoId)
    }

    fun obtenerMovimientosPorProducto(productoId: Int): Flow<List<MovimientoInventarioEntity>> {
        return movimientoInventarioDao.obtenerMovimientosPorProducto(productoId)
    }

    suspend fun insertarMovimiento(movimiento: MovimientoInventarioEntity) {
        movimientoInventarioDao.insertarMovimiento(movimiento)
    }
}