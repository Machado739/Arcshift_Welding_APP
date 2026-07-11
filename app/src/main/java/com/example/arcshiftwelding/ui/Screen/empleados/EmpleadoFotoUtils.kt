package com.example.arcshiftwelding.ui.Screen.empleados

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Conserva el permiso de lectura del URI seleccionado con OpenDocument. */
fun conservarPermisoFotoEmpleado(context: Context, uri: Uri): String {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
    return uri.toString()
}

@Composable
fun ImagenPerfilEmpleado(
    fotoUri: String,
    iniciales: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    colorFondo: Color = Color(0xFFEAF2FF),
    colorContenido: Color = Color(0xFF2563EB)
) {
    val context = LocalContext.current
    val imagen by produceState<ImageBitmap?>(initialValue = null, fotoUri, context) {
        value = if (fotoUri.isBlank()) null else withContext(Dispatchers.IO) {
            cargarImagenEmpleado(context, fotoUri)
        }
    }

    Box(
        modifier = modifier.clip(shape).background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        when {
            imagen != null -> Image(
                bitmap = imagen!!,
                contentDescription = "Foto del empleado",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            iniciales.isNotBlank() -> Text(
                text = iniciales,
                color = colorContenido,
                style = MaterialTheme.typography.titleMedium
            )
            else -> Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = colorContenido,
                modifier = Modifier.fillMaxSize(0.55f)
            )
        }
    }
}

private fun cargarImagenEmpleado(context: Context, fotoUri: String): ImageBitmap? = runCatching {
    val uri = Uri.parse(fotoUri)
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, bounds)
    }

    var sample = 1
    while (bounds.outWidth / sample > 1024 || bounds.outHeight / sample > 1024) {
        sample *= 2
    }

    val options = BitmapFactory.Options().apply { inSampleSize = sample.coerceAtLeast(1) }
    val bitmap: Bitmap? = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    }
    bitmap?.asImageBitmap()
}.getOrNull()
