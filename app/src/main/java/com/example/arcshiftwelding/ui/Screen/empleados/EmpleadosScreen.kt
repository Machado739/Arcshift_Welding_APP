package com.example.arcshiftwelding.ui.Screen.empleados

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes

data class EmpleadoUI(
    val id: Int,
    val nombre: String,
    val puesto: String,
    val trabajo: String,
    val contrato: String,
    val pagoTotal: String,
    val periodoPago: String,
    val estado: String,
    val color: Color
)

@Composable
fun EmpleadosScreen(
    navController: NavController
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    val empleados = listOf(
        EmpleadoUI(
            id = 1,
            nombre = "Jaime Lozano",
            puesto = "Ayudante General",
            trabajo = "Tejaban 6x4m",
            contrato = "Contrato: 20%",
            pagoTotal = "$980",
            periodoPago = "Pago total",
            estado = "Activo",
            color = Color(0xFF2563EB)
        ),
        EmpleadoUI(
            id = 2,
            nombre = "Carlos Martínez",
            puesto = "Soldador",
            trabajo = "Portón 123\"x85\"",
            contrato = "Contrato: 25%",
            pagoTotal = "$2,730",
            periodoPago = "Pago total",
            estado = "Activo",
            color = Color(0xFF16A34A)
        ),
        EmpleadoUI(
            id = 3,
            nombre = "Pedro Sánchez",
            puesto = "Ayudante",
            trabajo = "Estructura metálica",
            contrato = "Contrato: 15%",
            pagoTotal = "$645",
            periodoPago = "Pago total",
            estado = "Activo",
            color = Color(0xFFF59E0B)
        ),
        EmpleadoUI(
            id = 4,
            nombre = "Miguel Torres",
            puesto = "Soldador",
            trabajo = "Tejaban 6x4m",
            contrato = "Contrato: 30%",
            pagoTotal = "$1,470",
            periodoPago = "Pago total",
            estado = "Activo",
            color = Color(0xFF7C3AED)
        ),
        EmpleadoUI(
            id = 5,
            nombre = "Roberto Salas",
            puesto = "Soldador",
            trabajo = "Escalera metálica",
            contrato = "Contrato: 20%",
            pagoTotal = "$1,250",
            periodoPago = "Pago total",
            estado = "Inactivo",
            color = Color(0xFF64748B)
        ),
        EmpleadoUI(
            id = 6,
            nombre = "Luis Hernández",
            puesto = "Ayudante",
            trabajo = "Reparación de barandal",
            contrato = "Contrato: 10%",
            pagoTotal = "$420",
            periodoPago = "Pago total",
            estado = "Activo",
            color = Color(0xFF0EA5E9)
        )
    )

    val empleadosFiltrados = empleados.filter { empleado ->
        val coincideCategoria = when (categoriaSeleccionada) {
            "Todos" -> true
            "Activos" -> empleado.estado == "Activo"
            "Soldadores" -> empleado.puesto.contains("Soldador", ignoreCase = true)
            "Ayudantes" -> empleado.puesto.contains("Ayudante", ignoreCase = true)
            else -> true
        }

        val coincideBusqueda =
            empleado.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    empleado.puesto.contains(textoBusqueda, ignoreCase = true) ||
                    empleado.trabajo.contains(textoBusqueda, ignoreCase = true)

        coincideCategoria && coincideBusqueda
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(
                start = 8.dp,
                top = 0.dp,
                end = 8.dp,
                bottom = 8.dp
            )
    ) {
        HeaderEmpleados(navController = navController)

        Spacer(modifier = Modifier.height(8.dp))

        ResumenEmpleados()

        Spacer(modifier = Modifier.height(12.dp))

        BuscadorEmpleados(
            textoBusqueda = textoBusqueda,
            onTextoBusquedaChange = {
                textoBusqueda = it
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        FiltrosCategoriaEmpleados(
            seleccionada = categoriaSeleccionada,
            onSeleccionar = {
                categoriaSeleccionada = it
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        BotonNuevoEmpleado(
            onClick = {
                navController.navigate(AppRoutes.NUEVO_EMPLEADO)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ListaEmpleados(
            empleados = empleadosFiltrados,
            onClickEmpleado = { empleado ->
                navController.navigate(AppRoutes.detalleEmpleado(empleado.id))
            }
        )
    }
}

@Composable
fun HeaderEmpleados(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú"
            )
        }

        Text(
            text = "Empleados",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones"
            )
        }

        IconButton(
            onClick = {
                navController.navigate(AppRoutes.LOGIN) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Salir"
            )
        }
    }
}

@Composable
fun ResumenEmpleados() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ResumenEmpleadoCard(
            titulo = "Total empleados",
            valor = "12",
            subtitulo = "Registrados",
            icono = Icons.Default.Groups,
            colorIcono = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )

        ResumenEmpleadoCard(
            titulo = "Activos",
            valor = "10",
            subtitulo = "Empleados",
            icono = Icons.Default.PersonAdd,
            colorIcono = Color(0xFF16A34A),
            modifier = Modifier.weight(1f)
        )

        ResumenEmpleadoCard(
            titulo = "Pago total",
            valor = "$9,800",
            subtitulo = "Semana",
            icono = Icons.Default.AttachMoney,
            colorIcono = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )

        ResumenEmpleadoCard(
            titulo = "Trabajos",
            valor = "6",
            subtitulo = "Activos",
            icono = Icons.Default.Work,
            colorIcono = Color(0xFF7C3AED),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ResumenEmpleadoCard(
    titulo: String,
    valor: String,
    subtitulo: String,
    icono: ImageVector,
    colorIcono: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(105.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(colorIcono.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BuscadorEmpleados(
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onTextoBusquedaChange,
            placeholder = {
                Text("Buscar empleado...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )

        OutlinedButton(
            onClick = { },
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text("Filtros")
        }
    }
}

@Composable
fun FiltrosCategoriaEmpleados(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf(
        "Todos",
        "Activos",
        "Soldadores",
        "Ayudantes"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias) { categoria ->
            CategoriaEmpleadoChip(
                texto = categoria,
                seleccionado = seleccionada == categoria,
                onClick = {
                    onSeleccionar(categoria)
                }
            )
        }
    }
}

@Composable
fun CategoriaEmpleadoChip(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = texto,
                maxLines = 1
            )
        },
        leadingIcon = {
            if (seleccionado) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (seleccionado) Color(0xFFE0ECFF) else Color.White,
            labelColor = if (seleccionado) Color(0xFF2563EB) else Color.DarkGray
        )
    )
}

@Composable
fun BotonNuevoEmpleado(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1D4ED8)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Nuevo empleado",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ListaEmpleados(
    empleados: List<EmpleadoUI>,
    onClickEmpleado: (EmpleadoUI) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(empleados) { empleado ->
            ItemEmpleado(
                empleado = empleado,
                onClick = {
                    onClickEmpleado(empleado)
                }
            )
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
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = empleado.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = empleado.puesto,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                InfoEmpleadoLinea(
                    icono = Icons.Default.Work,
                    texto = "Trabajo: ${empleado.trabajo}"
                )

                InfoEmpleadoLinea(
                    icono = Icons.Default.Badge,
                    texto = empleado.contrato
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.width(78.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = empleado.pagoTotal,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = empleado.periodoPago,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                EstadoEmpleadoBadge(empleado.estado)
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalle",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun InfoEmpleadoLinea(
    icono: ImageVector,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EstadoEmpleadoBadge(
    estado: String
) {
    val color = when (estado) {
        "Activo" -> Color(0xFF16A34A)
        "Inactivo" -> Color(0xFF64748B)
        "Pendiente" -> Color(0xFFEAB308)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = estado,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}