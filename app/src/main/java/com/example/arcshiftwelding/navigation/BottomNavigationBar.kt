package com.example.arcshiftwelding.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

private data class ElementoNavegacionInferior(
    val titulo: String,
    val ruta: String,
    val icono: ImageVector,
    val rutasRelacionadas: Set<String> = setOf(ruta)
)

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val elementos = listOf(
        ElementoNavegacionInferior(
            titulo = "Inicio",
            ruta = AppRoutes.DASHBOARD,
            icono = Icons.Default.Dashboard
        ),
        ElementoNavegacionInferior(
            titulo = "Inventario",
            ruta = AppRoutes.INVENTARIO,
            icono = Icons.Default.Inventory
        ),
        ElementoNavegacionInferior(
            titulo = "Proyectos",
            ruta = AppRoutes.PROYECTOS,
            icono = Icons.Default.Work
        ),
        ElementoNavegacionInferior(
            titulo = "Clientes",
            ruta = AppRoutes.CLIENTES,
            icono = Icons.Default.People
        ),
        ElementoNavegacionInferior(
            titulo = "Módulos",
            ruta = AppRoutes.MODULOS,
            icono = Icons.Default.Apps,
            rutasRelacionadas = setOf(
                AppRoutes.MODULOS,
                AppRoutes.INGRESOS,
                AppRoutes.GASTOS,
                AppRoutes.COTIZACIONES,
                AppRoutes.EMPLEADOS,
                AppRoutes.REPORTES
            )
        )
    )

    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.height(72.dp),
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            elementos.forEach { elemento ->
                val seleccionado = rutaActual in elemento.rutasRelacionadas

                NavigationBarItem(
                    selected = seleccionado,
                    onClick = {
                        navController.navigateBottomBar(elemento.ruta)
                    },
                    icon = {
                        Icon(
                            imageVector = elemento.icono,
                            contentDescription = elemento.titulo,
                            modifier = Modifier.size(21.dp)
                        )
                    },
                    label = {
                        Text(
                            text = elemento.titulo,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    },
                    alwaysShowLabel = true,
                    colors = bottomNavigationItemColors()
                )
            }
        }
    }
}

@Composable
fun bottomNavigationItemColors(): NavigationBarItemColors {
    return NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFF2563EB),
        selectedTextColor = Color(0xFF1D4ED8),
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B),
        indicatorColor = Color(0xFFDBEAFE)
    )
}

fun NavController.navigateBottomBar(route: String) {
    if (currentDestination?.route == route) return

    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
