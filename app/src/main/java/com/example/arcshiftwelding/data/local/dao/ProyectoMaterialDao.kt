package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.arcshiftwelding.data.local.entity.ProyectoMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoMaterialDao {

    @Query("SELECT * FROM proyecto_materiales WHERE proyectoId = :proyectoId ORDER BY id DESC")
    fun obtenerMaterialesPorProyecto(proyectoId: Int): Flow<List<ProyectoMaterialEntity>>

    @Insert
    suspend fun agregarMaterial(proyectoMaterial: ProyectoMaterialEntity)

    @Delete
    suspend fun eliminarMaterial(proyectoMaterial: ProyectoMaterialEntity)

    @Query("SELECT SUM(subtotal) FROM proyecto_materiales WHERE proyectoId = :proyectoId")
    suspend fun obtenerCostoMaterialesProyecto(proyectoId: Int): Double?
}