package com.example.arcshiftwelding.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arcshiftwelding.ui.login.LoginScreen
import com.example.arcshiftwelding.ui.dashboard.DashboardScreen
import com.example.arcshiftwelding.ui.inventario.InventarioScreen
import com.example.arcshiftwelding.ui.gastos.GastosScreen
import com.example.arcshiftwelding.ui.ingresos.IngresosScreen
import com.example.arcshiftwelding.ui.cotizaciones.CotizacionesScreen
import com.example.arcshiftwelding.ui.clientes.ClientesScreen
import com.example.arcshiftwelding.ui.empleados.EmpleadosScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(AppRoutes.DASHBOARD) {
                        popUpTo(AppRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoutes.DASHBOARD) {
            DashboardScreen(navController)
        }

        composable(AppRoutes.INVENTARIO) {
            InventarioScreen()
        }

        composable(AppRoutes.GASTOS) {
            GastosScreen()
        }

        composable(AppRoutes.INGRESOS) {
            IngresosScreen()
        }

        composable(AppRoutes.COTIZACIONES) {
            CotizacionesScreen()
        }

        composable(AppRoutes.CLIENTES) {
            ClientesScreen()
        }

        composable(AppRoutes.EMPLEADOS) {
            EmpleadosScreen()
        }
    }
}