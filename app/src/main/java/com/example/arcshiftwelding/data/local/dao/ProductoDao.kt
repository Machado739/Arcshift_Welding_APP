package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.ProductoEntity
import com.example.arcshiftwelding.data.local.relation.MovimientoInventarioConRelaciones
import com.example.arcshiftwelding.data.local.relation.ProductoConMovimientos
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerProductos(): Flow<List<ProductoEntity>>



    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 
        AND (
            nombre LIKE '%' || :texto || '%' 
            OR codigo LIKE '%' || :texto || '%' 
            OR categoria LIKE '%' || :texto || '%'
            OR ubicacion LIKE '%' || :texto || '%'
            OR proveedor LIKE '%' || :texto || '%'
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
    suspend fun insertarProducto(producto: ProductoEntity): Long

    @Update
    suspend fun actualizarProducto(producto: ProductoEntity)

    @Delete
    suspend fun eliminarProducto(producto: ProductoEntity)

    @Query("UPDATE productos SET activo = 0 WHERE id = :productoId")
    suspend fun desactivarProducto(productoId: Int)

    @Query("UPDATE productos SET activo = 1 WHERE id = :productoId")
    suspend fun activarProducto(productoId: Int)

    @Query("SELECT * FROM productos WHERE activo = 0 ORDER BY nombre ASC")
    fun obtenerProductosInactivos(): Flow<List<ProductoEntity>>

    @Query("""
        UPDATE productos 
        SET stock = stock + :cantidad,
            estado = CASE 
                WHEN stock + :cantidad = 0 THEN 'Agotado'
                WHEN stock + :cantidad <= stockMinimo THEN 'Bajo Stock'
                ELSE 'En Stock'
            END
        WHERE id = :productoId
    """)
    suspend fun reponerStock(productoId: Int, cantidad: Int)

    @Query("""
        UPDATE productos 
        SET stock = stock - :cantidad,
            estado = CASE 
                WHEN stock - :cantidad = 0 THEN 'Agotado'
                WHEN stock - :cantidad <= stockMinimo THEN 'Bajo Stock'
                ELSE 'En Stock'
            END
        WHERE id = :productoId
        AND (
            permitirStockNegativo = 1 
            OR stock >= :cantidad
        )
    """)
    suspend fun reportarSalida(productoId: Int, cantidad: Int)

    @Transaction
    @Query("SELECT * FROM productos WHERE id = :productoId")
    fun obtenerProductoConMovimientos(productoId: Int): Flow<ProductoConMovimientos?>

    @Transaction
    @Query("SELECT * FROM movimientos_inventario WHERE productoId = :productoId ORDER BY id DESC")
    fun obtenerMovimientosConRelacionesPorProducto(
        productoId: Int
    ): Flow<List<MovimientoInventarioConRelaciones>>

    @Query("""
    SELECT codigo FROM productos
    WHERE codigo LIKE :prefijo || '-%'
    ORDER BY CAST(SUBSTR(codigo, LENGTH(:prefijo) + 2) AS INTEGER) DESC
    LIMIT 1
""")
    suspend fun obtenerUltimoCodigoPorPrefijo(prefijo: String): String?

    @Query("""
    SELECT * FROM productos
    WHERE id = :productoId
    LIMIT 1
""")
    suspend fun obtenerProductoPorId(productoId: Int): ProductoEntity?

    @Query("""
    UPDATE productos
    SET stock = stock - :cantidad,
        estado = CASE
            WHEN stock - :cantidad = 0 THEN 'Agotado'
            WHEN stock - :cantidad <= stockMinimo THEN 'Bajo Stock'
            ELSE 'En Stock'
        END
    WHERE id = :productoId
    AND (permitirStockNegativo = 1 OR stock >= :cantidad)
""")
    suspend fun descontarStockSiDisponible(
        productoId: Int,
        cantidad: Int
    ): Int

    @Query("""
    UPDATE productos
    SET stock = stock + :cantidad,
        estado = CASE
            WHEN stock + :cantidad = 0 THEN 'Agotado'
            WHEN stock + :cantidad <= stockMinimo THEN 'Bajo Stock'
            ELSE 'En Stock'
        END
    WHERE id = :productoId
""")
    suspend fun regresarStock(
        productoId: Int,
        cantidad: Int
    ): Int


}