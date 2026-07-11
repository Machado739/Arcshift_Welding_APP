package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arcshiftwelding.data.local.entity.ProyectoAvanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoAvanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(avance: ProyectoAvanceEntity): Long

    @Query("SELECT * FROM proyecto_avances WHERE proyectoId = :proyectoId ORDER BY id DESC")
    fun observarPorProyecto(proyectoId: Int): Flow<List<ProyectoAvanceEntity>>

    @Query("DELETE FROM proyecto_avances WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
