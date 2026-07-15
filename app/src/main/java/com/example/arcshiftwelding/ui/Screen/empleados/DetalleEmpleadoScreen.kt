package com.example.arcshiftwelding.ui.Screen.empleados

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.navigation.AppRoutes
import com.example.arcshiftwelding.ui.viewmodel.EmpleadosViewModel
import com.example.arcshiftwelding.ui.viewmodel.toDetalleUi
import com.example.arcshiftwelding.ui.theme.arcshiftColors

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
    val notas: String,
    val fotoUri: String = ""
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


    val context = LocalContext.current
    val empleadoEntity by viewModel
        .observarEmpleado(empleadoId)
        .collectAsState(initial = null)

    val proyectoActual by remember(empleadoId) {
        ArcshiftWeldingDatabase.getDatabase(context)
            .proyectoEmpleadoDao()
            .observarProyectoActualEmpleado(empleadoId)
    }.collectAsState(initial = null)

    if (empleadoEntity == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("Empleado no encontrado")
        }
        return
    }

    val empleadoBase = empleadoEntity!!.toDetalleUi()
    val empleado = empleadoBase.copy(
        trabajoActual = proyectoActual?.nombreProyecto ?: empleadoBase.trabajoActual
    )

    val trabajosAsignados = emptyList<TrabajoAsignadoEmpleadoUI>()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
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
        containerColor = MaterialTheme.colorScheme.background,
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
                onLlamarClick = {
                    val telefono = empleadoEntity!!.telefono.trim()
                    if (telefono.isNotBlank()) {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.fromParts("tel", telefono, null)
                        )
                        runCatching { context.startActivity(intent) }
                    }
                },
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
            containerColor = MaterialTheme.colorScheme.surface
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
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                ImagenPerfilEmpleado(
                    fotoUri = empleado.fotoUri,
                    iniciales = obtenerInicialesEmpleado(empleado.nombre),
                    modifier = Modifier.fillMaxSize(),
                    colorFondo = MaterialTheme.colorScheme.primaryContainer,
                    colorContenido = MaterialTheme.colorScheme.primary
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.arcshiftColors.success)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BadgeEstadoEmpleado(
                        texto = empleado.estado,
                        color = MaterialTheme.arcshiftColors.success
                    )

                    if (empleado.porcentajeContrato.isNotBlank()) {
                        BadgeEstadoEmpleado(
                            texto = empleado.porcentajeContrato,
                            color = MaterialTheme.colorScheme.primary
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardsContactoEmpleado(
    empleado: EmpleadoDetalleUI
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardContactoEmpleado(
                titulo = "Teléfono",
                valor = empleado.telefono,
                icono = Icons.Default.Phone,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                onLongClick = {
                    copiarInformacionEmpleado(
                        context = context,
                        clipboardManager = clipboardManager,
                        titulo = "Teléfono",
                        valor = empleado.telefono
                    )
                },
                modifier = Modifier.weight(1f)
            )

            CardContactoEmpleado(
                titulo = "Correo",
                valor = empleado.correo,
                icono = Icons.Default.Email,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 2,
                onLongClick = {
                    copiarInformacionEmpleado(
                        context = context,
                        clipboardManager = clipboardManager,
                        titulo = "Correo",
                        valor = empleado.correo
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        CardContactoEmpleado(
            titulo = "Dirección",
            valor = empleado.direccion,
            icono = Icons.Default.LocationOn,
            color = MaterialTheme.arcshiftColors.success,
            maxLines = 3,
            onLongClick = {
                copiarInformacionEmpleado(
                    context = context,
                    clipboardManager = clipboardManager,
                    titulo = "Dirección",
                    valor = empleado.direccion
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Mantén presionada una tarjeta para copiar la información.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardContactoEmpleado(
    titulo: String,
    valor: String,
    icono: ImageVector,
    color: Color,
    maxLines: Int,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val valorVisible = valor.ifBlank { "No registrado" }

    Card(
        modifier = modifier
            .heightIn(min = 76.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = valorVisible,
                style = MaterialTheme.typography.bodySmall,
                color = if (valor.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun copiarInformacionEmpleado(
    context: android.content.Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    titulo: String,
    valor: String
) {
    val contenido = valor.trim()

    if (contenido.isBlank()) {
        Toast.makeText(
            context,
            "$titulo no registrado.",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    clipboardManager.setText(AnnotatedString(contenido))

    Toast.makeText(
        context,
        "$titulo copiado.",
        Toast.LENGTH_SHORT
    ).show()
}

@Composable
fun SeccionInformacionPersonalEmpleado(
    empleado: EmpleadoDetalleUI
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Información personal",
                icono = Icons.Default.Info,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            ItemInfoEmpleado(
                icono = Icons.Default.Phone,
                titulo = "Teléfono",
                valor = empleado.telefono
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            ItemInfoEmpleado(
                icono = Icons.Default.CalendarMonth,
                titulo = "Fecha de ingreso",
                valor = empleado.fechaIngreso
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            ItemInfoEmpleado(
                icono = Icons.Default.Email,
                titulo = "Correo",
                valor = empleado.correo
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Información laboral",
                icono = Icons.Default.Work,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            ItemInfoEmpleado(
                icono = Icons.Default.Work,
                titulo = "Puesto",
                valor = empleado.puesto
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            val tipoPagoEmpleado = obtenerTipoContratoEmpleado(empleado.porcentajeContrato)

            ItemInfoEmpleado(
                icono = Icons.Default.Badge,
                titulo = if (tipoPagoEmpleado.isBlank()) "Tipo de pago" else tipoPagoEmpleado,
                valor = obtenerTextoPagoEmpleado(empleado.porcentajeContrato)
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            ItemInfoEmpleado(
                icono = Icons.Default.Assignment,
                titulo = "Trabajo actual",
                valor = empleado.trabajoActual
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            /*
                        ItemInfoEmpleado(
                            icono = Icons.Default.AttachMoney,
                            titulo = "Pago total semana",
                            valor = empleado.pagoTotalSemana
                        )
            */
            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            ItemInfoEmpleado(
                icono = Icons.Default.CheckCircle,
                titulo = "Estado",
                valor = empleado.estado,
                valorColor = MaterialTheme.arcshiftColors.success
            )
        }
    }
}
fun obtenerTextoPagoEmpleado(contrato: String): String {
    val contratoLimpio = contrato.trim()

    if (contratoLimpio.isBlank()) {
        return "Sin definir"
    }

    return when {
        contratoLimpio.startsWith("% por trabajo") -> {
            val valor = contratoLimpio
                .substringAfter(":", "")
                .replace("$", "")
                .replace("%", "")
                .trim()

            if (valor.isBlank()) "Sin definir" else "$valor%"
        }

        contratoLimpio.startsWith("Pago por día") -> {
            val valor = contratoLimpio
                .substringAfter(":", "")
                .replace("$", "")
                .replace("%", "")
                .trim()

            if (valor.isBlank()) "Sin definir" else "$$valor"
        }

        contratoLimpio.startsWith("Pago por semana") -> {
            val valor = contratoLimpio
                .substringAfter(":", "")
                .replace("$", "")
                .replace("%", "")
                .trim()

            if (valor.isBlank()) "Sin definir" else "$$valor"
        }

        else -> contratoLimpio
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Notas del empleado",
                icono = Icons.Default.Message,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = empleado.notas,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ItemInfoEmpleado(
    icono: ImageVector,
    titulo: String,
    valor: String,
    valorColor: Color = MaterialTheme.colorScheme.onSurface
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
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(17.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Trabajos asignados",
                icono = Icons.Default.Assignment,
                color = MaterialTheme.colorScheme.primary
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
            containerColor = MaterialTheme.colorScheme.background
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
                    .background(MaterialTheme.colorScheme.outlineVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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

@Composable

fun obtenerColorEstadoTrabajo(estado: String): Color {
    return when (estado) {
        "En proceso" -> MaterialTheme.arcshiftColors.warning
        "Terminado" -> MaterialTheme.arcshiftColors.success
        "Pendiente" -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
fun SeccionAccionesRapidasEmpleado(
    onLlamarClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            TituloSeccionEmpleado(
                titulo = "Acciones rápidas",
                icono = Icons.Default.Bolt,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonAccionEmpleado(
                    texto = "Llamar",
                    icono = Icons.Default.Phone,
                    color = MaterialTheme.arcshiftColors.success,
                    onClick = onLlamarClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionEmpleado(
                    texto = "Editar",
                    icono = Icons.Default.Edit,
                    color = MaterialTheme.arcshiftColors.warning,
                    onClick = onEditarClick,
                    modifier = Modifier.weight(1f)
                )

                BotonAccionEmpleado(
                    texto = "Eliminar",
                    icono = Icons.Default.Delete,
                    color = MaterialTheme.colorScheme.error,
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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