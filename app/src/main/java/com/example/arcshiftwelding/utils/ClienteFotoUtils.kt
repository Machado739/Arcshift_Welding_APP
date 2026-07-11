package com.example.arcshiftwelding.ui.Screen.clientes

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

/**
 * Conserva el permiso de lectura del URI recibido desde OpenDocument.
 * No abre la cámara ni requiere permisos de almacenamiento.
 */
fun conservarPermisoFotoCliente(
    context: Context,
    uri: Uri
): String {
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (_: Exception) {
        // Algunos proveedores no permiten persistir el permiso.
    }

    return uri.toString()
}

@Composable
fun ImagenPerfilCliente(
    fotoUri: String,
    iniciales: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    colorFondo: Color = Color(0xFFEAF2FF),
    colorContenido: Color = Color(0xFF2563EB)
) {
    val context = LocalContext.current
    val imagen by produceState<ImageBitmap?>(
        initialValue = null,
        key1 = fotoUri,
        key2 = context
    ) {
        value = if (fotoUri.isBlank()) {
            null
        } else {
            withContext(Dispatchers.IO) {
                cargarImagenCliente(context, fotoUri)
            }
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        if (imagen != null) {
            Image(
                bitmap = imagen!!,
                contentDescription = "Foto del cliente",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else if (iniciales.isNotBlank()) {
            Text(
                text = iniciales,
                color = colorContenido,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = colorContenido,
                modifier = Modifier.fillMaxSize(0.55f)
            )
        }
    }
}

private fun cargarImagenCliente(
    context: Context,
    fotoUri: String
): ImageBitmap? {
    return runCatching {
        val uri = Uri.parse(fotoUri)

        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        }

        var sampleSize = 1
        while (
            bounds.outWidth / sampleSize > 1024 ||
            bounds.outHeight / sampleSize > 1024
        ) {
            sampleSize *= 2
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize.coerceAtLeast(1)
        }

        val bitmap: Bitmap? = context.contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, options)
        }

        bitmap?.asImageBitmap()
    }.getOrNull()
}
