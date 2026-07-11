package com.example.arcshiftwelding.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

const val MAX_COMPROBANTE_GASTO_BYTES: Long = 10L * 1024L * 1024L
const val MAX_COMPROBANTES_POR_REGISTRO: Int = 10

data class ComprobanteArchivoSeleccionado(
    val uri: String,
    val tipo: String,
    val nombre: String,
    val tamanoBytes: Long = 0L
)

typealias ComprobanteGastoSeleccionado = ComprobanteArchivoSeleccionado

data class CapturaFotoGasto(
    val uriCamara: Uri,
    val rutaArchivo: String,
    val nombreArchivo: String
)

fun prepararComprobanteDesdeDocumento(
    context: Context,
    uri: Uri
): ComprobanteArchivoSeleccionado {
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (_: SecurityException) {
        // Algunos proveedores no permiten permisos persistentes.
    }

    val nombre = obtenerNombreDocumento(context, uri).ifBlank {
        uri.lastPathSegment?.substringAfterLast('/')?.ifBlank { "Comprobante" }
            ?: "Comprobante"
    }

    return ComprobanteArchivoSeleccionado(
        uri = uri.toString(),
        tipo = detectarTipoComprobante(context, uri, nombre),
        nombre = nombre,
        tamanoBytes = obtenerTamanoDocumento(context, uri)
    )
}

fun prepararComprobanteGastoDesdeDocumento(
    context: Context,
    uri: Uri
): ComprobanteGastoSeleccionado = prepararComprobanteDesdeDocumento(context, uri)

fun prepararCapturaFotoGasto(context: Context): CapturaFotoGasto? {
    return runCatching {
        val carpeta = File(context.filesDir, "comprobantes_gastos")
        if (!carpeta.exists()) carpeta.mkdirs()

        val nombre = "gasto_${System.currentTimeMillis()}.jpg"
        val archivo = File(carpeta, nombre)
        if (!archivo.exists()) archivo.createNewFile()

        CapturaFotoGasto(
            uriCamara = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                archivo
            ),
            rutaArchivo = archivo.absolutePath,
            nombreArchivo = nombre
        )
    }.getOrNull()
}

fun finalizarCapturaFotoGasto(
    captura: CapturaFotoGasto
): ComprobanteGastoSeleccionado? {
    val archivo = File(captura.rutaArchivo)
    if (!archivo.exists() || archivo.length() <= 0L) return null

    return ComprobanteArchivoSeleccionado(
        uri = archivo.absolutePath,
        tipo = "Imagen",
        nombre = captura.nombreArchivo,
        tamanoBytes = archivo.length()
    )
}

fun serializarComprobantes(
    comprobantes: List<ComprobanteArchivoSeleccionado>
): String {
    val array = JSONArray()

    comprobantes.forEach { comprobante ->
        array.put(
            JSONObject().apply {
                put("uri", comprobante.uri)
                put("tipo", comprobante.tipo)
                put("nombre", comprobante.nombre)
                put("tamanoBytes", comprobante.tamanoBytes)
            }
        )
    }

    return array.toString()
}

fun deserializarComprobantes(
    comprobantesJson: String,
    comprobanteUriLegado: String = "",
    tipoComprobanteLegado: String = "",
    nombreComprobanteLegado: String = ""
): List<ComprobanteArchivoSeleccionado> {
    val resultado = mutableListOf<ComprobanteArchivoSeleccionado>()

    if (comprobantesJson.isNotBlank()) {
        runCatching {
            val array = JSONArray(comprobantesJson)
            for (indice in 0 until array.length()) {
                val item = array.optJSONObject(indice) ?: continue
                val uri = item.optString("uri")
                if (uri.isBlank()) continue

                resultado += ComprobanteArchivoSeleccionado(
                    uri = uri,
                    tipo = item.optString("tipo").ifBlank { "Archivo" },
                    nombre = item.optString("nombre").ifBlank { "Comprobante" },
                    tamanoBytes = item.optLong("tamanoBytes", 0L)
                )
            }
        }
    }

    if (comprobanteUriLegado.isNotBlank() && resultado.none { it.uri == comprobanteUriLegado }) {
        resultado.add(
            0,
            ComprobanteArchivoSeleccionado(
                uri = comprobanteUriLegado,
                tipo = tipoComprobanteLegado.ifBlank { "Archivo" },
                nombre = nombreComprobanteLegado.ifBlank { "Comprobante" }
            )
        )
    }

    return resultado.distinctBy { it.uri }
}

fun obtenerTipoRealComprobante(
    context: Context,
    comprobante: ComprobanteArchivoSeleccionado
): String {
    val nombre = comprobante.nombre.lowercase()

    if (nombre.endsWith(".pdf")) return "PDF"
    if (nombre.substringAfterLast('.', "") in EXTENSIONES_IMAGEN) return "Imagen"

    if (comprobante.uri.startsWith("content://")) {
        return detectarTipoComprobante(
            context = context,
            uri = Uri.parse(comprobante.uri),
            nombre = comprobante.nombre
        )
    }

    return comprobante.tipo.ifBlank { "Archivo" }
}

