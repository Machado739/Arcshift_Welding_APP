package com.example.arcshiftwelding.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun guardarArchivoEnUri(
    context: Context,
    archivo: File,
    destino: Uri
): Result<Long> = runCatching {
    require(archivo.exists() && archivo.isFile) {
        "No se encontró el archivo generado."
    }

    val salida = context.contentResolver.openOutputStream(destino, "w")
        ?: error("No fue posible abrir la ubicación seleccionada.")

    archivo.inputStream().use { entrada ->
        salida.use { output ->
            entrada.copyTo(output)
        }
    }
}
