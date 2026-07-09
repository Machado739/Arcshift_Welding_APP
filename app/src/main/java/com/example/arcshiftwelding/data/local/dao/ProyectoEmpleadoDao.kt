package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.ProyectoEmpleadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoEmpleadoDao {

    @Insert
    suspend fun insertar(proyectoEmpleado: ProyectoEmpleadoEntity)

    @Update
    suspend fun actualizar(proyectoEmpleado: ProyectoEmpleadoEntity)

    @Delete
    suspend fun eliminar(proyectoEmpleado: ProyectoEmpleadoEntity)

    @Query("""
        SELECT * FROM proyecto_empleados
        WHERE proyectoId = :proyectoId
        ORDER BY nombreEmpleado ASC
    """)
    fun obtenerPorProyecto(proyectoId: Int): Flow<List<ProyectoEmpleadoEntity>>

    @Query("""
        SELECT * FROM proyecto_empleados
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerPorId(id: Int): ProyectoEmpleadoEntity?

    @Query("""
        DELETE FROM proyecto_empleados
        WHERE proyectoId = :proyectoId
    """)
    suspend fun eliminarPorProyecto(proyectoId: Int)

    @Query("""
        SELECT COALESCE(SUM(costoCalculado), 0)
        FROM proyecto_empleados
        WHERE proyectoId = :proyectoId
    """)
    fun totalManoObraPorProyecto(proyectoId: Int): Flow<Double>
}