package com.example.arcshiftwelding.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arcshiftwelding.ui.Screen.ClientesScreen
import com.example.arcshiftwelding.ui.Screen.CotizacionesScreen
import com.example.arcshiftwelding.ui.Screen.DashboardScreen
import com.example.arcshiftwelding.ui.Screen.inventario.DetalleProductoScreen
import com.example.arcshiftwelding.ui.Screen.inventario.EditarProductoScreen
import com.example.arcshiftwelding.ui.Screen.EmpleadosScreen
import com.example.arcshiftwelding.ui.Screen.GastosScreen
import com.example.arcshiftwelding.ui.Screen.IngresosScreen
import com.example.arcshiftwelding.ui.Screen.LoginScreen
import com.example.arcshiftwelding.ui.Screen.inventario.NuevoProductoScreen
import com.example.arcshiftwelding.ui.Screen.ReportesScreen
import com.example.arcshiftwelding.ui.Screen.inventario.ReportarSalidaScreen
import com.example.arcshiftwelding.ui.screens.inventario.InventarioScreen
import com.example.arcshiftwelding.ui.screens.inventario.ReponerStockScreen


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

            val productoId = backStackEntry.arguments
                ?.getString("productoId")
                ?.toIntOrNull() ?: 0

            DetalleProductoScreen(
                navController = navController,
                productoId = productoId
            )
        }

        composable(
            route = AppRoutes.EDITAR_PRODUCTO
        ) { backStackEntry ->

            val productoId = backStackEntry.arguments
                ?.getString("productoId")
                ?.toIntOrNull() ?: 0

            EditarProductoScreen(
                navController = navController,
                productoId = productoId
            )
        }

        composable(
            route = AppRoutes.REPONER_STOCK
        ) { backStackEntry ->

            val productoId = backStackEntry.arguments
                ?.getString("productoId")
                ?.toIntOrNull() ?: 0

            ReponerStockScreen(
                navController = navController,
                productoId = productoId
            )
        }

        composable(
            route = AppRoutes.REPORTAR_SALIDA
        ) { backStackEntry ->

            val productoId = backStackEntry.arguments
                ?.getString("productoId")
                ?.toIntOrNull() ?: 0

            ReportarSalidaScreen(
                navController = navController,
                productoId = productoId
            )
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