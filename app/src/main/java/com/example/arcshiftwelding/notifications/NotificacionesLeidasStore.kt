package com.example.arcshiftwelding.notifications

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Conserva las alertas que el usuario ya revisó.
 *
 * Una alerta permanece oculta mientras la condición que la originó siga activa.
 * Cuando la condición desaparece, su identificador se elimina automáticamente para
 * permitir que vuelva a notificarse si ocurre nuevamente en el futuro.
 */
class NotificacionesLeidasStore(
    context: Context
) {
    private val preferencias = context.applicationContext.getSharedPreferences(
        NOMBRE_PREFERENCIAS,
        Context.MODE_PRIVATE
    )

    private val _idsLeidas = MutableStateFlow(cargarIds())
    val idsLeidas: StateFlow<Set<String>> = _idsLeidas.asStateFlow()

    fun marcarComoLeida(id: String) {
        if (id.isBlank()) return

        val actualizadas = _idsLeidas.value + id
        guardar(actualizadas)
    }

    fun marcarTodasComoLeidas(ids: Collection<String>) {
        if (ids.isEmpty()) return

        val actualizadas = _idsLeidas.value + ids.filter(String::isNotBlank)
        guardar(actualizadas)
    }

    fun obtenerIdsLeidas(): Set<String> = cargarIds()

    fun sincronizarConActivas(idsActivas: Set<String>) {
        val conservadas = _idsLeidas.value.intersect(idsActivas)
        if (conservadas != _idsLeidas.value) {
            guardar(conservadas)
        }
    }

    private fun cargarIds(): Set<String> {
        return preferencias
            .getStringSet(CLAVE_IDS_LEIDAS, emptySet())
            ?.toSet()
            .orEmpty()
    }

    private fun guardar(ids: Set<String>) {
        val copiaInmutable = ids.toSet()
        preferencias.edit()
            .putStringSet(CLAVE_IDS_LEIDAS, copiaInmutable)
            .apply()
        _idsLeidas.value = copiaInmutable
    }

    private companion object {
        const val NOMBRE_PREFERENCIAS = "notificaciones_leidas_arcshift"
        const val CLAVE_IDS_LEIDAS = "ids_leidas"
    }
}
