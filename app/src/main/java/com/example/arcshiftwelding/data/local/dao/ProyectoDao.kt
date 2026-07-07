package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entity.ProyectoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoDao {

    @Query("SELECT * FROM proyectos ORDER BY fechaInicio DESC")
    fun obtenerProyectos(): Flow<List<ProyectoEntity>>

    @Query("SELECT * FROM proyectos WHERE id = :id")
    suspend fun obtenerProyectoPorId(id: Int): ProyectoEntity?

    @Query("SELECT * FROM proyectos WHERE cotizacionId = :cotizacionId LIMIT 1")
    suspend fun obtenerProyectoPorCotizacionId(cotizacionId: Int): ProyectoEntity?

    @Query("SELECT * FROM proyectos WHERE clienteId = :clienteId ORDER BY id DESC")
    fun obtenerProyectosPorCliente(clienteId: Int): Flow<List<ProyectoEntity>>

    @Insert
    suspend fun insertarProyecto(proyecto: ProyectoEntity): Long

    @Update
    suspend fun actualizarProyecto(proyecto: ProyectoEntity)

    @Delete
    suspend fun eliminarProyecto(proyecto: ProyectoEntity)

}