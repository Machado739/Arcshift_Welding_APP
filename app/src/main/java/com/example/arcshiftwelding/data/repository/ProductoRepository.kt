package com.example.arcshiftwelding.data.repository

import com.example.arcshiftwelding.data.local.dao.ProductoDao
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

class ProductoRepository(
    private val productoDao: ProductoDao
) {

    val productos: Flow<List<ProductoEntity>> =
        productoDao.obtenerProductos()

    val productosBajoStock: Flow<List<ProductoEntity>> =
        productoDao.obtenerProductosBajoStock()

    val productosInactivos: Flow<List<ProductoEntity>> =
        productoDao.obtenerProductosInactivos()

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

    suspend fun activarProducto(productoId: Int) {
        productoDao.activarProducto(productoId)
    }

    suspend fun reponerStock(productoId: Int, cantidad: Int) {
        productoDao.reponerStock(productoId, cantidad)
    }

    suspend fun reportarSalida(productoId: Int, cantidad: Int) {
        productoDao.reportarSalida(productoId, cantidad)
    }

    suspend fun obtenerSiguienteCodigoPorCategoria(categoria: String): String {
        val prefijo = obtenerPrefijoPorCategoria(categoria)

        val ultimoCodigo = productoDao.obtenerUltimoCodigoPorPrefijo(prefijo)

        val ultimoNumero = ultimoCodigo
            ?.substringAfter("-")
            ?.toIntOrNull() ?: 0

        val siguienteNumero = ultimoNumero + 1

        return "$prefijo-${siguienteNumero.toString().padStart(4, '0')}"
    }

    private fun obtenerPrefijoPorCategoria(categoria: String): String {
        return when (categoria.trim()) {
            "Materiales" -> "MAT"
            "Herramientas" -> "HER"
            "Consumibles" -> "CON"
            "Seguridad" -> "SEG"
            else -> "PRO"
        }
    }
}