package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.GastoEntity
import com.example.arcshiftwelding.data.local.relation.GastoConRelaciones
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    @Query("SELECT * FROM gastos ORDER BY id DESC")
    fun obtenerGastosActivos(): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    fun obtenerGastoPorIdFlow(id: Int): Flow<GastoEntity?>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    fun obtenerGastoPorId(id: Int): Flow<GastoEntity?>

    @Insert
    suspend fun insertarGasto(gasto: GastoEntity)

    @Update
    suspend fun actualizarGasto(gasto: GastoEntity)

    @Query("DELETE FROM gastos WHERE id = :id")
    suspend fun eliminarGasto(id: Int)

    @Transaction
    @Query("SELECT * FROM gastos ORDER BY id DESC")
    fun obtenerGastosConRelaciones(): Flow<List<GastoConRelaciones>>

    @Transaction
    @Query("SELECT * FROM gastos WHERE id = :gastoId LIMIT 1")
    fun obtenerGastoConRelaciones(gastoId: Int): Flow<GastoConRelaciones?>

    @Query("SELECT * FROM gastos WHERE clienteId = :clienteId ORDER BY id DESC")
    fun obtenerGastosPorCliente(clienteId: Int): Flow<List<GastoEntity>>

    @Query("SELECT * FROM gastos WHERE cotizacionId = :cotizacionId ORDER BY id DESC")
    fun obtenerGastosPorCotizacion(cotizacionId: Int): Flow<List<GastoEntity>>

    @Query("""
    SELECT * FROM gastos
    WHERE proyectoId = :proyectoId
    ORDER BY fecha DESC
""")
    fun obtenerGastosPorProyecto(proyectoId: Int): Flow<List<GastoEntity>>

    @Query("""
    SELECT COALESCE(SUM(total), 0)
    FROM gastos
    WHERE proyectoId = :proyectoId
""")
    fun totalGastosPorProyecto(proyectoId: Int): Flow<Double>

}