package com.example.arcshiftwelding.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arcshiftwelding.ui.clientes.ClientesScreen
import com.example.arcshiftwelding.ui.cotizaciones.CotizacionesScreen
import com.example.arcshiftwelding.ui.dashboard.DashboardScreen
import com.example.arcshiftwelding.ui.empleados.EmpleadosScreen
import com.example.arcshiftwelding.ui.gastos.GastosScreen
import com.example.arcshiftwelding.ui.ingresos.IngresosScreen
import com.example.arcshiftwelding.ui.inventario.InventarioScreen
import com.example.arcshiftwelding.ui.login.LoginScreen
import com.example.arcshiftwelding.ui.reportes.ReportesScreen

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