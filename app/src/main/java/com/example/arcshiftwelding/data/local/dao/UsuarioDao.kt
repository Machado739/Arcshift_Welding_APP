package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entities.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios")
    fun obtenerUsuarios(): Flow<List<UsuarioEntity>>

    @Query("""
        SELECT * 
        FROM usuarios
        WHERE usuario = :usuario
        AND password = :password
        LIMIT 1
    """)
    suspend fun login(
        usuario: String,
        password: String
    ): UsuarioEntity?

    @Insert
    suspend fun insertarUsuario(
        usuario: UsuarioEntity
    )

    @Update
    suspend fun actualizarUsuario(
        usuario: UsuarioEntity
    )

    @Delete
    suspend fun eliminarUsuario(
        usuario: UsuarioEntity
    )
}