fun abrirComprobante(
    context: Context,
    comprobante: ComprobanteArchivoSeleccionado
): Boolean = abrirComprobanteGasto(
    context = context,
    comprobanteUri = comprobante.uri,
    tipoComprobante = comprobante.tipo,
    nombreComprobante = comprobante.nombre
)

fun abrirComprobanteGasto(
    context: Context,
    comprobanteUri: String,
    tipoComprobante: String,
    nombreComprobante: String = ""
): Boolean {
    if (comprobanteUri.isBlank()) return false

    val uri = resolverUriCompartible(context, comprobanteUri) ?: return false

    val mimeType = resolverMimeType(
        context = context,
        uri = uri,
        tipoComprobante = tipoComprobante,
        nombreComprobante = nombreComprobante
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    return try {
        context.startActivity(Intent.createChooser(intent, "Abrir comprobante"))
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}

fun eliminarComprobanteInternoGasto(comprobanteUri: String) {
    if (comprobanteUri.isBlank() || comprobanteUri.startsWith("content://")) return

    runCatching {
        val archivo = if (comprobanteUri.startsWith("file://")) {
            File(Uri.parse(comprobanteUri).path.orEmpty())
        } else {
            File(comprobanteUri)
        }

        if (archivo.exists() && archivo.parentFile?.name == "comprobantes_gastos") {
            archivo.delete()
        }
    }
}

fun formatearTamanoComprobante(bytes: Long): String {
    if (bytes <= 0L) return "Archivo seleccionado"

    val kb = bytes / 1024.0
    return if (kb < 1024.0) {
        "%.1f KB".format(kb)
    } else {
        "%.1f MB".format(kb / 1024.0)
    }
}

fun detectarTipoComprobante(
    context: Context,
    uri: Uri,
    nombre: String = ""
): String {
    val mimeType = context.contentResolver.getType(uri).orEmpty().lowercase()
    val nombreNormalizado = nombre.lowercase()
    val extension = nombreNormalizado.substringAfterLast('.', "")

    val parecePdfPorCabecera = runCatching {
        context.contentResolver.openInputStream(uri)?.use { input ->
            val cabecera = ByteArray(5)
            val leidos = input.read(cabecera)
            leidos == 5 && String(cabecera, Charsets.US_ASCII) == "%PDF-"
        } ?: false
    }.getOrDefault(false)

    return when {
        mimeType == "application/pdf" || extension == "pdf" || parecePdfPorCabecera -> "PDF"
        mimeType.startsWith("image/") || extension in EXTENSIONES_IMAGEN -> "Imagen"
        else -> "Archivo"
    }
}

private fun resolverUriCompartible(context: Context, comprobanteUri: String): Uri? {
    return when {
        comprobanteUri.startsWith("content://") -> Uri.parse(comprobanteUri)

        comprobanteUri.startsWith("file://") -> {
            val archivo = File(Uri.parse(comprobanteUri).path.orEmpty())
            if (!archivo.exists()) null else FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                archivo
            )
        }

        else -> {
            val archivo = File(comprobanteUri)
            if (!archivo.exists()) null else FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                archivo
            )
        }
    }
}

private fun obtenerNombreDocumento(context: Context, uri: Uri): String {
    var nombre = ""

    runCatching {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            val indice = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (indice >= 0 && cursor.moveToFirst()) {
                nombre = cursor.getString(indice).orEmpty()
            }
        }
    }

    return nombre
}

private fun obtenerTamanoDocumento(context: Context, uri: Uri): Long {
    var tamano = 0L

    runCatching {
        context.contentResolver.query(
            uri,
            arrayOf(OpenableColumns.SIZE),
            null,
            null,
            null
        )?.use { cursor ->
            val indice = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (indice >= 0 && cursor.moveToFirst() && !cursor.isNull(indice)) {
                tamano = cursor.getLong(indice)
            }
        }
    }

    if (tamano <= 0L) {
        tamano = runCatching {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { descriptor ->
                descriptor.length.takeIf { it > 0L } ?: 0L
            } ?: 0L
        }.getOrDefault(0L)
    }

    return tamano
}

private fun resolverMimeType(
    context: Context,
    uri: Uri,
    tipoComprobante: String,
    nombreComprobante: String
): String {
    if (tipoComprobante.equals("PDF", ignoreCase = true) ||
        nombreComprobante.endsWith(".pdf", ignoreCase = true)
    ) {
        return "application/pdf"
    }

    context.contentResolver.getType(uri)?.takeIf { it.isNotBlank() }?.let {
        return it
    }

    if (tipoComprobante.equals("Imagen", ignoreCase = true)) {
        return "image/*"
    }

    val extension = nombreComprobante
        .substringAfterLast('.', "")
        .lowercase()

    return MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(extension)
        ?: "application/octet-stream"
}

private val EXTENSIONES_IMAGEN = setOf(
    "jpg", "jpeg", "png", "webp", "gif", "bmp", "heic", "heif"
)
