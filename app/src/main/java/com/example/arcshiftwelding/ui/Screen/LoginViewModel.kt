package com.example.arcshiftwelding.ui.Screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.DatosPruebaSeeder
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val database: ArcshiftWeldingDatabase
) : ViewModel() {

    var cargandoDatosPrueba by mutableStateOf(false)
        private set

    var mensajeDatosPrueba by mutableStateOf<String?>(null)
        private set

    fun cargarDatosPrueba() {
        viewModelScope.launch {
            try {
                cargandoDatosPrueba = true
                mensajeDatosPrueba = null

                DatosPruebaSeeder.cargarDatosPrueba(database)

                mensajeDatosPrueba = "Datos de prueba cargados correctamente"
            } catch (e: Exception) {
                mensajeDatosPrueba = "Error al cargar datos de prueba: ${e.message}"
            } finally {
                cargandoDatosPrueba = false
            }
        }
    }
}