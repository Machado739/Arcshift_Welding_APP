package com.example.arcshiftwelding.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val expandedMas = remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val masSeleccionado = currentRoute in listOf(
        AppRoutes.INGRESOS,
        AppRoutes.COTIZACIONES,
        AppRoutes.EMPLEADOS,
        AppRoutes.REPORTES,
        AppRoutes.MAS
    )

    NavigationBar(
        modifier = Modifier.height(74.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == AppRoutes.DASHBOARD,
            onClick = {
                navController.navigateBottomBar(AppRoutes.DASHBOARD)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Dashboard",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Dashboard",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == AppRoutes.INVENTARIO,
            onClick = {
                navController.navigateBottomBar(AppRoutes.INVENTARIO)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = "Inventario",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Inventario",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == AppRoutes.GASTOS,
            onClick = {
                navController.navigateBottomBar(AppRoutes.GASTOS)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Gastos",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Gastos",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == AppRoutes.CLIENTES,
            onClick = {
                navController.navigateBottomBar(AppRoutes.CLIENTES)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Clientes",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Clientes",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )

        NavigationBarItem(
            selected = masSeleccionado,
            onClick = {
                expandedMas.value = true
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Más",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Más",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )
    }

    DropdownMenu(
        expanded = expandedMas.value,
        onDismissRequest = {
            expandedMas.value = false
        },
        offset = DpOffset(x = 300.dp, y = -280.dp)
    ) {
        DropdownMenuItem(
            text = {
                Text("Ingresos")
            },
            onClick = {
                navController.navigateBottomBar(AppRoutes.INGRESOS)
                expandedMas.value = false
            }
        )

        DropdownMenuItem(
            text = {
                Text("Cotizaciones")
            },
            onClick = {
                navController.navigateBottomBar(AppRoutes.COTIZACIONES)
                expandedMas.value = false
            }
        )

        DropdownMenuItem(
            text = {
                Text("Empleados")
            },
            onClick = {
                navController.navigateBottomBar(AppRoutes.EMPLEADOS)
                expandedMas.value = false
            }
        )

        DropdownMenuItem(
            text = {
                Text("Reportes")
            },
            onClick = {
                navController.navigateBottomBar(AppRoutes.REPORTES)
                expandedMas.value = false
            }
        )
    }
}

@Composable
fun bottomNavigationItemColors(): NavigationBarItemColors {
    return NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFF2563EB),
        selectedTextColor = Color(0xFF2563EB),
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B),
        indicatorColor = Color(0xFFE0ECFF)
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