package com.example.arcshiftwelding.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arcshiftwelding.ui.Screen.AgregarStockScreen
import com.example.arcshiftwelding.ui.Screen.ClientesScreen
import com.example.arcshiftwelding.ui.Screen.CotizacionesScreen
import com.example.arcshiftwelding.ui.Screen.DashboardScreen
import com.example.arcshiftwelding.ui.Screen.DetalleProductoScreen
import com.example.arcshiftwelding.ui.Screen.EditarProductoScreen
import com.example.arcshiftwelding.ui.Screen.EmpleadosScreen
import com.example.arcshiftwelding.ui.Screen.GastosScreen
import com.example.arcshiftwelding.ui.Screen.IngresosScreen
import com.example.arcshiftwelding.ui.Screen.LoginScreen
import com.example.arcshiftwelding.ui.Screen.NuevoProductoScreen
import com.example.arcshiftwelding.ui.Screen.ReportarSalidaScreen
import com.example.arcshiftwelding.ui.Screen.ReportesScreen
import com.example.arcshiftwelding.ui.screens.inventario.InventarioScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(onLoginClick = { navController.navigate(AppRoutes.DASHBOARD) })
        }

        composable(AppRoutes.DASHBOARD) {
            DashboardScreen(navController)
        }

        composable(AppRoutes.INVENTARIO) {
            InventarioScreen(
                navController = navController,
                onNuevoProducto = {
                    navController.navigate(AppRoutes.NUEVO_PRODUCTO)
                },
                onDetalleProducto = { producto ->
                    navController.navigate(AppRoutes.detalleProducto(producto.id))
                }
            )
        }

        composable(AppRoutes.NUEVO_PRODUCTO) {
            NuevoProductoScreen(navController)
        }

        composable(
            route = AppRoutes.DETALLE_PRODUCTO
        ) { backStackEntry ->

            val productoId =
                backStackEntry.arguments
                    ?.getString("productoId")
                    ?.toIntOrNull()

            DetalleProductoScreen(
                navController = navController,
               // productoId = productoId ?: 0
            )
        }

        composable(AppRoutes.EDITAR_PRODUCTO) {
            EditarProductoScreen(navController)
        }

        composable(AppRoutes.AGREGAR_STOCK) {
            AgregarStockScreen(navController)
        }

        composable(AppRoutes.REPORTAR_SALIDA) {
            ReportarSalidaScreen(navController)
        }

        composable(AppRoutes.GASTOS) {
            GastosScreen(navController)
        }

        composable(AppRoutes.INGRESOS) {
            IngresosScreen(navController)
        }

        composable(AppRoutes.COTIZACIONES) {
            CotizacionesScreen(navController)
        }

        composable(AppRoutes.CLIENTES) {
            ClientesScreen(navController)
        }

        composable(AppRoutes.EMPLEADOS) {
            EmpleadosScreen(navController)
        }

        composable(AppRoutes.REPORTES) {
            ReportesScreen(navController)
        }

        composable(AppRoutes.MAS) {
            MasScreen(navController)
        }
    }
}