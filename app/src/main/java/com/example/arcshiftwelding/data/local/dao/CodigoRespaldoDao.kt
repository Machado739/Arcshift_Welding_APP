package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.arcshiftwelding.data.local.entity.CodigoRespaldoEntity

@Dao
interface CodigoRespaldoDao {

    @Insert
    suspend fun insertarTodos(codigos: List<CodigoRespaldoEntity>)

    @Query("DELETE FROM codigos_respaldo WHERE usuarioId = :usuarioId")
    suspend fun eliminarPorUsuario(usuarioId: Int)

    @Query(
        """
        SELECT * FROM codigos_respaldo
        WHERE usuarioId = :usuarioId
          AND codigoHash = :codigoHash
          AND usado = 0
        LIMIT 1
        """
    )
    suspend fun obtenerDisponible(
        usuarioId: Int,
        codigoHash: String
    ): CodigoRespaldoEntity?

    @Query(
        """
        UPDATE codigos_respaldo
        SET usado = 1, fechaUso = :fechaUso
        WHERE id = :codigoId AND usado = 0
        """
    )
    suspend fun marcarComoUsado(
        codigoId: Int,
        fechaUso: String
    ): Int

    @Query(
        """
        SELECT COUNT(*) FROM codigos_respaldo
        WHERE usuarioId = :usuarioId AND usado = 0
        """
    )
    suspend fun contarDisponibles(usuarioId: Int): Int

    @Transaction
    suspend fun reemplazarCodigos(
        usuarioId: Int,
        codigos: List<CodigoRespaldoEntity>
    ) {
        eliminarPorUsuario(usuarioId)
        insertarTodos(codigos)
    }
}
