package com.example.arcshiftwelding.navigation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private data class ModuloAplicacion(
    val titulo: String,
    val descripcion: String,
    val ruta: String,
    val icono: ImageVector,
    val color: Color,
    val colorFondo: Color
)

@Composable
fun ModulosScreen(
    navController: NavController
) {
    val operaciones = listOf(
        ModuloAplicacion(
            titulo = "Ingresos",
            descripcion = "Pagos, anticipos y cuentas por cobrar",
            ruta = AppRoutes.INGRESOS,
            icono = Icons.Default.Payments,
            color = Color(0xFF15803D),
            colorFondo = Color(0xFFDCFCE7)
        ),
        ModuloAplicacion(
            titulo = "Gastos",
            descripcion = "Egresos, proveedores y comprobantes",
            ruta = AppRoutes.GASTOS,
            icono = Icons.Default.AttachMoney,
            color = Color(0xFFDC2626),
            colorFondo = Color(0xFFFEE2E2)
        ),
        ModuloAplicacion(
            titulo = "Cotizaciones",
            descripcion = "Propuestas, conceptos y seguimiento",
            ruta = AppRoutes.COTIZACIONES,
            icono = Icons.Default.Description,
            color = Color(0xFF2563EB),
            colorFondo = Color(0xFFDBEAFE)
        )
    )

    val administracion = listOf(
        ModuloAplicacion(
            titulo = "Empleados",
            descripcion = "Personal, pagos y asignaciones",
            ruta = AppRoutes.EMPLEADOS,
            icono = Icons.Default.Work,
            color = Color(0xFF7C3AED),
            colorFondo = Color(0xFFEDE9FE)
        ),
        ModuloAplicacion(
            titulo = "Reportes",
            descripcion = "Resumen financiero y operativo",
            ruta = AppRoutes.REPORTES,
            icono = Icons.Default.Assessment,
            color = Color(0xFFD97706),
            colorFondo = Color(0xFFFEF3C7)
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 18.dp,
            end = 16.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            EncabezadoModulos()
        }

        item {
            TituloGrupoModulos(
                titulo = "Operaciones",
                descripcion = "Control diario del taller"
            )
        }

        items(operaciones.size) { indice ->
            TarjetaModuloNavegacion(
                modulo = operaciones[indice],
                onClick = {
                    navController.navigate(operaciones[indice].ruta) {
                        launchSingleTop = true
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(2.dp))
            TituloGrupoModulos(
                titulo = "Administración",
                descripcion = "Personal e información general"
            )
        }

        items(administracion.size) { indice ->
            TarjetaModuloNavegacion(
                modulo = administracion[indice],
                onClick = {
                    navController.navigate(administracion[indice].ruta) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun EncabezadoModulos() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Módulos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Accede a las herramientas administrativas de Arcshift Welding.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun TituloGrupoModulos(
    titulo: String,
    descripcion: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 1.dp)
    ) {
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = descripcion,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun TarjetaModuloNavegacion(
    modulo: ModuloAplicacion,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(14.dp),
                color = modulo.colorFondo
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = modulo.icono,
                        contentDescription = null,
                        tint = modulo.color,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = modulo.titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = modulo.descripcion,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF64748B),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Abrir ${modulo.titulo}",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
