package com.example.arcshiftwelding.navigation

import androidx.compose.foundation.background
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.arcshiftwelding.ui.Screen.DashboardScreen
import com.example.arcshiftwelding.ui.Screen.DetalleReporteScreen
import com.example.arcshiftwelding.ui.Screen.cotizaciones.CotizacionesScreen
import com.example.arcshiftwelding.ui.Screen.inventario.DetalleProductoScreen
import com.example.arcshiftwelding.ui.Screen.inventario.EditarProductoScreen
import com.example.arcshiftwelding.ui.Screen.empleados.EmpleadosScreen
import com.example.arcshiftwelding.ui.Screen.ingresos.IngresosScreen
import com.example.arcshiftwelding.ui.Screen.LoginScreen
import com.example.arcshiftwelding.ui.Screen.ReportesScreen
import com.example.arcshiftwelding.ui.Screen.inventario.NuevoProductoScreen
import com.example.arcshiftwelding.ui.Screen.clientes.DetalleClienteScreen
import com.example.arcshiftwelding.ui.Screen.clientes.EditarClienteScreen
import com.example.arcshiftwelding.ui.Screen.clientes.EliminarClienteScreen
import com.example.arcshiftwelding.ui.Screen.clientes.NuevoClienteScreen
import com.example.arcshiftwelding.ui.Screen.cotizaciones.DetalleCotizacionScreen
import com.example.arcshiftwelding.ui.Screen.cotizaciones.EditarCotizacionScreen
import com.example.arcshiftwelding.ui.Screen.cotizaciones.EliminarCotizacionScreen
import com.example.arcshiftwelding.ui.Screen.cotizaciones.NuevaCotizacionScreen
import com.example.arcshiftwelding.ui.Screen.empleados.DetalleEmpleadoScreen
import com.example.arcshiftwelding.ui.Screen.empleados.EditarEmpleadoScreen
import com.example.arcshiftwelding.ui.Screen.empleados.EliminarEmpleadoScreen
import com.example.arcshiftwelding.ui.Screen.empleados.NuevoEmpleadoScreen
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
import com.example.arcshiftwelding.ui.Screen.ingresos.DetalleIngresoScreen
import com.example.arcshiftwelding.ui.Screen.ingresos.EditarIngresoScreen
import com.example.arcshiftwelding.ui.Screen.ingresos.EliminarIngresoScreen
import com.example.arcshiftwelding.ui.Screen.ingresos.NuevoIngresoScreen
import com.example.arcshiftwelding.ui.Screen.clientes.ClientesScreen
import com.example.arcshiftwelding.ui.gastos.GastosViewModel
import com.example.arcshiftwelding.ui.gastos.GastosViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arcshiftwelding.data.local.database.ArcshiftWeldingDatabase
import com.example.arcshiftwelding.ui.Screen.clientes.ClientesViewModel
import com.example.arcshiftwelding.ui.Screen.clientes.ClientesViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = remember {
        ArcshiftWeldingDatabase.getDatabase(context)
    }
    val clientesViewModel: ClientesViewModel = viewModel(
        factory = ClientesViewModelFactory(
            clienteDao = database.clienteDao()
        )
    )

    val gastosViewModel: GastosViewModel = viewModel(
        factory = GastosViewModelFactory(database.gastoDao())
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mostrarBottomBar = currentRoute in listOf(
        AppRoutes.DASHBOARD,
        AppRoutes.INVENTARIO,
        AppRoutes.GASTOS,
        AppRoutes.CLIENTES,
        AppRoutes.INGRESOS,
        AppRoutes.COTIZACIONES,
        AppRoutes.EMPLEADOS,
        AppRoutes.REPORTES
    )

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            if (mostrarBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.LOGIN,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
        ) {
            composable(AppRoutes.LOGIN) {
                LoginScreen(
                    onLoginClick = {
                        navController.navigate(AppRoutes.DASHBOARD) {
                            popUpTo(AppRoutes.LOGIN) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppRoutes.DASHBOARD) {
                DashboardScreen(navController = navController)
            }


///                     INVENTARIO
///                     INVENTARIO
///                     INVENTARIO

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
                route = AppRoutes.DETALLE_PRODUCTO,
                arguments = listOf(
                    navArgument("productoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0

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

///                     GASTOS
///                     GASTOS
///                     GASTOS

            composable(AppRoutes.GASTOS) {
                val context = LocalContext.current

                val database = ArcshiftWeldingDatabase.getDatabase(context)

                val viewModel: GastosViewModel = viewModel(
                    factory = GastosViewModelFactory(
                        gastoDao = database.gastoDao()
                    )
                )

                GastosScreen(
                    navController = navController,
                    viewModel = viewModel
                )


            }

            composable(AppRoutes.NUEVO_GASTO) {
                val context = LocalContext.current

                val database = ArcshiftWeldingDatabase.getDatabase(context)

                val viewModel: GastosViewModel = viewModel(
                    factory = GastosViewModelFactory(
                        gastoDao = database.gastoDao()
                    )
                )

                NuevoGastoScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(
                route = AppRoutes.ELIMINAR_GASTO,
                arguments = listOf(
                    navArgument("gastoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val gastoId = backStackEntry.arguments?.getInt("gastoId") ?: return@composable

                EliminarGastoScreen(
                    navController = navController,
                    gastoId = gastoId,
                    viewModel = gastosViewModel
                )
            }

            composable(
                route = AppRoutes.DETALLE_GASTO
            ) { backStackEntry ->

                val context = LocalContext.current
                val database = ArcshiftWeldingDatabase.getDatabase(context)

                val viewModel: GastosViewModel = viewModel(
                    factory = GastosViewModelFactory(
                        gastoDao = database.gastoDao()
                    )
                )

                val gastoId = backStackEntry.arguments
                    ?.getString("gastoId")
                    ?.toIntOrNull() ?: 0

                DetalleGastoScreen(
                    navController = navController,
                    gastoId = gastoId,
                    viewModel = viewModel
                )
            }

            composable(
                route = AppRoutes.EDITAR_GASTO,
                arguments = listOf(
                    navArgument("gastoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val gastoId = backStackEntry.arguments?.getInt("gastoId") ?: return@composable

                EditarGastoScreen(
                    navController = navController,
                    gastoId = gastoId,
                    viewModel = gastosViewModel
                )
            }
///                     INGRESOS
///                     INGRESOS
///                     INGRESOS

            composable(AppRoutes.INGRESOS) {
                IngresosScreen(navController = navController)
            }

            composable(AppRoutes.NUEVO_INGRESO) {
                NuevoIngresoScreen(navController = navController)
            }

            composable(AppRoutes.DETALLE_INGRESO){backStackEntry ->
                val ingresoId = backStackEntry.arguments
                    ?.getString("ingresoId")
                    ?.toIntOrNull()
                DetalleIngresoScreen(
                    navController = navController,
                    ingresoId = ingresoId ?:0
                )
            }

            composable(
                route = AppRoutes.EDITAR_INGRESO,
                arguments = listOf(
                    navArgument("ingresoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val ingresoId = backStackEntry.arguments?.getInt("ingresoId") ?: 0

                EditarIngresoScreen(
                    navController = navController,
                    ingresoId = ingresoId
                )
            }

            composable(
                route = AppRoutes.ELIMINAR_INGRESO,
                arguments = listOf(
                    navArgument("ingresoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val ingresoId = backStackEntry.arguments?.getInt("ingresoId") ?: 0

                EliminarIngresoScreen(
                    navController = navController,
                    ingresoId = ingresoId
                )
            }

///                 COTIZACIONES
///                 COTIZACIONES
///                 COTIZACIONES

            composable(AppRoutes.COTIZACIONES) {
                CotizacionesScreen(navController = navController)
            }

            composable(AppRoutes.NUEVA_COTIZACION) {
               NuevaCotizacionScreen(navController = navController)
            }

            composable(
                route = AppRoutes.DETALLE_COTIZACION,
                arguments = listOf(
                    navArgument("cotizacionId") {
                        type = NavType.IntType
                    }
                )
            ) {
                DetalleCotizacionScreen(navController = navController)
            }

            composable(
                route = AppRoutes.EDITAR_COTIZACION,
                arguments = listOf(
                    navArgument("cotizacionId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val cotizacionId = backStackEntry.arguments?.getInt("cotizacionId") ?: 0

                EditarCotizacionScreen(
                    navController = navController,
                    cotizacionId = cotizacionId
                )
            }

            composable(
                route = AppRoutes.ELIMINAR_COTIZACION,
                arguments = listOf(
                    navArgument("cotizacionId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val cotizacionId = backStackEntry.arguments?.getInt("cotizacionId") ?: 0

                EliminarCotizacionScreen(
                    navController = navController,
                    cotizacionId = cotizacionId
                )
            }
///                 CLIENTES
///                 CLIENTES
///                 CLIENTES


            composable(AppRoutes.CLIENTES) {
                ClientesScreen(
                    navController = navController,
                    viewModel = clientesViewModel
                )


            }

            composable(AppRoutes.NUEVO_CLIENTE) {
                NuevoClienteScreen(
                    navController = navController,
                    viewModel = clientesViewModel
                )
            }

            composable(
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
                    clienteId = clienteId,
                    viewModel = clientesViewModel
                )
            }

            composable(
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
                    clienteId = clienteId,
                    viewModel = clientesViewModel
                )
            }

            composable(
                route = AppRoutes.ELIMINAR_CLIENTE,
                arguments = listOf(
                    navArgument("clienteId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val clienteId = backStackEntry.arguments?.getInt("clienteId") ?: 0

                EliminarClienteScreen(
                    navController = navController,
                    clienteId = clienteId,
                    viewModel = clientesViewModel
                )
            }


///                     EMPLEADOS
///                     EMPLEADOS
///                     EMPLEADOS

            composable(AppRoutes.EMPLEADOS) {
                EmpleadosScreen(navController)
            }

            composable(AppRoutes.NUEVO_EMPLEADO ) {
                NuevoEmpleadoScreen(navController = navController)
            }

            composable(
                route = AppRoutes.DETALLE_EMPLEADO,
                arguments = listOf(
                    navArgument("empleadoId") {
                        type = NavType.IntType
                    }
                )
            ) {
                val empleadoId = it.arguments?.getInt("empleadoId") ?: 0

                DetalleEmpleadoScreen(
                    navController = navController,
                    empleadoId = empleadoId
                )
            }

            composable(
                route = AppRoutes.EDITAR_EMPLEADO,
                arguments = listOf(
                    navArgument("empleadoId") {
                        type = NavType.IntType
                    }
                )
            ) {
                val empleadoId = it.arguments?.getInt("empleadoId") ?: 0

                EditarEmpleadoScreen(
                    navController = navController,
                    empleadoId = empleadoId
                )
            }

            composable(
                route = AppRoutes.ELIMINAR_EMPLEADO,
                arguments = listOf(navArgument("empleadoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val empleadoId = backStackEntry.arguments?.getInt("empleadoId") ?: 0

                EliminarEmpleadoScreen(
                    navController = navController,
                    empleadoId = empleadoId
                )
            }
///                     REPORTES
///                     REPORTES
///                     REPORTES

            composable(AppRoutes.REPORTES) {
                ReportesScreen(
                    navController = navController
                )
            }

            composable(
                route = AppRoutes.DETALLE_REPORTE,
                arguments = listOf(
                    navArgument("tipoReporte") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val tipoReporte = backStackEntry.arguments?.getString("tipoReporte") ?: ""

                DetalleReporteScreen(
                    navController = navController,
                    tipoReporte = tipoReporte
                )
            }

///                     MAS
///                     MAS
///                     MAS

            composable(AppRoutes.MAS) {
                MasScreen(navController)
            }
        }
    }
}