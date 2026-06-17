package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :productoId LIMIT 1")
    suspend fun obtenerProductoPorId(productoId: Int): ProductoEntity?

    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 
        AND (
            nombre LIKE '%' || :texto || '%' 
            OR codigo LIKE '%' || :texto || '%' 
            OR categoria LIKE '%' || :texto || '%'
        )
        ORDER BY nombre ASC
    """)
    fun buscarProductos(texto: String): Flow<List<ProductoEntity>>

    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 
        AND categoria = :categoria 
        ORDER BY nombre ASC
    """)
    fun obtenerProductosPorCategoria(categoria: String): Flow<List<ProductoEntity>>

    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 
        AND stock <= stockMinimo 
        ORDER BY stock ASC
    """)
    fun obtenerProductosBajoStock(): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: ProductoEntity)

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)

    @Query("UPDATE productos SET activo = 0 WHERE id = :productoId")
    suspend fun desactivarProducto(productoId: Int)

    @Query("UPDATE productos SET stock = stock + :cantidad WHERE id = :productoId")
    suspend fun reponerStock(productoId: Int, cantidad: Int)

    @Query("UPDATE productos SET stock = stock - :cantidad WHERE id = :productoId AND stock >= :cantidad")
    suspend fun reportarSalida(productoId: Int, cantidad: Int)
}