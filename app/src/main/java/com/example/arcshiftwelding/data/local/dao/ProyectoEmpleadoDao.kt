package com.example.arcshiftwelding.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arcshiftwelding.data.local.entity.ProyectoEmpleadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoEmpleadoDao {

    @Query("SELECT * FROM proyecto_empleados WHERE proyectoId = :proyectoId")
    fun obtenerEmpleadosPorProyecto(proyectoId: Int): Flow<List<ProyectoEmpleadoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun asignarEmpleado(proyectoEmpleado: ProyectoEmpleadoEntity)

    @Delete
    suspend fun quitarEmpleado(proyectoEmpleado: ProyectoEmpleadoEntity)

    @Query("DELETE FROM proyecto_empleados WHERE proyectoId = :proyectoId AND empleadoId = :empleadoId")
    suspend fun quitarEmpleadoDeProyecto(proyectoId: Int, empleadoId: Int)
}