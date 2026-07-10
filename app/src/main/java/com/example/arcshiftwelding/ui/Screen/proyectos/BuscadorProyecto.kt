package com.example.arcshiftwelding.ui.Screen.proyectos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties

@Composable
fun <T> BuscadorListaProyecto(
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit,
    label: String,
    placeholder: String,
    elementos: List<T>,
    textoPrincipal: (T) -> String,
    textoSecundario: (T) -> String,
    onSeleccionar: (T) -> Unit,
    modifier: Modifier = Modifier,
    mostrarLista: Boolean = true,
    maxResultados: Int = 20
) {
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val density = LocalDensity.current
    val anchoMenu = with(density) {
        textFieldSize.width.toDp()
    }

    val elementosFiltrados by remember(textoBusqueda, elementos) {
        derivedStateOf {
            if (textoBusqueda.isBlank()) {
                emptyList()
            } else {
                elementos
                    .filter { elemento ->
                        textoPrincipal(elemento).contains(textoBusqueda, ignoreCase = true) ||
                                textoSecundario(elemento).contains(textoBusqueda, ignoreCase = true)
                    }
                    .take(maxResultados)
            }
        }
    }

    val mostrarResultados = mostrarLista && textoBusqueda.isNotBlank()

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onTextoBusquedaChange,
            label = {
                Text(label)
            },
            placeholder = {
                Text(placeholder)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordenadas ->
                    textFieldSize = coordenadas.size.toSize()
                }
        )

        DropdownMenu(
            expanded = mostrarResultados,
            onDismissRequest = { },
            offset = DpOffset(x = 0.dp, y = 4.dp),
            properties = PopupProperties(
                focusable = false
            ),
            modifier = Modifier
                .width(anchoMenu)
                .heightIn(max = 260.dp)
        ) {
            if (elementosFiltrados.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "No se encontraron resultados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    },
                    onClick = { }
                )
            } else {
                elementosFiltrados.forEachIndexed { index, elemento ->
                    DropdownMenuItem(
                        text = {
                            androidx.compose.foundation.layout.Column {
                                Text(
                                    text = textoPrincipal(elemento),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )

                                val secundario = textoSecundario(elemento)

                                if (secundario.isNotBlank()) {
                                    Text(
                                        text = secundario,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        },
                        onClick = {
                            onSeleccionar(elemento)
                        }
                    )

                    if (index < elementosFiltrados.lastIndex) {
                        Divider(color = Color(0xFFE5E7EB))
                    }
                }
            }
        }
    }
}