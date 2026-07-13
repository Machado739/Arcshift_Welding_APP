package com.example.arcshiftwelding.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object NotificacionesScheduler {

    private const val TRABAJO_PERIODICO = "revision_diaria_notificaciones_arcshift"
    private const val TRABAJO_INMEDIATO = "revision_inmediata_notificaciones_arcshift"

    fun programar(context: Context) {
        NotificacionesTelefonicas.crearCanal(context)

        val solicitud = PeriodicWorkRequestBuilder<NotificacionesWorker>(
            24,
            TimeUnit.HOURS
        )
            .setInitialDelay(
                calcularRetrasoHastaSiguienteRevision(),
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            TRABAJO_PERIODICO,
            ExistingPeriodicWorkPolicy.UPDATE,
            solicitud
        )
    }

    fun ejecutarRevisionInmediata(context: Context) {
        val solicitud = OneTimeWorkRequestBuilder<NotificacionesWorker>()
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            TRABAJO_INMEDIATO,
            ExistingWorkPolicy.REPLACE,
            solicitud
        )
    }

    private fun calcularRetrasoHastaSiguienteRevision(): Long {
        val ahora = LocalDateTime.now()
        val hoyALasOcho = ahora
            .toLocalDate()
            .atTime(LocalTime.of(8, 0))

        val siguiente = if (ahora.isBefore(hoyALasOcho)) {
            hoyALasOcho
        } else {
            hoyALasOcho.plusDays(1)
        }

        return Duration.between(ahora, siguiente)
            .toMillis()
            .coerceAtLeast(0L)
    }
}
