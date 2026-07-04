package com.example.arcshiftwelding.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MasScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            Button(onClick = { navController.navigate(AppRoutes.INGRESOS) }) {
                Text("Ingresos")
            }

            Button(onClick = { navController.navigate(AppRoutes.COTIZACIONES) }) {
                Text("Cotizaciones")
            }

            Button(onClick = { navController.navigate(AppRoutes.EMPLEADOS) }) {
                Text("Empleados")
            }

            Button(onClick = { navController.navigate(AppRoutes.REPORTES) }) {
                Text("Reportes")
            }
            Button(onClick = {
                navController.navigate(AppRoutes.PROYECTOS)
            }) { Text("Proyectos") }
        }
    }
}