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
import com.example.arcshiftwelding.ui.gastos.CategoriaChip

data class IngresoUI(
    val id: Int,
    val cliente: String,
    val trabajo: String,
    val folio: String,
    val total: String,
    val anticipo: String,
    val pendiente: String,
    val categoria: String,
    val fecha: String,
    val color: Color
)

@Composable
fun IngresosScreen(
    navController: NavController


) {

    var categoriaSeleccionada by remember { mutableStateOf("Todos") }



    val ingresos = listOf(
        IngresoUI(
            id = 1,
            cliente = "Eduardo Barrios",
            trabajo = "Tejaban 6x4m",
            folio = "001",
            total = "$12,000",
            anticipo = "$7,000",
            pendiente = "$5,000",
            categoria = "Pagado",
            fecha = "19/05/2026",
            color = Color(0xFF2563EB)
        ),
        IngresoUI(
            id = 2,
            cliente = "Jose Vera",
            trabajo = "Portón 123\"x85\"",
            folio = "002",
            total = "$12,000",
            anticipo = "$6,000",
            pendiente = "$6,000",
            categoria = "Pendiente",
            fecha = "26/05/2026",
            color = Color(0xFF16A34A)
        ),
        IngresoUI(
            id = 3,
            cliente = "Maria Lopez",
            trabajo = "Escalera metálica",
            folio = "003",
            total = "$8,500",
            anticipo = "$4,000",
            pendiente = "$4,500",
            categoria = "Folio",
            fecha = "20/05/2026",
            color = Color(0xFFF97316)
        ),
        IngresoUI(
            id = 4,
            cliente = "Constructora Del Norte",
            trabajo = "Estructura metálica",
            folio = "004",
            total = "$15,000",
            anticipo = "$8,000",
            pendiente = "$7,000",
            categoria = "Pendiente",
            fecha = "22/05/2026",
            color = Color(0xFF7C3AED)
        ),
        IngresoUI(
            id = 5,
            cliente = "Alberto Ruiz",
            trabajo = "Reja perimetral",
            folio = "005",
            total = "$9,000",
            anticipo = "$5,000",
            pendiente = "$4,000",
            categoria = "Pagado",
            fecha = "16/05/2026",
            color = Color(0xFF0891B2)
        )
    )
    
    val ingresosFiltrados = ingresos.filter { ingreso -> 
        categoriaSeleccionada == "Totos"|| ingreso.categoria == categoriaSeleccionada }
    
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
        
   
        ResumenIngresos()

        Spacer(modifier = Modifier.height(12.dp))

        BarraBusquedaIngresos()

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
            seleccionada= categoriaSeleccionada,
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
fun ResumenIngresos() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Total ingresos",
            monto = "$24,000",
            subtitulo = "Este mes",
            icono = Icons.Default.AttachMoney,
            color = Color(0xFF2563EB),
            fondo = Color(0xFFEFF6FF)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Anticipos",
            monto = "$13,000",
            subtitulo = "Este mes",
            icono = Icons.Default.ArrowDownward,
            color = Color(0xFF16A34A),
            fondo = Color(0xFFF0FDF4)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Pendiente",
            monto = "$11,000",
            subtitulo = "Este mes",
            icono = Icons.Default.Schedule,
            color = Color(0xFFF59E0B),
            fondo = Color(0xFFFFFBEB)
        )

        CardResumenIngreso(
            modifier = Modifier.weight(1f),
            titulo = "Cobros realizados",
            monto = "$13,000",
            subtitulo = "Este mes",
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
fun BarraBusquedaIngresos() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
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
fun FiltrosIngresos() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ChipIngreso("Todos", true, Color(0xFF2563EB))
        ChipIngreso("Anticipos", false, Color(0xFF16A34A))
        ChipIngreso("Pendientes", false, Color(0xFFF59E0B))
        ChipIngreso("Pagados", false, Color(0xFF15803D))
        ChipIngreso("Más", false, Color.DarkGray)
    }
}

@Composable
fun ChipIngreso(
    texto: String,
    seleccionado: Boolean,
    color: Color
) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = texto,
                fontSize = 10.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = if (seleccionado) Icons.Default.GridView else Icons.Default.Circle,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = color
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (seleccionado) Color(0xFFEFF6FF) else Color.White,
            labelColor = if (seleccionado) Color(0xFF2563EB) else Color.DarkGray
        )
    )
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

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = ingreso.color.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = ingreso.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1.25f)
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
                    color = if (ingreso.categoria == "Pagado") Color(0xFF16A34A) else Color(0xFFF59E0B),
                    modifier = Modifier
                        .background(
                            color = if (ingreso.categoria == "Pagado") Color(0xFFEAF7EE) else Color(0xFFFFF7E6),
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

            IconButton(
                onClick = { },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Opciones",
                    modifier = Modifier.size(18.dp),
                    tint = Color.DarkGray
                )
            }
        }
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