package com.example.arcshiftwelding.ui.Screen.empleados

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.Screen.notificaciones.CampanaNotificacionesPrincipal
import com.example.arcshiftwelding.ui.viewmodel.EmpleadosViewModel
import com.example.arcshiftwelding.ui.viewmodel.formatoMoneda
import com.example.arcshiftwelding.ui.theme.arcshiftColors

data class EmpleadoUI(
    val id: Int,
    val nombre: String,
    val puesto: String,
    val trabajo: String,
    val contrato: String,
    val pagoTotal: String,
    val periodoPago: String,
    val estado: String,
    val color: Color,
    val salario: Double,
    val fotoUri: String = ""
)

@Composable
fun EmpleadosScreen(
    navController: NavController,
    viewModel: EmpleadosViewModel
) {
    val empleados by viewModel.empleados.collectAsState()
    var textoBusqueda by rememberSaveable { mutableStateOf("") }
    var categoriaSeleccionada by rememberSaveable { mutableStateOf("Todos") }
    var resumenExpandido by rememberSaveable { mutableStateOf(false) }

    val empleadosFiltrados = remember(
        empleados,
        textoBusqueda,
        categoriaSeleccionada
    ) {
        val busqueda = textoBusqueda.trim()
        empleados.filter { empleado ->
            val coincideCategoria = when (categoriaSeleccionada) {
                "Todos" -> true
                "Activos" -> empleado.estado.equals("Activo", true)
                "Inactivos" -> empleado.estado.equals("Inactivo", true)
                "Soldadores" -> empleado.puesto.contains("Soldador", true)
                "Ayudantes" -> empleado.puesto.contains("Ayudante", true)
                else -> true
            }
            val coincideBusqueda = busqueda.isBlank() ||
                empleado.nombre.contains(busqueda, true) ||
                empleado.puesto.contains(busqueda, true) ||
                empleado.trabajo.contains(busqueda, true)
            coincideCategoria && coincideBusqueda
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        HeaderEmpleados(navController)

        Spacer(modifier = Modifier.height(6.dp))

        ResumenEmpleadosCompacto(
            empleados = empleados,
            expandido = resumenExpandido,
            onCambiar = { resumenExpandido = !resumenExpandido }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BuscadorEmpleados(
                textoBusqueda = textoBusqueda,
                onTextoBusquedaChange = { textoBusqueda = it },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = { navController.navigate(AppRoutes.NUEVO_EMPLEADO) },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo empleado")
            }
        }

        FiltrosCategoriaEmpleados(
            seleccionada = categoriaSeleccionada,
            onSeleccionar = { categoriaSeleccionada = it }
        )

        Text(
            text = "Empleados (${empleadosFiltrados.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
        )

        ListaEmpleados(
            empleados = empleadosFiltrados,
            onClickEmpleado = {
                navController.navigate(AppRoutes.detalleEmpleado(it.id))
            }
        )
    }
}

@Composable
fun HeaderEmpleados(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 20.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Empleados",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        CampanaNotificacionesPrincipal(navController)
        IconButton(
            onClick = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
        }
    }
}

