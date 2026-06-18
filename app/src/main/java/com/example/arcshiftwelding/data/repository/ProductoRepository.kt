package com.example.arcshiftwelding.data.repository

import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

class ProductoRepository(
    private val productoDao: ProductoDao
) {

    val productos: Flow<List<ProductoEntity>> = productoDao.obtenerProductos()

    val productosBajoStock: Flow<List<ProductoEntity>> = productoDao.obtenerProductosBajoStock()

    fun buscarProductos(texto: String): Flow<List<ProductoEntity>> {
        return productoDao.buscarProductos(texto)
    }

    fun obtenerProductosPorCategoria(categoria: String): Flow<List<ProductoEntity>> {
        return productoDao.obtenerProductosPorCategoria(categoria)
    }

    suspend fun obtenerProductoPorId(productoId: Int): ProductoEntity? {
        return productoDao.obtenerProductoPorId(productoId)
    }

    suspend fun insertarProducto(producto: ProductoEntity): Long {
        return productoDao.insertarProducto(producto)
    }

    suspend fun actualizarProducto(producto: ProductoEntity) {
        productoDao.actualizarProducto(producto)
    }

    suspend fun eliminarProducto(producto: ProductoEntity) {
        productoDao.eliminarProducto(producto)
    }

    suspend fun desactivarProducto(productoId: Int) {
        productoDao.desactivarProducto(productoId)
    }

    suspend fun reponerStock(productoId: Int, cantidad: Int) {
        productoDao.reponerStock(productoId, cantidad)
    }

    suspend fun reportarSalida(productoId: Int, cantidad: Int) {
        productoDao.reportarSalida(productoId, cantidad)
    }
}