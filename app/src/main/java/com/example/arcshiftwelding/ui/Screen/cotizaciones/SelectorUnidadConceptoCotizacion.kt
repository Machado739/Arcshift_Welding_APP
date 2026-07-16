package com.example.arcshiftwelding.ui.Screen.cotizaciones

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * Unidades disponibles según la categoría del concepto.
 * Se guardan como texto para conservar compatibilidad con DetalleCotizacionEntity.
 */
fun opcionesUnidadConceptoCotizacion(tipo: String): List<String> {
    return when (tipo) {
        "Materiales" -> listOf(
            "Pza",
            "Tramo",
            "Metro",
            "m²",
            "m³",
            "kg",
            "g",
            "Litro",
            "ml",
            "Hoja",
            "Rollo",
            "Caja",
            "Paquete",
            "Juego"
        )

        "Mano de obra" -> listOf(
            "Servicio",
            "Hora",
            "Día",
            "Jornada",
            "Semana",
            "Trabajo",
            "Pza"
        )

        "Gastos adicionales" -> listOf(
            "Gasto",
            "Servicio",
            "Viaje",
            "Día",
            "Noche",
            "Evento",
            "Pza"
        )

        else -> listOf(
            "Pza",
            "Servicio",
            "Metro",
            "kg",
            "Litro"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorUnidadConceptoCotizacion(
    tipo: String,
    valor: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }

    val opcionesBase = remember(tipo) {
        opcionesUnidadConceptoCotizacion(tipo)
    }

    // Conserva unidades antiguas o personalizadas al editar cotizaciones existentes.
    val opciones = remember(tipo, valor) {
        if (valor.isNotBlank() && valor !in opcionesBase) {
            listOf(valor) + opcionesBase
        } else {
            opcionesBase
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Unidad",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = !expandido }
        ) {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = unidadDefaultConcepto(tipo),
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandido
                    )
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 11.sp
                ),
                shape = RoundedCornerShape(7.dp)
            )

            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false },
                modifier = Modifier.heightIn(max = 280.dp)
            ) {
                opciones.forEach { unidad ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = unidad,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingIcon = {
                            if (unidad == valor) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        onClick = {
                            onValueChange(unidad)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}
