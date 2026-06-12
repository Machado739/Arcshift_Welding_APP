package com.example.arcshiftwelding.ui.clientes

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
    navController: NavController
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }

    val clientes = listOf(
        ClienteUI(
            id = 1,
            nombre = "Eduardo Barrios",
            telefono = "614 123 4567",
            ubicacion = "Chihuahua, Chihuahua",
            correo = "eduardo@correo.com",
            cotizaciones = 2,
            estado = "Activo",
            tipo = "Cliente desde: 10/05/2026",
            color = Color(0xFF2563EB)
        ),
        ClienteUI(
            id = 2,
            nombre = "Jose Vera",
            telefono = "614 987 6543",
            ubicacion = "Cuauhtémoc, Chihuahua",
            correo = "severa@gmail.com",
            cotizaciones = 1,
            estado = "Activo",
            tipo = "Cliente desde: 12/05/2026",
            color = Color(0xFF16A34A)
        ),
        ClienteUI(
            id = 3,
            nombre = "Maria Lopez",
            telefono = "614 555 1122",
            ubicacion = "Delicias, Chihuahua",
            correo = "mlopez@gmail.com",
            cotizaciones = 1,
            estado = "Activo",
            tipo = "Cliente desde: 15/05/2026",
            color = Color(0xFFF97316)
        ),
        ClienteUI(
            id = 4,
            nombre = "Constructora del Norte",
            telefono = "614 222 3344",
            ubicacion = "Chihuahua, Chihuahua",
            correo = "contacto@cdn.com",
            cotizaciones = 3,
            estado = "Activo",
            tipo = "Cliente desde: 16/05/2026",
            color = Color(0xFF7C3AED)
        ),
        ClienteUI(
            id = 5,
            nombre = "Alberto Ruiz",
            telefono = "614 444 6577",
            ubicacion = "Juárez, Chihuahua",
            correo = "alberto.ruiz@mail.com",
            cotizaciones = 0,
            estado = "Inactivo",
            tipo = "Cliente desde: 18/05/2026",
            color = Color(0xFF64748B)
        ),
        ClienteUI(
            id = 6,
            nombre = "Cliente General",
            telefono = "614 000 8999",
            ubicacion = "Chihuahua, Chihuahua",
            correo = "cliente@correo.com",
            cotizaciones = 1,
            estado = "Pendiente",
            tipo = "Cliente desde: 20/05/2026",
            color = Color(0xFFEAB308)
        ),
        ClienteUI(
            id = 7,
            nombre = "Carlos Mendoza",
            telefono = "614 321 7788",
            ubicacion = "Chihuahua, Chihuahua",
            correo = "cmendoza@gmail.com",
            cotizaciones = 4,
            estado = "Activo",
            tipo = "Cliente desde: 22/05/2026",
            color = Color(0xFF0EA5E9)
        ),
        ClienteUI(
            id = 8,
            nombre = "Taller Metalúrgico Ramírez",
            telefono = "614 678 1122",
            ubicacion = "Aldama, Chihuahua",
            correo = "ventas@tmramirez.com",
            cotizaciones = 6,
            estado = "Activo",
            tipo = "Cliente desde: 24/05/2026",
            color = Color(0xFFDC2626)
        ),
        ClienteUI(
            id = 9,
            nombre = "Ana Torres",
            telefono = "614 456 2233",
            ubicacion = "Delicias, Chihuahua",
            correo = "ana.torres@hotmail.com",
            cotizaciones = 2,
            estado = "Activo",
            tipo = "Cliente desde: 25/05/2026",
            color = Color(0xFF14B8A6)
        ),
        ClienteUI(
            id = 10,
            nombre = "Servicios Industriales Vega",
            telefono = "614 852 3698",
            ubicacion = "Chihuahua, Chihuahua",
            correo = "contacto@vegaindustrial.com",
            cotizaciones = 5,
            estado = "Pendiente",
            tipo = "Cliente desde: 26/05/2026",
            color = Color(0xFF8B5CF6)
        ),
        ClienteUI(
            id = 11,
            nombre = "Roberto Salas",
            telefono = "614 741 2589",
            ubicacion = "Camargo, Chihuahua",
            correo = "rsalas@gmail.com",
            cotizaciones = 1,
            estado = "Activo",
            tipo = "Cliente desde: 27/05/2026",
            color = Color(0xFF10B981)
        ),
        ClienteUI(
            id = 12,
            nombre = "Agropecuaria El Valle",
            telefono = "614 963 1478",
            ubicacion = "Cuauhtémoc, Chihuahua",
            correo = "administracion@elvalle.com",
            cotizaciones = 3,
            estado = "Activo",
            tipo = "Cliente desde: 28/05/2026",
            color = Color(0xFFF59E0B)
        ),
        ClienteUI(
            id = 13,
            nombre = "Miguel Herrera",
            telefono = "614 147 8523",
            ubicacion = "Juárez, Chihuahua",
            correo = "miguelh@gmail.com",
            cotizaciones = 0,
            estado = "Inactivo",
            tipo = "Cliente desde: 29/05/2026",
            color = Color(0xFF6B7280)
        ),
        ClienteUI(
            id = 14,
            nombre = "Transportes del Norte",
            telefono = "614 852 7410",
            ubicacion = "Juárez, Chihuahua",
            correo = "logistica@tdn.com",
            cotizaciones = 7,
            estado = "Activo",
            tipo = "Cliente desde: 30/05/2026",
            color = Color(0xFFEF4444)
        ),
        ClienteUI(
            id = 15,
            nombre = "Laura Sánchez",
            telefono = "614 555 8899",
            ubicacion = "Parral, Chihuahua",
            correo = "laurasanchez@gmail.com",
            cotizaciones = 2,
            estado = "Pendiente",
            tipo = "Cliente desde: 01/06/2026",
            color = Color(0xFFEC4899)
        ),
        ClienteUI(
            id = 16,
            nombre = "Constructora Horizonte",
            telefono = "614 777 4411",
            ubicacion = "Chihuahua, Chihuahua",
            correo = "proyectos@horizonte.com",
            cotizaciones = 8,
            estado = "Activo",
            tipo = "Cliente desde: 02/06/2026",
            color = Color(0xFF3B82F6)
        ),
        ClienteUI(
            id = 17,
            nombre = "Ricardo Chávez",
            telefono = "614 333 7788",
            ubicacion = "Delicias, Chihuahua",
            correo = "rchavez@gmail.com",
            cotizaciones = 1,
            estado = "Activo",
            tipo = "Cliente desde: 03/06/2026",
            color = Color(0xFF22C55E)
        ),
        ClienteUI(
            id = 18,
            nombre = "Soldaduras Industriales García",
            telefono = "614 888 9900",
            ubicacion = "Chihuahua, Chihuahua",
            correo = "ventas@sigarcia.com",
            cotizaciones = 9,
            estado = "Activo",
            tipo = "Cliente desde: 04/06/2026",
            color = Color(0xFF7C3AED)
        ),
        ClienteUI(
            id = 19,
            nombre = "Patricia Moreno",
            telefono = "614 123 9876",
            ubicacion = "Aldama, Chihuahua",
            correo = "paty.moreno@gmail.com",
            cotizaciones = 3,
            estado = "Activo",
            tipo = "Cliente desde: 05/06/2026",
            color = Color(0xFF06B6D4)
        ),
        ClienteUI(
            id = 20,
            nombre = "Grupo Constructor Alfa",
            telefono = "614 456 7890",
            ubicacion = "Chihuahua, Chihuahua",
            correo = "contacto@grupoalfa.com",
            cotizaciones = 12,
            estado = "Activo",
            tipo = "Cliente desde: 06/06/2026",
            color = Color(0xFF2563EB)
        )
    )
    val clientesFiltrados = clientes.filter { cliente ->
        when (categoriaSeleccionada) {
            "Todos" -> true
            "Activos" -> cliente.estado == "Activo"
            "Inactivos" -> cliente.estado == "Inactivo"
            "Pendientes" -> cliente.estado == "Pendiente"
            else -> true
        }
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

            ResumenClientes()

            Spacer(modifier = Modifier.height(12.dp))

            BuscadorClientes()

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
fun ResumenClientes() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ResumenClienteCard(
            titulo = "Total clientes",
            valor = "18",
            subtitulo = "Registrados",
            icono = Icons.Default.Groups,
            colorIcono = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )

        ResumenClienteCard(
            titulo = "Clientes activos",
            valor = "15",
            subtitulo = "Este mes",
            icono = Icons.Default.PersonAdd,
            colorIcono = Color(0xFF16A34A),
            modifier = Modifier.weight(1f)
        )

        ResumenClienteCard(
            titulo = "Nuevos este mes",
            valor = "3",
            subtitulo = "Este mes",
            icono = Icons.Default.AccessTime,
            colorIcono = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f)
        )

        ResumenClienteCard(
            titulo = "Inactivos",
            valor = "3",
            subtitulo = "Este mes",
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
fun BuscadorClientes() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {  },
            placeholder = {
                Text("Buscar cliente...")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
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
            Icon(imageVector = Icons.Default.FilterList, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Filtros")
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

