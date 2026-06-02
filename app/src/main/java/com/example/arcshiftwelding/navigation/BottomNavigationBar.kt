package com.example.arcshiftwelding.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    val expandedMas = remember { mutableStateOf(false) }

    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashborad") }
        )
        NavigationBarItem(
            selected = false,
        onClick = { navController.navigate(Routes.INVENTARIO) },
            icon = { Icon(Icons.Default.Inventory, contentDescription = "Inventario") },
            label = { Text("Inventario") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.GASTOS) },
            icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Gastos") },
            label = { Text("Gastos") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.CLIENTES) },
            icon = { Icon(Icons.Default.People, contentDescription = "Clientes") },
            label = { Text("Clientes") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { expandedMas.value = true },
            icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Más") },
            label = { Text("Más") }
        )
    }

    DropdownMenu(
        expanded = expandedMas.value,
        onDismissRequest = { expandedMas.value = false},
        offset = DpOffset(x = 300.dp, y = -280.dp)
    ) {
        DropdownMenuItem(
            text = { Text("Ingresos") },
            onClick = {
                navController.navigate(Routes.INGRESOS)
                expandedMas.value = false
            }
        )
        DropdownMenuItem(
            text = { Text("Cotizaciones") },
            onClick = {
                navController.navigate(Routes.COTIZACIONES)
                expandedMas.value = false
            }
        )
        DropdownMenuItem(
            text = { Text("Empleados") },
            onClick = {
                navController.navigate(Routes.EMPLEADOS)
                expandedMas.value = false
            }
        )
        DropdownMenuItem(
            text = { Text("Reportes") },
            onClick = {
                navController.navigate(Routes.REPORTES)
                expandedMas.value = false
            }
        )
    }
}