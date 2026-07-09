package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.ClienteEntity
import com.example.arcshiftwelding.data.local.relation.ClienteConCotizaciones
import com.example.arcshiftwelding.data.local.relation.ClienteConCantidadCotizaciones
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

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

    @Transaction
    @Query("SELECT * FROM clientes WHERE id = :clienteId")
    fun obtenerClienteConCotizaciones(clienteId: Int): Flow<ClienteConCotizaciones?>

    @Query("SELECT * FROM clientes WHERE eliminado = 0 AND clienteActivo = 1 ORDER BY nombre ASC")
    fun obtenerClientesActivos(): Flow<List<ClienteEntity>>

    @Query("SELECT COUNT(*) FROM cotizaciones WHERE clienteId = :clienteId")
    fun contarCotizacionesPorCliente(clienteId: Int): Flow<Int>



    @Query("""
    SELECT 
        c.*,
        COUNT(co.id) AS cantidadCotizaciones
    FROM clientes c
    LEFT JOIN cotizaciones co 
        ON co.clienteId = c.id
    WHERE c.eliminado = 0
    GROUP BY c.id
    ORDER BY c.id DESC
""")
    fun obtenerClientesConCantidadCotizaciones(): Flow<List<ClienteConCantidadCotizaciones>>


    ///test
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarClientes(clientes: List<ClienteEntity>)
}

