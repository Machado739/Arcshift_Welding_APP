package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Query("""
        SELECT * FROM clientes 
        WHERE eliminado = 0 
        ORDER BY id DESC
    """)
    fun obtenerClientesActivos(): Flow<List<ClienteEntity>>

    @Query("""
        SELECT * FROM clientes 
        WHERE id = :clienteId 
        LIMIT 1
    """)
    fun obtenerClientePorId(clienteId: Int): Flow<ClienteEntity?>

    @Query("""
        SELECT * FROM clientes 
        WHERE id = :clienteId 
        LIMIT 1
    """)
    suspend fun obtenerClientePorIdUnaVez(clienteId: Int): ClienteEntity?

    @Insert
    suspend fun insertarCliente(cliente: ClienteEntity): Long

    @Update
    suspend fun actualizarCliente(cliente: ClienteEntity)

    @Query("""
        UPDATE clientes 
        SET eliminado = 1,
            clienteActivo = 0,
            estatus = 'Inactivo',
            ultimaActualizacion = :fecha
        WHERE id = :clienteId
    """)
    suspend fun eliminarCliente(clienteId: Int, fecha: Long)

    @Query("""
        SELECT COUNT(*) FROM clientes 
        WHERE eliminado = 0
    """)
    fun contarClientes(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM clientes 
        WHERE eliminado = 0 AND estatus = 'Activo'
    """)
    fun contarClientesActivos(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM clientes 
        WHERE eliminado = 0 AND estatus = 'Inactivo'
    """)
    fun contarClientesInactivos(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM clientes 
        WHERE eliminado = 0 AND estatus = 'Pendiente'
    """)
    fun contarClientesPendientes(): Flow<Int>
}