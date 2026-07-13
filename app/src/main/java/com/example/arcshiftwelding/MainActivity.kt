package com.example.arcshiftwelding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.arcshiftwelding.navigation.AppNavigation
import com.example.arcshiftwelding.notifications.NotificacionesScheduler
import com.example.arcshiftwelding.ui.theme.ArcshiftWeldingTheme

class MainActivity : ComponentActivity() {

    private var solicitudAbrirNotificaciones by mutableIntStateOf(0)

    private val solicitarPermisoNotificaciones = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            NotificacionesScheduler.ejecutarRevisionInmediata(applicationContext)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        atenderIntentNotificacion(intent)
        NotificacionesScheduler.programar(applicationContext)
        prepararNotificacionesTelefonicas()

        setContent {
            ArcshiftWeldingTheme {
                AppNavigation(
                    solicitudAbrirNotificaciones = solicitudAbrirNotificaciones
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        atenderIntentNotificacion(intent)
    }

    private fun atenderIntentNotificacion(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_ABRIR_NOTIFICACIONES, false) == true) {
            solicitudAbrirNotificaciones++
            intent.removeExtra(EXTRA_ABRIR_NOTIFICACIONES)
        }
    }

    private fun prepararNotificacionesTelefonicas() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            NotificacionesScheduler.ejecutarRevisionInmediata(applicationContext)
            return
        }

        val permisoConcedido = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {
            NotificacionesScheduler.ejecutarRevisionInmediata(applicationContext)
            return
        }

        val preferencias = getSharedPreferences(
            PREFS_NOTIFICACIONES,
            MODE_PRIVATE
        )
        val permisoSolicitado = preferencias.getBoolean(
            CLAVE_PERMISO_SOLICITADO,
            false
        )

        if (!permisoSolicitado) {
            preferencias.edit()
                .putBoolean(CLAVE_PERMISO_SOLICITADO, true)
                .apply()
            solicitarPermisoNotificaciones.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    companion object {
        const val EXTRA_ABRIR_NOTIFICACIONES = "abrir_notificaciones"

        private const val PREFS_NOTIFICACIONES =
            "configuracion_notificaciones_arcshift"
        private const val CLAVE_PERMISO_SOLICITADO =
            "permiso_notificaciones_solicitado"
    }
}
