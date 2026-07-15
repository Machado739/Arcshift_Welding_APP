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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.MaterialTheme

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
                AppRoutes.REPORTES,
                AppRoutes.CONFIGURACION
            )
        )
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.height(72.dp),
            containerColor = MaterialTheme.colorScheme.surface,
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
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer
    )
}

fun NavController.navigateBottomBar(route: String) {
    if (route == AppRoutes.MODULOS) {
        navigateToModulesRoot()
        return
    }

    if (currentDestination?.route == route) return

    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Regresa a la raíz de Módulos en lugar de restaurar la última pantalla abierta
 * dentro del grupo. Esto evita que al tocar Módulos desde Ingresos, Gastos,
 * Cotizaciones, Empleados, Reportes o Configuración se restaure esa misma pantalla.
 */
fun NavController.navigateToModulesRoot() {
    if (currentDestination?.route == AppRoutes.MODULOS) return

    val moduloYaEstabaEnPila = popBackStack(
        route = AppRoutes.MODULOS,
        inclusive = false
    )

    if (!moduloYaEstabaEnPila) {
        navigate(AppRoutes.MODULOS) {
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }
}
