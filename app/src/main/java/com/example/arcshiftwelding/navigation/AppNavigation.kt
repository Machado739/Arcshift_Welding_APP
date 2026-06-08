package com.example.arcshiftwelding.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.arcshiftwelding.ui.Screen.CotizacionesScreen
import com.example.arcshiftwelding.ui.Screen.DashboardScreen
import com.example.arcshiftwelding.ui.Screen.inventario.DetalleProductoScreen
import com.example.arcshiftwelding.ui.Screen.inventario.EditarProductoScreen
import com.example.arcshiftwelding.ui.Screen.EmpleadosScreen
import com.example.arcshiftwelding.ui.Screen.IngresosScreen
import com.example.arcshiftwelding.ui.Screen.LoginScreen
import com.example.arcshiftwelding.ui.Screen.inventario.NuevoProductoScreen
import com.example.arcshiftwelding.ui.Screen.ReportesScreen
import com.example.arcshiftwelding.ui.Screen.gastos.NuevoGastoScreen
import com.example.arcshiftwelding.ui.Screen.gastos.EliminarGastoScreen
import com.example.arcshiftwelding.ui.Screen.inventario.ReportarSalidaScreen
import com.example.arcshiftwelding.ui.Screen.inventario.EliminarProductoScreen
import com.example.arcshiftwelding.ui.Screen.inventario.InventarioScreen
import com.example.arcshiftwelding.ui.Screen.inventario.ReponerStockScreen
import com.example.arcshiftwelding.ui.Screen.inventario.SeleccionarProductoReponerScreen
import com.example.arcshiftwelding.ui.gastos.EditarGastoScreen
import com.example.arcshiftwelding.ui.gastos.GastosScreen
import com.example.arcshiftwelding.ui.Screen.gastos.DetalleGastoScreen
import com.example.arcshiftwelding.ui.clientes.ClientesScreen


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

        composable(
            route = AppRoutes.ELIMINAR_PRODUCTO
        ) { backStackEntry ->

            val productoId = backStackEntry.arguments
                ?.getString("productoId")
                ?.toIntOrNull() ?: 0

            EliminarProductoScreen(
                navController = navController,
                productoId = productoId
            )
        }
        composable(AppRoutes.SELECCIONAR_PRODUCTO_REPONER) {
            SeleccionarProductoReponerScreen(navController)
        }
        composable(AppRoutes.GASTOS) {
            GastosScreen(navController)
        }

        composable(AppRoutes.NUEVO_GASTO) {
            NuevoGastoScreen(
                onBack = { navController.popBackStack() },
                onGuardar = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoutes.ELIMINAR_GASTO
        ) { backStackEntry ->

            val gastoId = backStackEntry.arguments
                ?.getString("gastoId")
                ?.toIntOrNull() ?: 0

            EliminarGastoScreen(
                navController = navController,
                gastoId = gastoId
            )
        }

        composable(AppRoutes.DETALLE_GASTO) { backStackEntry ->
            val gastoId = backStackEntry.arguments
                ?.getString("gastoId")
                ?.toIntOrNull()

            DetalleGastoScreen(
                navController = navController,
                gastoId = gastoId ?: 0
            )
        }

        composable(AppRoutes.EDITAR_GASTO) { backStackEntry ->
            val gastoId = backStackEntry.arguments
                ?.getString("gastoId")
                ?.toIntOrNull()

            EditarGastoScreen(
                navController = navController,
                gastoId = gastoId ?: 0
            )
        }

        composable(AppRoutes.INGRESOS) {
            IngresosScreen(navController)
        }

        composable(AppRoutes.COTIZACIONES) {
            CotizacionesScreen(navController)
        }

        composable(AppRoutes.CLIENTES) {
            ClientesScreen(navController = navController)
        }

       /* composable(AppRoutes.NUEVO_CLIENTE) {
            NuevoClienteScreen(navController = navController)
        }*/

        /*composable(
            route = AppRoutes.DETALLE_CLIENTE,
            arguments = listOf(
                navArgument("clienteId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0

            DetalleClienteScreen(
                navController = navController,
                clienteId = clienteId
            )
        }*/

       /* composable(
            route = AppRoutes.EDITAR_CLIENTE,
            arguments = listOf(
                navArgument("clienteId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0

            EditarClienteScreen(
                navController = navController,
                clienteId = clienteId
            )
        }*/

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