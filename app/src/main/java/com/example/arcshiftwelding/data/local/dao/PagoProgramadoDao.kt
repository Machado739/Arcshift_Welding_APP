package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.PagoProgramadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoProgramadoDao {

    @Insert
    suspend fun insertarPagoProgramado(pago: PagoProgramadoEntity): Long

    @Insert
    suspend fun insertarPagosProgramados(pagos: List<PagoProgramadoEntity>)

    @Update
    suspend fun actualizarPagoProgramado(pago: PagoProgramadoEntity)

    @Query("""
        SELECT * FROM pagos_programados
        WHERE proyectoId = :proyectoId
        AND activo = 1
        ORDER BY fechaProgramada ASC
    """)
    fun obtenerPagosPorProyecto(
        proyectoId: Int
    ): Flow<List<PagoProgramadoEntity>>

    @Query("""
        SELECT * FROM pagos_programados
        WHERE clienteId = :clienteId
        AND activo = 1
        ORDER BY fechaProgramada ASC
    """)
    fun obtenerPagosPorCliente(
        clienteId: Int
    ): Flow<List<PagoProgramadoEntity>>

    @Query("""
        SELECT * FROM pagos_programados
        WHERE ingresoAnticipoId = :ingresoId
        AND activo = 1
        ORDER BY fechaProgramada ASC
    """)
    fun obtenerPagosPorIngresoAnticipo(
        ingresoId: Int
    ): Flow<List<PagoProgramadoEntity>>

    @Query("""
        SELECT * FROM pagos_programados
        WHERE estado = 'Pendiente'
        AND activo = 1
        ORDER BY fechaProgramada ASC
    """)
    fun obtenerPagosPendientes(): Flow<List<PagoProgramadoEntity>>

    @Query("""
        UPDATE pagos_programados
        SET estado = 'Pagado',
            ingresoPagadoId = :ingresoId
        WHERE id = :pagoProgramadoId
    """)
    suspend fun marcarComoPagado(
        pagoProgramadoId: Int,
        ingresoId: Int
    )

    @Query("""
        UPDATE pagos_programados
        SET activo = 0
        WHERE id = :pagoProgramadoId
    """)
    suspend fun desactivarPagoProgramado(
        pagoProgramadoId: Int
    )

    @Query("""
        UPDATE pagos_programados
        SET activo = 0
        WHERE ingresoAnticipoId = :ingresoId
    """)
    suspend fun desactivarPagosPorIngresoAnticipo(
        ingresoId: Int
    )
}