@Composable
private fun ResumenEmpleadosCompacto(
    empleados: List<EmpleadoUI>,
    expandido: Boolean,
    onCambiar: () -> Unit
) {
    val total = empleados.size
    val activos = empleados.count { it.estado.equals("Activo", true) }
    val pagoActivo = empleados.filter { it.estado.equals("Activo", true) }.sumOf { it.salario }
    val trabajos = empleados
        .map { it.trabajo.trim() }
        .filter {
            it.isNotBlank() &&
                !it.equals("Sin trabajo asignado", true) &&
                !it.equals("Sin asignar", true)
        }
        .distinct()
        .size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onCambiar),
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ResumenEmpleadoDato(
                    titulo = "Total",
                    valor = total.toString(),
                    icono = Icons.Default.Groups,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                ResumenEmpleadoDato(
                    titulo = "Activos",
                    valor = activos.toString(),
                    icono = Icons.Default.PersonAdd,
                    color = MaterialTheme.arcshiftColors.success,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expandido) "Contraer" else "Desplegar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            AnimatedVisibility(visible = expandido) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResumenEmpleadoDato(
                        titulo = "Pago activo",
                        valor = pagoActivo.formatoMoneda(),
                        icono = Icons.Default.AttachMoney,
                        color = MaterialTheme.arcshiftColors.warning,
                        modifier = Modifier.weight(1f)
                    )
                    ResumenEmpleadoDato(
                        titulo = "Trabajos",
                        valor = trabajos.toString(),
                        icono = Icons.Default.Work,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ResumenEmpleadoDato(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(7.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
            Text(
                text = valor,
                fontWeight = FontWeight.Bold,
                fontSize = when {
                    valor.length >= 15 -> 9.sp
                    valor.length >= 11 -> 11.sp
                    else -> 14.sp
                },
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BuscadorEmpleados(
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = textoBusqueda,
        onValueChange = onTextoBusquedaChange,
        placeholder = { Text("Buscar empleado...", fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            if (textoBusqueda.isNotBlank()) {
                IconButton(onClick = { onTextoBusquedaChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar", modifier = Modifier.size(18.dp))
                }
            }
        },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(9.dp),
        singleLine = true
    )
}

@Composable
fun FiltrosCategoriaEmpleados(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf("Todos", "Activos", "Inactivos", "Soldadores", "Ayudantes")
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias) { categoria ->
            AssistChip(
                onClick = { onSeleccionar(categoria) },
                label = { Text(categoria, fontSize = 10.sp, maxLines = 1) },
                leadingIcon = {
                    if (seleccionada == categoria) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                    }
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (seleccionada == categoria) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    labelColor = if (seleccionada == categoria) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun ListaEmpleados(
    empleados: List<EmpleadoUI>,
    onClickEmpleado: (EmpleadoUI) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(7.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        if (empleados.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No hay empleados para los filtros seleccionados.",
                        modifier = Modifier.padding(18.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            items(items = empleados, key = { it.id }) { empleado ->
                ItemEmpleado(empleado) { onClickEmpleado(empleado) }
            }
        }
    }
}

@Composable
fun ItemEmpleado(
    empleado: EmpleadoUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImagenPerfilEmpleado(
                fotoUri = empleado.fotoUri,
                iniciales = obtenerInicialesEmpleado(empleado.nombre),
                modifier = Modifier.size(42.dp),
                colorFondo = MaterialTheme.colorScheme.primaryContainer,
                colorContenido = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = empleado.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    EstadoEmpleadoBadge(empleado.estado)
                }
                Text(
                    text = empleado.puesto,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                InfoEmpleadoLinea(
                    icono = Icons.Default.Work,
                    texto = empleado.trabajo
                )
                InfoEmpleadoLinea(
                    icono = Icons.Default.Badge,
                    texto = limpiarContratoListadoEmpleado(empleado.contrato)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(82.dp)
            ) {
                Text(
                    text = empleado.pagoTotal,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = when {
                        empleado.pagoTotal.length >= 12 -> 9.sp
                        empleado.pagoTotal.length >= 9 -> 10.sp
                        else -> 12.sp
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = empleado.periodoPago,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 8.sp,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Ver detalle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

fun limpiarContratoListadoEmpleado(contrato: String): String {
    val limpio = contrato.replace("Contrato", "", ignoreCase = true).trim()
    return limpio.ifBlank { "Sin modalidad definida" }
}

@Composable
fun InfoEmpleadoLinea(
    icono: ImageVector,
    texto: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = texto.ifBlank { "Sin información" },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EstadoEmpleadoBadge(estado: String) {
    val color = when {
        estado.equals("Activo", true) -> MaterialTheme.arcshiftColors.success
        estado.equals("Inactivo", true) -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.arcshiftColors.warning
    }
    Text(
        text = estado,
        color = color,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
