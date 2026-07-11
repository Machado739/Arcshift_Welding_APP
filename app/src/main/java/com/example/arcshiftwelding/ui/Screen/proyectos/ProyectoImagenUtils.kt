package com.example.arcshiftwelding.ui.Screen.proyectos

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

const val MAX_IMAGENES_PROYECTO = 12

data class ImagenProyectoSeleccionada(
    val uri: String,
    val nombre: String
)

fun serializarImagenesProyecto(imagenes: List<ImagenProyectoSeleccionada>): String {
    val arreglo = JSONArray()
    imagenes.forEach { imagen ->
        arreglo.put(JSONObject().apply {
            put("uri", imagen.uri)
            put("nombre", imagen.nombre)
        })
    }
    return arreglo.toString()
}

fun deserializarImagenesProyecto(json: String): List<ImagenProyectoSeleccionada> = runCatching {
    if (json.isBlank()) return@runCatching emptyList()
    val arreglo = JSONArray(json)
    buildList {
        for (i in 0 until arreglo.length()) {
            val item = arreglo.optJSONObject(i) ?: continue
            val uri = item.optString("uri")
            if (uri.isNotBlank()) {
                add(
                    ImagenProyectoSeleccionada(
                        uri = uri,
                        nombre = item.optString("nombre", "Imagen ${i + 1}")
                    )
                )
            }
        }
    }
}.getOrDefault(emptyList())

private fun prepararImagenProyecto(context: Context, uri: Uri): ImagenProyectoSeleccionada {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    var nombre = "imagen_proyecto"
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            val indice = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && indice >= 0) {
                nombre = cursor.getString(indice) ?: nombre
            }
        }

    return ImagenProyectoSeleccionada(uri.toString(), nombre)
}

@Composable
fun SelectorImagenesProyecto(
    imagenes: List<ImagenProyectoSeleccionada>,
    onImagenesChange: (List<ImagenProyectoSeleccionada>) -> Unit,
    tituloBoton: String = "Seleccionar imágenes",
    maximo: Int = MAX_IMAGENES_PROYECTO
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val nuevas = uris
            .map { prepararImagenProyecto(context, it) }
            .filterNot { nueva -> imagenes.any { it.uri == nueva.uri } }

        onImagenesChange((imagenes + nuevas).take(maximo))
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = { launcher.launch(arrayOf("image/*")) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = imagenes.size < maximo
        ) {
            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
            Spacer(Modifier.size(6.dp))
            Text("$tituloBoton (${imagenes.size}/$maximo)")
        }

        if (imagenes.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                imagenes.forEach { imagen ->
                    MiniaturaImagenProyecto(
                        imagen = imagen,
                        onEliminar = {
                            onImagenesChange(imagenes.filterNot { it.uri == imagen.uri })
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniaturaImagenProyecto(
    imagen: ImagenProyectoSeleccionada,
    onEliminar: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, imagen.uri, context) {
        value = withContext(Dispatchers.IO) { cargarImagenProyecto(context, imagen.uri) }
    }

    Card(
        modifier = Modifier
            .size(width = 112.dp, height = 116.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = imagen.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF64748B))
                }
            }

            Text(
                text = imagen.nombre,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 7.dp, vertical = 6.dp)
                    .fillMaxWidth()
            )

            if (onEliminar != null) {
                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                        .clip(RoundedCornerShape(bottomStart = 10.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Quitar imagen",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GaleriaImagenesProyecto(
    imagenes: List<ImagenProyectoSeleccionada>,
    onAbrir: (ImagenProyectoSeleccionada) -> Unit
) {
    if (imagenes.isEmpty()) {
        Text(
            "No hay imágenes registradas.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B)
        )
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        imagenes.forEach { imagen ->
            MiniaturaImagenProyecto(imagen = imagen, onClick = { onAbrir(imagen) })
        }
    }
}

fun abrirImagenProyecto(context: Context, imagen: ImagenProyectoSeleccionada) {
    val uri = Uri.parse(imagen.uri)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "image/*")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching { context.startActivity(intent) }
}

private fun cargarImagenProyecto(context: Context, uriTexto: String): ImageBitmap? = runCatching {
    val uri = Uri.parse(uriTexto)
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, bounds)
    }

    var sample = 1
    while (bounds.outWidth / sample > 1200 || bounds.outHeight / sample > 1200) {
        sample *= 2
    }

    val options = BitmapFactory.Options().apply { inSampleSize = sample.coerceAtLeast(1) }
    val bitmap: Bitmap? = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    }
    bitmap?.asImageBitmap()
}.getOrNull()
