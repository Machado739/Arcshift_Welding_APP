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


    @Query("""
    SELECT COUNT(*) 
    FROM proyecto_empleados
    WHERE proyectoId = :proyectoId
    AND empleadoId = :empleadoId
""")
    suspend fun existeEmpleadoEnProyecto(
        proyectoId: Int,
        empleadoId: Int
    ): Int

    @Query("""
    UPDATE proyecto_empleados
    SET diasTrabajados = :diasTrabajados,
        horasTrabajadas = :horasTrabajadas,
        porcentaje = :porcentaje,
        costoCalculado = :costoCalculado,
        observaciones = :observaciones
    WHERE id = :id
""")
    suspend fun actualizarDatosEmpleadoProyecto(
        id: Int,
        diasTrabajados: Double,
        horasTrabajadas: Double,
        porcentaje: Double,
        costoCalculado: Double,
        observaciones: String
    )

    @Query("""
    SELECT * FROM proyecto_empleados
    WHERE id = :empleadoProyectoId
    LIMIT 1
""")
    fun observarEmpleadoProyectoPorId(
        empleadoProyectoId: Int
    ): Flow<ProyectoEmpleadoEntity?>

    @Query("""
    UPDATE proyecto_empleados
    SET diasTrabajados = :diasTrabajados,
        horasTrabajadas = :horasTrabajadas,
        porcentaje = :porcentaje,
        costoCalculado = :costoCalculado,
        observaciones = :observaciones
    WHERE id = :empleadoProyectoId
""")
    suspend fun actualizarEmpleadoProyecto(
        empleadoProyectoId: Int,
        diasTrabajados: Double,
        horasTrabajadas: Double,
        porcentaje: Double,
        costoCalculado: Double,
        observaciones: String
    )

    @Query("""
    DELETE FROM proyecto_empleados
    WHERE id = :empleadoProyectoId
""")
    suspend fun eliminarEmpleadoProyectoPorId(
        empleadoProyectoId: Int
    )

    @Query("""
    SELECT * FROM proyecto_empleados
""")
    fun obtenerTodosEmpleadosProyecto(): Flow<List<ProyectoEmpleadoEntity>>

}