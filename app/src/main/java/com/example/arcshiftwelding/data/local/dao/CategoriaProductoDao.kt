package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.CategoriaProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaProductoDao {

    @Query("SELECT * FROM categorias_producto ORDER BY nombre")
    fun obtenerCategorias(): Flow<List<CategoriaProductoEntity>>

    @Insert
    suspend fun insertarCategoria(
        categoria: CategoriaProductoEntity
    )

    @Update
    suspend fun actualizarCategoria(
        categoria: CategoriaProductoEntity
    )

    @Delete
    suspend fun eliminarCategoria(
        categoria: CategoriaProductoEntity
    )
}