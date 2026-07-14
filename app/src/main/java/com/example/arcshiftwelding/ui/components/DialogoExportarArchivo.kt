package com.example.arcshiftwelding.ui.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.arcshiftwelding.utils.guardarArchivoEnUri
import java.io.File

@Composable
fun DialogoExportarArchivo(
    archivo: File,
    mimeType: String,
    titulo: String,
    onDismiss: () -> Unit,
    onCompartir: () -> Unit
) {
    val context = LocalContext.current

    val guardarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult

        val destino = resultado.data?.data
            ?: return@rememberLauncherForActivityResult

        guardarArchivoEnUri(
            context = context,
            archivo = archivo,
            destino = destino
        ).onSuccess {
            Toast.makeText(
                context,
                "Archivo guardado correctamente.",
                Toast.LENGTH_SHORT
            ).show()
            onDismiss()
        }.onFailure { error ->
            Toast.makeText(
                context,
                error.message ?: "No fue posible guardar el archivo.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "El archivo ${archivo.name} está listo. Puedes guardarlo en el dispositivo o compartirlo."
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = mimeType
                        putExtra(Intent.EXTRA_TITLE, archivo.name)
                    }

                    try {
                        guardarLauncher.launch(intent)
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            "No se encontró una aplicación para guardar archivos.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            ) {
                Text("Descargar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onCompartir()
                }
            ) {
                Text("Compartir")
            }
        }
    )
}
