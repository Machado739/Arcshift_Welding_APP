package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.arcshiftwelding.data.local.entity.ProyectoCostoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoCostoDao {

    @Insert
    suspend fun insertar(costo: ProyectoCostoEntity)

    @Update
    suspend fun actualizar(costo: ProyectoCostoEntity)

    @Delete
    suspend fun eliminar(costo: ProyectoCostoEntity)

    @Query("""
        SELECT * FROM proyecto_costos
        WHERE proyectoId = :proyectoId
        ORDER BY fecha DESC
    """)
    fun obtenerPorProyecto(proyectoId: Int): Flow<List<ProyectoCostoEntity>>

    @Query("""
        SELECT * FROM proyecto_costos
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerPorId(id: Int): ProyectoCostoEntity?

    @Query("""
        DELETE FROM proyecto_costos
        WHERE proyectoId = :proyectoId
    """)
    suspend fun eliminarPorProyecto(proyectoId: Int)

    @Query("""
        SELECT COALESCE(SUM(monto), 0)
        FROM proyecto_costos
        WHERE proyectoId = :proyectoId
    """)
    fun totalCostosPorProyecto(proyectoId: Int): Flow<Double>
}