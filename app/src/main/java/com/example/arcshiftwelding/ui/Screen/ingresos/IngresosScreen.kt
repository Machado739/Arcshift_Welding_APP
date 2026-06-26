package com.example.arcshiftwelding.ui.Screen.ingresos

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arcshiftwelding.navigation.AppRoutes


@Composable
fun IngresosScreen(
    navController: NavController,
    viewModel: IngresosViewModel
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    var busqueda by remember { mutableStateOf("") }

    val ingresos by viewModel.ingresos.collectAsState()

    val ingresosFiltrados = ingresos.filter { ingreso ->
        val coincideCategoria =
            categoriaSeleccionada == "Todos" || ingreso.categoria == categoriaSeleccionada

        val coincideBusqueda =
            ingreso.cliente.contains(busqueda, ignoreCase = true) ||
                    ingreso.trabajo.contains(busqueda, ignoreCase = true) ||
                    ingreso.folio.contains(busqueda, ignoreCase = true)

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
        HeaderIngresos(navController = navController)

        Spacer(modifier = Modifier.height(8.dp))

        ResumenIngresos(ingresos = ingresos)

        Spacer(modifier = Modifier.height(12.dp))

        BarraBusquedaIngresos(
            busqueda = busqueda,
            onBusquedaChange = { busqueda = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                navController.navigate(AppRoutes.NUEVO_INGRESO)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nuevo Ingreso")
        }

        Spacer(modifier = Modifier.height(10.dp))

        FiltrosCategoriaIngresos(
            seleccionada = categoriaSeleccionada,
            onSeleccionar = {
                categoriaSeleccionada = it
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        ListadoIngresos(
            ingresos = ingresosFiltrados,
            onClickIngreso = { ingreso ->
                navController.navigate(AppRoutes.detalleIngreso(ingreso.id))
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderIngresos(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(onClick = { }) {
            Icon(Icons.Default.Menu, contentDescription = "Menú")
        }

        Text(
            text = "Ingreso",
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
fun ResumenIngresos(
    ingresos: List<IngresoUI>
) {
    val totalIngresos = ingresos.sumOf { it.totalNumero }
    val totalAnticipos = ingresos.sumOf { it.anticipoNumero }
    val totalPendiente = ingresos.sumOf { it.pendienteNumero }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Total ingresos",
            monto = totalIngresos.formatoDinero(),
            subtitulo = "Registrados",
            icono = Icons.Default.AttachMoney,
            color = Color(0xFF2563EB),
            fondo = Color(0xFFEFF6FF)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Anticipos",
            monto = totalAnticipos.formatoDinero(),
            subtitulo = "Cobrados",
            icono = Icons.Default.ArrowDownward,
            color = Color(0xFF16A34A),
            fondo = Color(0xFFF0FDF4)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Pendiente",
            monto = totalPendiente.formatoDinero(),
            subtitulo = "Por cobrar",
            icono = Icons.Default.Schedule,
            color = Color(0xFFF59E0B),
            fondo = Color(0xFFFFFBEB)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Cobros",
            monto = ingresos.size.toString(),
            subtitulo = "Registros",
            icono = Icons.Default.ReceiptLong,
            color = Color(0xFF7C3AED),
            fondo = Color(0xFFF5F3FF)
        )
    }
}

@Composable
fun CardResumenIngreso(
    modifier: Modifier = Modifier,
    titulo: String,
    monto: String,
    subtitulo: String,
    icono: ImageVector,
    color: Color,
    fondo: Color
) {
    Card(
        modifier = modifier.height(82.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(fondo, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = titulo,
                fontSize = 8.sp,
                color = Color.Gray,
                maxLines = 1
            )

            Text(
                text = monto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = subtitulo,
                fontSize = 7.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun BarraBusquedaIngresos(
    busqueda: String,
    onBusquedaChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    text = "Buscar ingreso...",
                    fontSize = 12.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
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
fun FiltrosCategoriaIngresos(
    seleccionada: String,
    onSeleccionar: (String) -> Unit
) {
    val categorias = listOf(
        "Todos",
        "Anticipos",
        "Pendientes",
        "Pagados",
        "Más"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(categorias){ categoria ->
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
                text = texto,
                maxLines = 1
            )
        },
        leadingIcon = {
            if (seleccionado) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (seleccionado) Color(0xFFE0ECFF) else Color.White,
            labelColor = if (seleccionado) Color(0xFF1D4ED8) else Color.DarkGray
        )
    )
}

@Composable
fun ListadoIngresos(
    ingresos: List<IngresoUI>,
    onClickIngreso: (IngresoUI) -> Unit
){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
            .padding(
                start = 0.dp,
                top = 0.dp,
                end = 0.dp,
                bottom = 8.dp
            )
    ) {
        items(ingresos) { ingreso ->
            ItemIngreso(
                ingreso = ingreso,
                onClick = {
                    onClickIngreso(ingreso)
                }
            )

        }
    }

}

@Composable
fun ItemIngreso(
    ingreso: IngresoUI,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoCategoriaIngreso(ingreso.categoria)


            Spacer(modifier = Modifier.width(10.dp))




            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ingreso.cliente,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )

                Text(
                    text = "Trabajo: ${ingreso.trabajo}",
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Folio: ${ingreso.folio}",
                    fontSize = 8.sp,
                    color = Color(0xFF2563EB),
                    modifier = Modifier
                        .background(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }

            DatosIngreso(
                titulo = "Total",
                valor = ingreso.total,
                color = Color(0xFF2563EB)
            )

            DatosIngreso(
                titulo = "Anticipo",
                valor = ingreso.anticipo,
                color = Color(0xFF16A34A)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(58.dp)
            ) {
                Text(
                    text = "Pendiente",
                    fontSize = 8.sp,
                    color = Color.Gray
                )

                Text(
                    text = ingreso.pendiente,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF97316)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = ingreso.categoria,
                    fontSize = 8.sp,
                    color = if (ingreso.categoria == "Pagados") Color(0xFF16A34A) else Color(0xFFF59E0B),
                    modifier = Modifier
                        .background(
                            color = if (ingreso.categoria == "Pagados") Color(0xFFEAF7EE) else Color(0xFFFFF7E6),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                )

                Text(
                    text = ingreso.fecha,
                    fontSize = 7.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun IconoCategoriaIngreso(
    categoria: String
) {
    val icono = when (categoria) {
        "Anticipos" -> Icons.Default.Savings
        "Pendientes" -> Icons.Default.Schedule
        "Pagados" -> Icons.Default.CheckCircle
        "Cobros" -> Icons.Default.AttachMoney
        "Transferencias" -> Icons.Default.AccountBalance
        "Efectivos" -> Icons.Default.Payments
        "Tarjetas" -> Icons.Default.CreditCard
        else -> Icons.Default.ReceiptLong
    }

    val color = when (categoria) {
        "Anticipos" -> Color(0xFF16A34A)
        "Pendientes" -> Color(0xFFF59E0B)
        "Pagados" -> Color(0xFF15803D)
        "Cobros" -> Color(0xFF2563EB)
        "Transferencias" -> Color(0xFF7C3AED)
        "Efectivos" -> Color(0xFF0891B2)
        "Tarjetas" -> Color(0xFFDB2777)
        else -> Color(0xFF64748B)
    }

    val fondo = when (categoria) {
        "Anticipos" -> Color(0xFFDCFCE7)
        "Pendientes" -> Color(0xFFFEF3C7)
        "Pagados" -> Color(0xFFD1FAE5)
        "Cobros" -> Color(0xFFDBEAFE)
        "Transferencias" -> Color(0xFFEDE9FE)
        "Efectivos" -> Color(0xFFCFFAFE)
        "Tarjetas" -> Color(0xFFFCE7F3)
        else -> Color(0xFFF1F5F9)
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .background(
                color = fondo,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = categoria,
            tint = color,
            modifier = Modifier.size(23.dp)
        )
    }
}

@Composable
fun DatosIngreso(
    titulo: String,
    valor: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(50.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 8.sp,
            color = Color.Gray
        )

        Text(
            text = valor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}