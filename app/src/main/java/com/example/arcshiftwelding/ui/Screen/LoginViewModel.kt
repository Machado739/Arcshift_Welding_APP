package com.example.arcshiftwelding.ui.Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arcshiftwelding.data.DatosPruebaSeeder
import com.example.arcshiftwelding.data.PerfilDatosPrueba
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoCargaDatosPrueba(
    val cargando: Boolean = false,
    val progreso: Int = 0,
    val etapa: String = "",
    val mensaje: String? = null,
    val esError: Boolean = false
)

class LoginViewModel(
    private val database: ArcshiftWeldingDatabase
) : ViewModel() {

    private val _estadoCargaDatosPrueba = MutableStateFlow(EstadoCargaDatosPrueba())
    val estadoCargaDatosPrueba: StateFlow<EstadoCargaDatosPrueba> =
        _estadoCargaDatosPrueba.asStateFlow()

    fun cargarDatosPrueba(
        perfil: PerfilDatosPrueba,
        reemplazarDatosExistentes: Boolean = true
    ) {
        if (_estadoCargaDatosPrueba.value.cargando) return

        viewModelScope.launch {
            _estadoCargaDatosPrueba.value = EstadoCargaDatosPrueba(
                cargando = true,
                progreso = 0,
                etapa = "Preparando carga"
            )

            try {
                val resumen = DatosPruebaSeeder.cargarDatosPrueba(
                    database = database,
                    perfil = perfil,
                    reemplazarDatosExistentes = reemplazarDatosExistentes,
                    onProgreso = { porcentaje, etapa ->
                        _estadoCargaDatosPrueba.update { estadoActual ->
                            estadoActual.copy(
                                cargando = true,
                                progreso = porcentaje.coerceIn(0, 100),
                                etapa = etapa,
                                mensaje = null,
                                esError = false
                            )
                        }
                    }
                )

                val segundos = resumen.duracionMs / 1_000.0
                _estadoCargaDatosPrueba.value = EstadoCargaDatosPrueba(
                    cargando = false,
                    progreso = 100,
                    etapa = "Carga terminada",
                    mensaje = "${resumen.totalRegistros} registros creados en " +
                        "${"%.1f".format(segundos)} s. Usuario: admin / admin123",
                    esError = false
                )
            } catch (e: Exception) {
                _estadoCargaDatosPrueba.value = EstadoCargaDatosPrueba(
                    cargando = false,
                    progreso = 0,
                    etapa = "Carga interrumpida",
                    mensaje = "Error al cargar datos de prueba: ${e.message ?: "causa desconocida"}",
                    esError = true
                )
            }
        }
    }

    fun limpiarMensajeDatosPrueba() {
        _estadoCargaDatosPrueba.update {
            it.copy(mensaje = null, esError = false)
        }
    }
}
