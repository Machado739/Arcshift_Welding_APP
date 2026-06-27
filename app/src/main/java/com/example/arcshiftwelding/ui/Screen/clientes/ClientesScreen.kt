package com.example.arcshiftwelding.ui.Screen.clientes

import android.R.attr.shape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.runtime.*
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
import com.example.arcshiftwelding.navigation.BottomNavigationBar
import okhttp3.internal.http2.Header

data class ClienteUI(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val ubicacion: String,
    val correo: String,
    val cotizaciones: Int,
    val estado: String,
    val tipo: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(
    navController: NavController,
    viewModel: ClientesViewModel
) {

    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    val clientes by viewModel.clientes.collectAsState()

    val clientesFiltrados = clientes.filter { cliente ->
        val coincideEstado = when (categoriaSeleccionada) {
            "Todos" -> true
            "Activos" -> cliente.estado == "Activo"
            "Inactivos" -> cliente.estado == "Inactivo"
            "Pendientes" -> cliente.estado == "Pendiente"
            else -> true
        }

        val coincideBusqueda =
            cliente.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    cliente.telefono.contains(textoBusqueda, ignoreCase = true) ||
                    cliente.correo.contains(textoBusqueda, ignoreCase = true) ||
                    cliente.ubicacion.contains(textoBusqueda, ignoreCase = true)

        coincideEstado && coincideBusqueda
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
            HeaderClientes(navController = navController)


            Spacer(modifier = Modifier.height(8.dp))

            ResumenClientes(clientes = clientes)

            Spacer(modifier = Modifier.height(12.dp))

            BuscadorClientes(
                textoBusqueda = textoBusqueda,
                onTextoBusquedaChange = {
                    textoBusqueda = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    navController.navigate(AppRoutes.NUEVO_CLIENTE)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo Cliente")
            }

            Spacer(modifier = Modifier.height(10.dp))


            FiltrosCategoriaClientes(
                seleccionada = categoriaSeleccionada,
                onSeleccionar = {
                    categoriaSeleccionada = it
                }
            )
            Spacer(modifier = Modifier.height(8.dp))

            ListaClientes(
                clientes = clientesFiltrados,
                onClickCliente = { cliente: ClienteUI ->
                    navController.navigate(AppRoutes.detalleCliente(cliente.id))
                }
            )
            }
        }



@Composable
fun HeaderClientes(
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
            Icon(Icons.Default.Menu, contentDescription = "Menú")
        }

        Text(
            text = "Clientes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
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
fun ResumenClientes(
    clientes: List<ClienteUI>
) {
    val total = clientes.size
    val activos = clientes.count { it.estado == "Activo" }
    val inactivos = clientes.count { it.estado == "Inactivo" }
    val pendientes = clientes.count { it.estado == "Pendiente" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ResumenClienteCard(
            titulo = "Total clientes",
            valor = total.toString(),
            subtitulo = "Registrados",
            icono = Icons.Default.Groups,
            colorIcono = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )

        ResumenClienteCard(
            titulo = "Activos",
            valor = activos.toString(),
            subtitulo = "Disponibles",
            icono = Icons.Default.PersonAdd,
            colorIcono = Color(0xFF16A34A),
            modifier = Modifier.weight(1f)
        )

        ResumenClienteCard(
            titulo = "Pendientes",
            valor = pendientes.toString(),
            subtitulo = "Por revisar",
            icono = Icons.Default.AccessTime,
            colorIcono = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )

        ResumenClienteCard(
            titulo = "Inactivos",
            valor = inactivos.toString(),
            subtitulo = "Desactivados",
            icono = Icons.Default.Work,
            colorIcono = Color(0xFF7C3AED),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ResumenClienteCard(
    titulo: String,
    valor: String,
    subtitulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
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
                color = Color.Gray
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun BuscadorClientes(
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
                Text("Buscar cliente...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier.weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        OutlinedButton(
            onClick = { },
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Filtros",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BotonNuevoCliente(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2563EB)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color.White
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Nuevo cliente",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FiltrosCategoriaClientes(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf(
        "Todos",
        "Activos",
        "Inactivos",
        "Pendientes"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias) { categoria ->
            CategoriaChip(
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
fun CategoriaChip(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text =texto,
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
fun ItemCliente(
    cliente: ClienteUI,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(cliente.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = cliente.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cliente.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                InfoClienteLinea(Icons.Default.Phone, cliente.telefono)
                InfoClienteLinea(Icons.Default.LocationOn, cliente.ubicacion)

                Text(
                    text = cliente.tipo,
                    style = MaterialTheme.typography.labelSmall,
                    color = cliente.color,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.width(120.dp),
                horizontalAlignment = Alignment.Start
            ) {
                EstadoClienteBadge(cliente.estado)

                Spacer(modifier = Modifier.height(8.dp))

                InfoClienteLinea(
                    icono = Icons.Default.Email,
                    texto = cliente.correo
                )

                InfoClienteLinea(
                    icono = Icons.Default.Description,
                    texto = "${cliente.cotizaciones} cotizaciones"
                )
            }
        }
    }
}

@Composable
fun ListaClientes(
    clientes: List<ClienteUI>,
    onClickCliente: (ClienteUI) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()

    ) {
        items(clientes) { cliente ->
            ItemCliente(
                cliente = cliente,
                onClick = {
                    onClickCliente(cliente)
                }

            )
        }


    }
}


@Composable
fun InfoClienteLinea(
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
fun EstadoClienteBadge(
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
            fontWeight = FontWeight.Bold
        )
    }
}

