package com.example.arcshiftwelding.data.local.dao

import androidx.room.*
import com.example.arcshiftwelding.data.local.entities.EmpresaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpresaDao {

    @Query("SELECT * FROM empresa LIMIT 1")
    fun obtenerEmpresa(): Flow<EmpresaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarEmpresa(
        empresa: EmpresaEntity
    )
}