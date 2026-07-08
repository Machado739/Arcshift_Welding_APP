package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.IngresoEntity
import com.example.arcshiftwelding.data.local.relation.IngresoConRelaciones
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDao {

    @Query("SELECT * FROM ingresos WHERE activo = 1 ORDER BY id DESC")
    fun obtenerIngresosActivos(): Flow<List<IngresoEntity>>

    @Query("SELECT * FROM ingresos WHERE id = :ingresoId AND activo = 1 LIMIT 1")
    fun obtenerIngresoPorId(ingresoId: Int): Flow<IngresoEntity?>

    @Query("SELECT * FROM ingresos WHERE id = :ingresoId AND activo = 1 LIMIT 1")
    suspend fun obtenerIngresoPorIdDirecto(ingresoId: Int): IngresoEntity?

    @Insert
    suspend fun insertarIngreso(ingreso: IngresoEntity)

    @Update
    suspend fun actualizarIngreso(ingreso: IngresoEntity)

    @Query("UPDATE ingresos SET activo = 0 WHERE id = :ingresoId")
    suspend fun desactivarIngreso(ingresoId: Int)

    @Transaction
    @Query("SELECT * FROM ingresos WHERE activo = 1 ORDER BY id DESC")
    fun obtenerIngresosConRelaciones(): Flow<List<IngresoConRelaciones>>

    @Transaction
    @Query("SELECT * FROM ingresos WHERE id = :ingresoId")
    fun obtenerIngresoConRelaciones(ingresoId: Int): Flow<IngresoConRelaciones?>

    @Query("SELECT * FROM ingresos WHERE clienteId = :clienteId AND activo = 1 ORDER BY id DESC")
    fun obtenerIngresosPorCliente(clienteId: Int): Flow<List<IngresoEntity>>

    @Query("SELECT * FROM ingresos WHERE cotizacionId = :cotizacionId AND activo = 1 ORDER BY id DESC")
    fun obtenerIngresosPorCotizacion(cotizacionId: Int): Flow<List<IngresoEntity>>

    @Query("SELECT * FROM ingresos WHERE proyectoId = :proyectoId AND activo = 1 ORDER BY id DESC")
    fun obtenerIngresosPorProyecto(proyectoId: Int): Flow<List<IngresoEntity>>



}