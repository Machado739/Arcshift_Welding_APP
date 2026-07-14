package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun obtenerUsuarios(): Flow<List<UsuarioEntity>>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    @Query(
        """
        SELECT * FROM usuarios
        WHERE LOWER(TRIM(usuario)) = LOWER(TRIM(:usuario))
        LIMIT 1
        """
    )
    suspend fun obtenerPorUsuario(usuario: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :usuarioId LIMIT 1")
    suspend fun obtenerPorId(usuarioId: Int): UsuarioEntity?

    /** Conservado para compatibilidad con código anterior. */
    @Query(
        """
        SELECT * FROM usuarios
        WHERE usuario = :usuario AND password = :password
        LIMIT 1
        """
    )
    suspend fun login(
        usuario: String,
        password: String
    ): UsuarioEntity?

    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizarUsuario(usuario: UsuarioEntity)

    @Query("UPDATE usuarios SET password = :passwordHash WHERE id = :usuarioId")
    suspend fun actualizarPassword(
        usuarioId: Int,
        passwordHash: String
    )

    @Delete
    suspend fun eliminarUsuario(usuario: UsuarioEntity)
}
