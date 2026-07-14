package com.example.arcshiftwelding.ui.Screen.configuracion

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.data.local.entity.CodigoRespaldoEntity
import com.example.arcshiftwelding.data.local.entity.UsuarioEntity
import com.example.arcshiftwelding.security.PasswordSecurity
import com.example.arcshiftwelding.security.SesionUsuarioStore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConfiguracionUiState(
    val cargando: Boolean = true,
    val usuario: UsuarioEntity? = null,
    val codigosDisponibles: Int = 0,
    val codigosGenerados: List<String> = emptyList(),
    val mensaje: String? = null,
    val esError: Boolean = false
)

class ConfiguracionViewModel(
    private val database: ArcshiftWeldingDatabase,
    context: Context
) : ViewModel() {

    private val sesionStore = SesionUsuarioStore(context.applicationContext)
    private val _uiState = MutableStateFlow(ConfiguracionUiState())
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    init {
        cargarCuenta()
    }

    fun cambiarPassword(
        passwordActual: String,
        nuevaPassword: String,
        confirmacion: String,
        onExito: () -> Unit = {}
    ) {
        val errorNueva = PasswordSecurity.validarNuevaPassword(nuevaPassword)
        val error = when {
            passwordActual.isBlank() -> "Escribe la contraseña actual."
            errorNueva != null -> errorNueva
            nuevaPassword != confirmacion -> "Las contraseñas nuevas no coinciden."
            passwordActual == nuevaPassword -> "La nueva contraseña debe ser diferente."
            else -> null
        }

        if (error != null) {
            mostrarMensaje(error, true)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensaje = null) }
            try {
                val usuario = obtenerUsuarioActual()
                    ?: error("No hay una sesión de usuario válida.")

                if (!PasswordSecurity.verificarPassword(passwordActual, usuario.password)) {
                    mostrarMensaje("La contraseña actual no es correcta.", true)
                    return@launch
                }

                val nuevoHash = PasswordSecurity.hashPassword(nuevaPassword)
                database.usuarioDao().actualizarPassword(usuario.id, nuevoHash)
                val actualizado = usuario.copy(password = nuevoHash)
                sesionStore.iniciarSesion(actualizado)

                _uiState.update {
                    it.copy(
                        cargando = false,
                        usuario = actualizado,
                        mensaje = "Contraseña actualizada correctamente.",
                        esError = false
                    )
                }
                onExito()
            } catch (e: Exception) {
                mostrarMensaje(
                    "No fue posible cambiar la contraseña: ${e.message ?: "error desconocido"}",
                    true
                )
            }
        }
    }

    fun generarCodigosRespaldo(passwordActual: String) {
        if (passwordActual.isBlank()) {
            mostrarMensaje("Confirma tu contraseña para generar los códigos.", true)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensaje = null) }
            try {
                val usuario = obtenerUsuarioActual()
                    ?: error("No hay una sesión de usuario válida.")

                if (!PasswordSecurity.verificarPassword(passwordActual, usuario.password)) {
                    mostrarMensaje("La contraseña no es correcta.", true)
                    return@launch
                }

                val codigos = PasswordSecurity.generarCodigosRespaldo()
                val fecha = fechaHoraActual()
                val entidades = codigos.map { codigo ->
                    CodigoRespaldoEntity(
                        usuarioId = usuario.id,
                        codigoHash = PasswordSecurity.hashCodigoRespaldo(codigo),
                        fechaCreacion = fecha
                    )
                }

                database.withTransaction {
                    database.codigoRespaldoDao().reemplazarCodigos(
                        usuarioId = usuario.id,
                        codigos = entidades
                    )
                }

                _uiState.update {
                    it.copy(
                        cargando = false,
                        codigosDisponibles = codigos.size,
                        codigosGenerados = codigos,
                        mensaje = "Se generaron ${codigos.size} códigos nuevos. Los anteriores dejaron de funcionar.",
                        esError = false
                    )
                }
            } catch (e: Exception) {
                mostrarMensaje(
                    "No fue posible generar los códigos: ${e.message ?: "error desconocido"}",
                    true
                )
            }
        }
    }

    fun ocultarCodigosGenerados() {
        _uiState.update { it.copy(codigosGenerados = emptyList()) }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null, esError = false) }
    }

    private fun cargarCuenta() {
        viewModelScope.launch {
            try {
                val usuario = obtenerUsuarioActual()
                val disponibles = usuario?.let {
                    database.codigoRespaldoDao().contarDisponibles(it.id)
                } ?: 0

                _uiState.value = ConfiguracionUiState(
                    cargando = false,
                    usuario = usuario,
                    codigosDisponibles = disponibles,
                    mensaje = if (usuario == null) "No se encontró una sesión activa." else null,
                    esError = usuario == null
                )
            } catch (e: Exception) {
                _uiState.value = ConfiguracionUiState(
                    cargando = false,
                    mensaje = "No fue posible cargar la cuenta: ${e.message ?: "error desconocido"}",
                    esError = true
                )
            }
        }
    }

    private suspend fun obtenerUsuarioActual(): UsuarioEntity? {
        val usuarioId = sesionStore.usuarioId()
        if (usuarioId <= 0) return null
        return database.usuarioDao().obtenerPorId(usuarioId)
    }

    private fun mostrarMensaje(mensaje: String, esError: Boolean) {
        _uiState.update {
            it.copy(
                cargando = false,
                mensaje = mensaje,
                esError = esError
            )
        }
    }

    private fun fechaHoraActual(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
}

class ConfiguracionViewModelFactory(
    private val database: ArcshiftWeldingDatabase,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfiguracionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConfiguracionViewModel(
                database = database,
                context = context.applicationContext
            ) as T
        }

        throw IllegalArgumentException(
            "ConfiguracionViewModelFactory no puede crear: ${modelClass.name}"
        )
    }
}
