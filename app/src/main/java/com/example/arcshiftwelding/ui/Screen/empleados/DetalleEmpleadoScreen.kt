package com.example.arcshiftwelding.ui.Screen.empleados

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.navigation.AppRoutes

data class EmpleadoDetalleUI(
    val id: Int,
    val nombre: String,
    val puesto: String,
    val telefono: String,
    val fechaIngreso: String,
    val correo: String,
    val direccion: String,
    val porcentajeContrato: String,
    val trabajoActual: String,
    val pagoTotalSemana: String,
    val estado: String,
    val notas: String
)

data class TrabajoAsignadoEmpleadoUI(
    val id: Int,
    val nombreTrabajo: String,
    val inicio: String,
    val estado: String,
    val pagoTotal: String
)

@Composable
fun DetalleEmpleadoScreen(
    navController: NavController,
    empleadoId: Int,
    viewModel: EmpleadosViewModel
) {


    val empleadoEntity by viewModel
        .observarEmpleado(empleadoId)
        .collectAsState(initial = null)

    if (empleadoEntity == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            Text("Empleado no encontrado")
        }
        return
    }

    val empleado = empleadoEntity!!.toDetalleUi()

    val trabajosAsignados = emptyList<TrabajoAsignadoEmpleadoUI>()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(
                        start = 17.dp,
                        top = 8.dp,
                        end = 14.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }

                Text(
                    text = "Detalle Empleado",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        navController.navigate(AppRoutes.editarEmpleado(empleado.id))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar empleado"
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardPrincipalEmpleado(empleado = empleado)

            CardsContactoEmpleado(empleado = empleado)

            SeccionInformacionPersonalEmpleado(empleado = empleado)

            SeccionInformacionLaboralEmpleado(empleado = empleado)

            SeccionNotasDetalleEmpleado(empleado = empleado)

            /*    SeccionTrabajosAsignadosEmpleado(trabajos = trabajosAsignados)*/

            SeccionAccionesRapidasEmpleado(
                onLlamarClick = { },
                onMensajeClick = { },
                onEditarClick = {
                    navController.navigate(AppRoutes.editarEmpleado(empleado.id))
                },
                onEliminarClick = {
                    navController.navigate(AppRoutes.eliminarEmpleado(empleado.id))
                }
            )
        }
    }
}

@Composable
fun CardPrincipalEmpleado(
    empleado: EmpleadoDetalleUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0ECFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = obtenerInicialesEmpleado(empleado.nombre),
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = empleado.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = empleado.puesto,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BadgeEstadoEmpleado(
                        texto = empleado.estado,
                        color = Color(0xFF16A34A)
                    )

                    if (empleado.porcentajeContrato.isNotBlank()) {
                        BadgeEstadoEmpleado(
                            texto = empleado.porcentajeContrato,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }
        }
    }
}

fun obtenerInicialesEmpleado(nombre: String): String {
    return nombre
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
}

@Composable
fun BadgeEstadoEmpleado(
    texto: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = texto,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CardsContactoEmpleado(
    empleado: EmpleadoDetalleUI
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CardContactoEmpleado(
            titulo = "Teléfono",
            valor = empleado.telefono,
            /*   accion = "Llamar",*/
            icono = Icons.Default.Phone,
            color = Color(0xFF2563EB),
            modifier = Modifier.weight(1f)
        )

        CardContactoEmpleado(
            titulo = "Correo",
            valor = empleado.correo,
            /*  accion = "Enviar",*/
            icono = Icons.Default.Email,
            color = Color(0xFF7C3AED),
            modifier = Modifier.weight(1f)
        )

        CardContactoEmpleado(
            titulo = "Dirección",
            valor = empleado.direccion,
            /* accion = "Ver",*/
            icono = Icons.Default.LocationOn,
            color = Color(0xFF16A34A),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CardContactoEmpleado(
    titulo: String,
    valor: String,
    /*  accion: String,*/
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valor,
                style = MaterialTheme.typography.labelSmall,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))
            /*
                        Text(
                            text = accion,
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )*/
        }
    }
}

