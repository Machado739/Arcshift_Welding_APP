package com.example.arcshiftwelding.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun guardarImagenProductoEnInterno(
    context: Context,
    imagenUri: Uri?
): String {
    if (imagenUri == null) return ""

    val inputStream = context.contentResolver.openInputStream(imagenUri)
        ?: return ""

    val carpetaImagenes = File(context.filesDir, "imagenes_productos")

    if (!carpetaImagenes.exists()) {
        carpetaImagenes.mkdirs()
    }

    val nombreArchivo = "producto_${System.currentTimeMillis()}.jpg"
    val archivoDestino = File(carpetaImagenes, nombreArchivo)

    FileOutputStream(archivoDestino).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    inputStream.close()

    return archivoDestino.absolutePath
}