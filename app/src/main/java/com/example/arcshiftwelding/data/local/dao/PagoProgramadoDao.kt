package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.PagoProgramadoEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction
import com.example.arcshiftwelding.data.local.relation.PagoProgramadoConRelaciones


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
    AND estado = 'Pendiente'
""")
    suspend fun desactivarPagosPendientesPorIngresoAnticipo(
        ingresoId: Int
    )

    @Transaction
    @Query("""
    SELECT pp.* FROM pagos_programados pp
    INNER JOIN ingresos i ON i.id = pp.ingresoAnticipoId
    WHERE pp.estado = 'Pendiente'
    AND pp.activo = 1
    AND i.activo = 1
    ORDER BY pp.fechaProgramada ASC
""")
    fun obtenerPagosPendientesConRelaciones(): Flow<List<PagoProgramadoConRelaciones>>

    @Query("""
    UPDATE pagos_programados
    SET activo = 0
    WHERE ingresoAnticipoId = :ingresoId
""")
    suspend fun desactivarTodosLosPagosPorIngresoAnticipo(
        ingresoId: Int
    )

    @Query("""
    SELECT * FROM pagos_programados
    WHERE ingresoAnticipoId = :ingresoId
    AND activo = 1
    ORDER BY fechaProgramada ASC
""")
    fun obtenerPagosPorIngreso(
        ingresoId: Int
    ): Flow<List<PagoProgramadoEntity>>

    @Query("""
    UPDATE pagos_programados
    SET estado = 'Pagado',
        fechaPago = :fechaPago,
        montoPagado = :montoPagado,
        metodoPago = :metodoPago,
        comprobanteUri = :comprobanteUri,
        tipoComprobante = :tipoComprobante
    WHERE id = :pagoId
""")
    suspend fun marcarPagoComoPagado(
        pagoId: Int,
        fechaPago: String,
        montoPagado: Double,
        metodoPago: String,
        comprobanteUri: String,
        tipoComprobante: String
    )

    @Transaction
    @Query("""
        SELECT * FROM pagos_programados
        WHERE estado = 'Pendiente'
        AND activo = 1
        ORDER BY fechaProgramada ASC
    """)
    fun observarPagosPendientesParaNotificaciones(): Flow<List<PagoProgramadoConRelaciones>>

    @Query("""
    SELECT * FROM pagos_programados
    WHERE activo = 1
""")
    fun obtenerTodosLosPagosActivos(): Flow<List<PagoProgramadoEntity>>
}