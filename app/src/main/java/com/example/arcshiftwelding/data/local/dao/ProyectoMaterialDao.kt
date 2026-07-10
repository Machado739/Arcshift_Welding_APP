package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.ProyectoMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoMaterialDao {

    @Insert
    suspend fun insertar(material: ProyectoMaterialEntity)

    @Update
    suspend fun actualizar(material: ProyectoMaterialEntity)

    @Delete
    suspend fun eliminar(material: ProyectoMaterialEntity)

    @Query("""
        SELECT * FROM proyecto_materiales
        WHERE proyectoId = :proyectoId
        ORDER BY fechaUso DESC
    """)
    fun obtenerPorProyecto(proyectoId: Int): Flow<List<ProyectoMaterialEntity>>

    @Query("""
        SELECT * FROM proyecto_materiales
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerPorId(id: Int): ProyectoMaterialEntity?

    @Query("""
        DELETE FROM proyecto_materiales
        WHERE proyectoId = :proyectoId
    """)
    suspend fun eliminarPorProyecto(proyectoId: Int)

    @Query("""
        SELECT COALESCE(SUM(subtotal), 0)
        FROM proyecto_materiales
        WHERE proyectoId = :proyectoId
    """)
    fun totalMaterialesPorProyecto(proyectoId: Int): Flow<Double>

    @Query("""
    SELECT * FROM proyecto_materiales
""")
    fun obtenerTodosMaterialesProyecto(): Flow<List<ProyectoMaterialEntity>>
}