package com.example.arcshiftwelding.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
class EstadoValidacionFormulario internal constructor() {
    var mensaje by mutableStateOf<String?>(null)
        private set

    fun limpiar() {
        mensaje = null
    }

    internal fun establecerMensaje(nuevoMensaje: String) {
        mensaje = nuevoMensaje
    }

    fun establecerMensajePublico(nuevoMensaje: String) {
        mensaje = nuevoMensaje
    }
}

@Composable
fun rememberEstadoValidacionFormulario(): EstadoValidacionFormulario =
    remember { EstadoValidacionFormulario() }

/**
 * Muestra el mensaje, desplaza el formulario hasta el campo y solicita el foco
 * cuando el control admite entrada directa.
 */
@OptIn(ExperimentalFoundationApi::class)
fun mostrarErrorEnCampo(
    scope: CoroutineScope,
    estado: EstadoValidacionFormulario,
    mensaje: String,
    bringIntoViewRequester: BringIntoViewRequester,
    focusRequester: FocusRequester? = null
) {
    estado.establecerMensaje(mensaje)
    scope.launch {
        bringIntoViewRequester.bringIntoView()
        delay(120)
        focusRequester?.requestFocus()
    }
}


@Composable
fun rememberSnackbarValidacion(
    estado: EstadoValidacionFormulario
): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }
    val mensaje = estado.mensaje

    LaunchedEffect(mensaje) {
        if (!mensaje.isNullOrBlank()) {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(
                message = mensaje,
                withDismissAction = true
            )
        }
    }

    return snackbarHostState
}

@Composable
fun AvisoValidacionFormulario(
    estado: EstadoValidacionFormulario,
    modifier: Modifier = Modifier
) {
    val mensaje = estado.mensaje ?: return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = mensaje,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
