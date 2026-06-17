package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Query("SELECT * FROM clientes ORDER BY id DESC")
    fun obtenerClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun obtenerClientePorId(id: Int): ClienteEntity?

    @Insert
    suspend fun insertarCliente(cliente: ClienteEntity)

    @Update
    suspend fun actualizarCliente(cliente: ClienteEntity)

    @Delete
    suspend fun eliminarCliente(cliente: ClienteEntity)
}