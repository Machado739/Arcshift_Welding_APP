package com.example.arcshiftwelding.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.arcshiftwelding.MainActivity
import com.example.arcshiftwelding.R
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import java.time.LocalDate
import kotlinx.coroutines.CancellationException

class NotificacionesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            NotificacionesTelefonicas.crearCanal(applicationContext)

            val database = ArcshiftWeldingDatabase.getDatabase(applicationContext)
            val repository = NotificacionesRepository(
                pagoProgramadoDao = database.pagoProgramadoDao(),
                cotizacionDao = database.cotizacionDao(),
                productoDao = database.productoDao()
            )

            val notificacionesActivas = repository.obtenerNotificacionesActuales()
            val leidasStore = NotificacionesLeidasStore(applicationContext)

            leidasStore.sincronizarConActivas(
                idsActivas = notificacionesActivas.map(NotificacionApp::id).toSet()
            )

            val idsLeidas = leidasStore.obtenerIdsLeidas()
            val notificaciones = notificacionesActivas.filterNot { notificacion ->
                notificacion.id in idsLeidas
            }

            if (notificaciones.isEmpty()) {
                NotificationManagerCompat
                    .from(applicationContext)
                    .cancel(NotificacionesTelefonicas.NOTIFICACION_RESUMEN_ID)
                PreferenciasNotificaciones(applicationContext).limpiarFirma()
                return Result.success()
            }

            if (!NotificacionesTelefonicas.tienePermiso(applicationContext)) {
                return Result.success()
            }

            val preferencias = PreferenciasNotificaciones(applicationContext)
            val firma = notificaciones
                .sortedBy { it.id }
                .joinToString(separator = "|") { item ->
                    "${item.id}:${item.titulo}:${item.descripcion}:${item.textoFecha}"
                }

            if (!preferencias.debeNotificar(firma)) {
                return Result.success()
            }

            NotificacionesTelefonicas.mostrarResumen(
                context = applicationContext,
                notificaciones = notificaciones
            )
            preferencias.guardarFirma(firma)

            Result.success()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            Result.retry()
        }
    }
}

object NotificacionesTelefonicas {

    const val CANAL_ID = "alertas_arcshift"
    const val NOTIFICACION_RESUMEN_ID = 4201

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val canal = NotificationChannel(
            CANAL_ID,
            "Alertas de Arcshift Welding",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Pagos por vencer, cotizaciones próximas a vencer y productos con stock bajo."
            enableVibration(true)
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }

    fun tienePermiso(context: Context): Boolean {
        val permisoConcedido = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        return permisoConcedido && NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()
    }

    fun mostrarResumen(
        context: Context,
        notificaciones: List<NotificacionApp>
    ) {
        if (!tienePermiso(context)) return

        val resumen = NotificacionesBuilder.resumir(notificaciones)
        val titulo = when (resumen.total) {
            1 -> "1 alerta pendiente"
            else -> "${resumen.total} alertas pendientes"
        }

        val contenido = construirResumenCorto(resumen)
        val detalle = buildString {
            append(contenido)
            notificaciones.take(5).forEach { notificacion ->
                append("\n• ")
                append(notificacion.titulo)
                append(": ")
                append(notificacion.descripcion)
            }
            if (notificaciones.size > 5) {
                append("\n• Y ${notificaciones.size - 5} alerta(s) más")
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MainActivity.EXTRA_ABRIR_NOTIFICACIONES, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            4201,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_arcshift_notification)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(detalle)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(NOTIFICACION_RESUMEN_ID, notificacion)
    }

    private fun construirResumenCorto(
        resumen: ResumenNotificaciones
    ): String {
        val partes = buildList {
            if (resumen.pagos > 0) {
                add("${resumen.pagos} pago${if (resumen.pagos == 1) "" else "s"}")
            }
            if (resumen.cotizaciones > 0) {
                add("${resumen.cotizaciones} cotización${if (resumen.cotizaciones == 1) "" else "es"}")
            }
            if (resumen.stock > 0) {
                add("${resumen.stock} producto${if (resumen.stock == 1) "" else "s"} con stock bajo")
            }
        }

        return partes.joinToString(separator = " · ")
    }
}

private class PreferenciasNotificaciones(
    context: Context
) {
    private val preferencias = context.getSharedPreferences(
        "preferencias_notificaciones_arcshift",
        Context.MODE_PRIVATE
    )

    fun debeNotificar(firmaActual: String): Boolean {
        val fechaActual = LocalDate.now().toString()
        val ultimaFecha = preferencias.getString(CLAVE_FECHA, null)
        val ultimaFirma = preferencias.getString(CLAVE_FIRMA, null)

        return ultimaFecha != fechaActual || ultimaFirma != firmaActual
    }

    fun guardarFirma(firma: String) {
        preferencias.edit()
            .putString(CLAVE_FECHA, LocalDate.now().toString())
            .putString(CLAVE_FIRMA, firma)
            .apply()
    }

    fun limpiarFirma() {
        preferencias.edit()
            .remove(CLAVE_FIRMA)
            .remove(CLAVE_FECHA)
            .apply()
    }

    private companion object {
        const val CLAVE_FECHA = "ultima_fecha"
        const val CLAVE_FIRMA = "ultima_firma"
    }
}
