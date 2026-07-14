package com.example.arcshiftwelding.ui.Screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.arcshiftwelding.data.DatosPruebaSeeder
import com.example.arcshiftwelding.data.PerfilDatosPrueba
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
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

data class EstadoCargaDatosPrueba(
    val cargando: Boolean = false,
    val progreso: Int = 0,
    val etapa: String = "",
    val mensaje: String? = null,
    val esError: Boolean = false
)

data class EstadoAutenticacion(
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val esError: Boolean = false,
    val usuarioInicialCreado: Boolean = false
)

data class EstadoRecuperacion(
    val cargando: Boolean = false,
    val mensaje: String? = null,
    val exitosa: Boolean = false
)

class LoginViewModel(
    private val database: ArcshiftWeldingDatabase,
    context: Context
) : ViewModel() {

    private val sesionStore = SesionUsuarioStore(context.applicationContext)

    private val _estadoAutenticacion = MutableStateFlow(EstadoAutenticacion())
    val estadoAutenticacion: StateFlow<EstadoAutenticacion> =
        _estadoAutenticacion.asStateFlow()

    private val _estadoRecuperacion = MutableStateFlow(EstadoRecuperacion())
    val estadoRecuperacion: StateFlow<EstadoRecuperacion> =
        _estadoRecuperacion.asStateFlow()

    private val _estadoCargaDatosPrueba = MutableStateFlow(EstadoCargaDatosPrueba())
    val estadoCargaDatosPrueba: StateFlow<EstadoCargaDatosPrueba> =
        _estadoCargaDatosPrueba.asStateFlow()

    init {
        viewModelScope.launch {
            asegurarUsuarioInicial()
        }
    }

    fun iniciarSesion(
        usuario: String,
        password: String,
        onLoginExitoso: () -> Unit
    ) {
        if (_estadoAutenticacion.value.cargando) return

        val usuarioLimpio = usuario.trim()
        if (usuarioLimpio.isBlank() || password.isBlank()) {
            _estadoAutenticacion.update {
                it.copy(
                    mensaje = "Escribe el usuario y la contraseña.",
                    esError = true
                )
            }
            return
        }

        viewModelScope.launch {
            _estadoAutenticacion.update {
                it.copy(cargando = true, mensaje = null, esError = false)
            }

            try {
                asegurarUsuarioInicial()
                val registro = database.usuarioDao().obtenerPorUsuario(usuarioLimpio)
                val correcto = registro != null &&
                    PasswordSecurity.verificarPassword(password, registro.password)

                if (!correcto || registro == null) {
                    _estadoAutenticacion.update {
                        it.copy(
                            cargando = false,
                            mensaje = "Usuario o contraseña incorrectos.",
                            esError = true
                        )
                    }
                    return@launch
                }

                val usuarioActualizado = if (
                    PasswordSecurity.esPasswordHasheado(registro.password)
                ) {
                    registro
                } else {
                    val nuevoHash = PasswordSecurity.hashPassword(password)
                    database.usuarioDao().actualizarPassword(registro.id, nuevoHash)
                    registro.copy(password = nuevoHash)
                }

                sesionStore.iniciarSesion(usuarioActualizado)
                _estadoAutenticacion.update {
                    it.copy(cargando = false, mensaje = null, esError = false)
                }
                onLoginExitoso()
            } catch (e: Exception) {
                _estadoAutenticacion.update {
                    it.copy(
                        cargando = false,
                        mensaje = "No fue posible iniciar sesión: ${e.message ?: "error desconocido"}",
                        esError = true
                    )
                }
            }
        }
    }

    fun recuperarAcceso(
        usuario: String,
        codigoRespaldo: String,
        nuevaPassword: String,
        confirmacion: String,
        onRecuperacionExitosa: () -> Unit
    ) {
        if (_estadoRecuperacion.value.cargando) return

        val errorPassword = PasswordSecurity.validarNuevaPassword(nuevaPassword)
        val error = when {
            usuario.isBlank() -> "Escribe el nombre de usuario."
            codigoRespaldo.isBlank() -> "Escribe un código de respaldo."
            errorPassword != null -> errorPassword
            nuevaPassword != confirmacion -> "Las contraseñas no coinciden."
            else -> null
        }

        if (error != null) {
            _estadoRecuperacion.value = EstadoRecuperacion(mensaje = error)
            return
        }

        viewModelScope.launch {
            _estadoRecuperacion.value = EstadoRecuperacion(cargando = true)

            try {
                val recuperada = database.withTransaction {
                    val registro = database.usuarioDao()
                        .obtenerPorUsuario(usuario.trim())
                        ?: return@withTransaction false

                    val codigoHash = PasswordSecurity.hashCodigoRespaldo(codigoRespaldo)
                    val codigo = database.codigoRespaldoDao()
                        .obtenerDisponible(registro.id, codigoHash)
                        ?: return@withTransaction false

                    val actualizado = database.codigoRespaldoDao().marcarComoUsado(
                        codigoId = codigo.id,
                        fechaUso = fechaHoraActual()
                    )
                    if (actualizado != 1) return@withTransaction false

                    database.usuarioDao().actualizarPassword(
                        usuarioId = registro.id,
                        passwordHash = PasswordSecurity.hashPassword(nuevaPassword)
                    )
                    true
                }

                if (recuperada) {
                    _estadoRecuperacion.value = EstadoRecuperacion(
                        mensaje = "Contraseña actualizada. El código ya no puede volver a utilizarse.",
                        exitosa = true
                    )
                    onRecuperacionExitosa()
                } else {
                    _estadoRecuperacion.value = EstadoRecuperacion(
                        mensaje = "El usuario o el código de respaldo no son válidos."
                    )
                }
            } catch (e: Exception) {
                _estadoRecuperacion.value = EstadoRecuperacion(
                    mensaje = "No fue posible recuperar el acceso: ${e.message ?: "error desconocido"}"
                )
            }
        }
    }

    fun limpiarEstadoRecuperacion() {
        _estadoRecuperacion.value = EstadoRecuperacion()
    }

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

    private suspend fun asegurarUsuarioInicial() {
        if (database.usuarioDao().contarUsuarios() > 0) return

        val id = database.usuarioDao().insertarUsuario(
            UsuarioEntity(
                nombre = "Administrador",
                usuario = "admin",
                password = PasswordSecurity.hashPassword("admin123"),
                rol = "Administrador"
            )
        )

        if (id > 0) {
            _estadoAutenticacion.update {
                it.copy(usuarioInicialCreado = true)
            }
        }
    }

    private fun fechaHoraActual(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
}
