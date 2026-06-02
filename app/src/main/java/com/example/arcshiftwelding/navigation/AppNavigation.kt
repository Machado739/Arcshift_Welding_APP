package com.example.arcshiftwelding.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arcshiftwelding.ui.Screen.ClientesScreen
import com.example.arcshiftwelding.ui.Screen.CotizacionesScreen
import com.example.arcshiftwelding.ui.Screen.DashboardScreen
import com.example.arcshiftwelding.ui.Screen.EmpleadosScreen
import com.example.arcshiftwelding.ui.Screen.GastosScreen
import com.example.arcshiftwelding.ui.Screen.IngresosScreen
import com.example.arcshiftwelding.ui.Screen.InventarioScreen
import com.example.arcshiftwelding.ui.Screen.LoginScreen
import com.example.arcshiftwelding.ui.Screen.ReportesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(onLoginClick = { navController.navigate(Routes.DASHBOARD) })
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(navController)
        }

        composable(Routes.INVENTARIO) {
            InventarioScreen(navController)
        }

        composable(Routes.GASTOS) {
            GastosScreen(navController)
        }

        composable(Routes.INGRESOS) {
            IngresosScreen(navController)
        }

        composable(Routes.COTIZACIONES) {
            CotizacionesScreen(navController)
        }

        composable(Routes.CLIENTES) {
            ClientesScreen(navController)
        }

        composable(Routes.EMPLEADOS) {
            EmpleadosScreen(navController)
        }

        composable(Routes.REPORTES) {
            ReportesScreen(navController)
        }

        composable(Routes.MAS) {
            MasScreen(navController)
        }
    }
}