@Composable
fun SeccionInformacionPersonalEmpleado(
    empleado: EmpleadoDetalleUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Información personal",
                icono = Icons.Default.Info,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            ItemInfoEmpleado(
                icono = Icons.Default.Phone,
                titulo = "Teléfono",
                valor = empleado.telefono
            )

            Divider(color = Color(0xFFE5E7EB))

            ItemInfoEmpleado(
                icono = Icons.Default.CalendarMonth,
                titulo = "Fecha de ingreso",
                valor = empleado.fechaIngreso
            )

            Divider(color = Color(0xFFE5E7EB))

            ItemInfoEmpleado(
                icono = Icons.Default.Email,
                titulo = "Correo",
                valor = empleado.correo
            )

            Divider(color = Color(0xFFE5E7EB))

            ItemInfoEmpleado(
                icono = Icons.Default.LocationOn,
                titulo = "Dirección",
                valor = empleado.direccion
            )
        }
    }
}

@Composable
fun SeccionInformacionLaboralEmpleado(
    empleado: EmpleadoDetalleUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Información laboral",
                icono = Icons.Default.Work,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            ItemInfoEmpleado(
                icono = Icons.Default.Work,
                titulo = "Puesto",
                valor = empleado.puesto
            )

            Divider(color = Color(0xFFE5E7EB))

            ItemInfoEmpleado(
                icono = Icons.Default.Badge,
                titulo = "Contrato / pago",
                valor = empleado.porcentajeContrato.ifBlank { "Sin definir" }
            )

            Divider(color = Color(0xFFE5E7EB))

            ItemInfoEmpleado(
                icono = Icons.Default.Assignment,
                titulo = "Trabajo actual",
                valor = empleado.trabajoActual
            )

            Divider(color = Color(0xFFE5E7EB))
            /*
                        ItemInfoEmpleado(
                            icono = Icons.Default.AttachMoney,
                            titulo = "Pago total semana",
                            valor = empleado.pagoTotalSemana
                        )
            */
            Divider(color = Color(0xFFE5E7EB))

            ItemInfoEmpleado(
                icono = Icons.Default.CheckCircle,
                titulo = "Estado",
                valor = empleado.estado,
                valorColor = Color(0xFF16A34A)
            )
        }
    }
}

@Composable
fun SeccionNotasDetalleEmpleado(
    empleado: EmpleadoDetalleUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Notas del empleado",
                icono = Icons.Default.Message,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = empleado.notas,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
fun ItemInfoEmpleado(
    icono: ImageVector,
    titulo: String,
    valor: String,
    valorColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(17.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.labelSmall,
            color = valorColor,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SeccionTrabajosAsignadosEmpleado(
    trabajos: List<TrabajoAsignadoEmpleadoUI>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Trabajos asignados",
                icono = Icons.Default.Assignment,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trabajos.forEach { trabajo ->
                    ItemTrabajoAsignadoEmpleado(
                        trabajo = trabajo
                    )
                }
            }
        }
    }
}

@Composable
fun ItemTrabajoAsignadoEmpleado(
    trabajo: TrabajoAsignadoEmpleadoUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = trabajo.nombreTrabajo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = trabajo.inicio,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray,
                    maxLines = 1
                )

                Text(
                    text = trabajo.estado,
                    style = MaterialTheme.typography.labelSmall,
                    color = obtenerColorEstadoTrabajo(trabajo.estado),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Pago total",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1
                )

                Text(
                    text = trabajo.pagoTotal,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

fun obtenerColorEstadoTrabajo(estado: String): Color {
    return when (estado) {
        "En proceso" -> Color(0xFFF59E0B)
        "Terminado" -> Color(0xFF16A34A)
        "Pendiente" -> Color(0xFF64748B)
        else -> Color.Gray
    }
}

@Composable
fun SeccionAccionesRapidasEmpleado(
    onLlamarClick: () -> Unit,
    onMensajeClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Acciones rápidas",
                icono = Icons.Default.Bolt,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonAccionEmpleado(
                    texto = "Llamar",
                    icono = Icons.Default.Phone,
                    color = Color(0xFF16A34A),
                    onClick = onLlamarClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionEmpleado(
                    texto = "Mensaje",
                    icono = Icons.Default.Message,
                    color = Color(0xFF2563EB),
                    onClick = onMensajeClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionEmpleado(
                    texto = "Editar",
                    icono = Icons.Default.Edit,
                    color = Color(0xFFF59E0B),
                    onClick = onEditarClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionEmpleado(
                    texto = "Eliminar",
                    icono = Icons.Default.Delete,
                    color = Color(0xFFDC2626),
                    onClick = onEliminarClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BotonAccionEmpleado(
    texto: String,
    icono: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                color = color,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TituloSeccionEmpleado(
    titulo: String,
    icono: ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}