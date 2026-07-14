package com.example.arcshiftwelding.security

import android.content.Context
import com.example.arcshiftwelding.data.local.entity.UsuarioEntity

/** Persistencia ligera de la sesión local del usuario autenticado. */
class SesionUsuarioStore(context: Context) {

    private val preferencias = context.applicationContext.getSharedPreferences(
        PREFS_SESION,
        Context.MODE_PRIVATE
    )

    fun iniciarSesion(usuario: UsuarioEntity) {
        preferencias.edit()
            .putInt(CLAVE_USUARIO_ID, usuario.id)
            .putString(CLAVE_NOMBRE, usuario.nombre)
            .putString(CLAVE_USUARIO, usuario.usuario)
            .putString(CLAVE_ROL, usuario.rol)
            .putBoolean(CLAVE_SESION_ACTIVA, true)
            .apply()
    }

    fun cerrarSesion() {
        preferencias.edit().clear().apply()
    }

    fun haySesionActiva(): Boolean =
        preferencias.getBoolean(CLAVE_SESION_ACTIVA, false) && usuarioId() > 0

    fun usuarioId(): Int = preferencias.getInt(CLAVE_USUARIO_ID, 0)

    fun nombre(): String = preferencias.getString(CLAVE_NOMBRE, "").orEmpty()

    fun usuario(): String = preferencias.getString(CLAVE_USUARIO, "").orEmpty()

    fun rol(): String = preferencias.getString(CLAVE_ROL, "").orEmpty()

    companion object {
        private const val PREFS_SESION = "sesion_arcshift_welding"
        private const val CLAVE_USUARIO_ID = "usuario_id"
        private const val CLAVE_NOMBRE = "nombre"
        private const val CLAVE_USUARIO = "usuario"
        private const val CLAVE_ROL = "rol"
        private const val CLAVE_SESION_ACTIVA = "sesion_activa"
    }
}
