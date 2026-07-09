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
import com.example.arcshiftwelding.ui.Screen.cotizaciones.CotizacionesViewModel
import com.example.arcshiftwelding.ui.Screen.cotizaciones.CotizacionesViewModelFactory
import com.example.arcshiftwelding.ui.Screen.empleados.EmpleadosViewModel
import com.example.arcshiftwelding.ui.Screen.empleados.EmpleadosViewModelFactory
import com.example.arcshiftwelding.ui.Screen.ingresos.IngresosViewModel
import com.example.arcshiftwelding.ui.Screen.ingresos.IngresosViewModelFactory
import com.example.arcshiftwelding.ui.Screen.proyectos.ProyectosScreen
import com.example.arcshiftwelding.ui.Screen.proyectos.NuevoProyectoScreen
import androidx.navigation.navArgument
import com.example.arcshiftwelding.ui.Screen.proyectos.AgregarCostoProyectoScreen
import com.example.arcshiftwelding.ui.Screen.proyectos.AsignarEmpleadoProyectoScreen
import com.example.arcshiftwelding.ui.Screen.proyectos.DetalleProyectoScreen
import com.example.arcshiftwelding.ui.Screen.proyectos.NuevoProyectoScreen
import com.example.arcshiftwelding.ui.Screen.proyectos.ProyectosScreen
import com.example.arcshiftwelding.ui.Screen.proyectos.ProyectosViewModel
import com.example.arcshiftwelding.ui.Screen.proyectos.ProyectosViewModelFactory
import com.example.arcshiftwelding.ui.Screen.proyectos.EditarProyectoScreen
import com.example.arcshiftwelding.ui.Screen.proyectos.RegistrarMaterialProyectoScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = remember {
        ArcshiftWeldingDatabase.getDatabase(context)
    }
    val clientesViewModel: ClientesViewModel = viewModel(
        factory = ClientesViewModelFactory(
            clienteDao = database.clienteDao(),
            ingresoDao = database.ingresoDao(),
            proyectoDao = database.proyectoDao()
        )
    )

    val gastosViewModel: GastosViewModel = viewModel(
        factory = GastosViewModelFactory(
            gastoDao = database.gastoDao(),
            clienteDao = database.clienteDao(),
            cotizacionDao = database.cotizacionDao(),
            proyectoDao = database.proyectoDao()
        )
    )

    val ingresosViewModel: IngresosViewModel = viewModel(
        factory = IngresosViewModelFactory(
            ingresoDao = database.ingresoDao(),
            clienteDao = database.clienteDao(),
            cotizacionDao = database.cotizacionDao(),
            proyectoDao = database.proyectoDao(),
            pagoProgramadoDao = database.pagoProgramadoDao()
        )
    )

    val cotizacionesViewModel: CotizacionesViewModel = viewModel(
        factory = CotizacionesViewModelFactory(
            cotizacionDao = database.cotizacionDao(),
            detalleCotizacionDao = database.detalleCotizacionDao(),
            clienteDao = database.clienteDao()
        )
    )

    val empleadosViewModel: EmpleadosViewModel = viewModel(
        factory = EmpleadosViewModelFactory(
            empleadoDao = database.empleadoDao()
        )
    )

    val proyectosViewModel: ProyectosViewModel = viewModel(
        factory = ProyectosViewModelFactory(database)
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
        AppRoutes.REPORTES,
        AppRoutes.PROYECTOS
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
                GastosScreen(
                    navController = navController,
                    viewModel = gastosViewModel
                )
            }

            composable(AppRoutes.NUEVO_GASTO) {
                NuevoGastoScreen(
                    navController = navController,
                    viewModel = gastosViewModel
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

                val gastoId = backStackEntry.arguments
                    ?.getString("gastoId")
                    ?.toIntOrNull() ?: 0

                DetalleGastoScreen(
                    navController = navController,
                    gastoId = gastoId,
                    viewModel = gastosViewModel
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
                IngresosScreen(
                    navController = navController,
                    viewModel = ingresosViewModel
                )
            }

            composable(AppRoutes.NUEVO_INGRESO) {
                NuevoIngresoScreen(
                    navController = navController,
                    viewModel = ingresosViewModel
                )
            }

            composable(AppRoutes.DETALLE_INGRESO) { backStackEntry ->
                val ingresoId = backStackEntry.arguments?.getString("ingresoId")?.toIntOrNull() ?: 0

                DetalleIngresoScreen(
                    navController = navController,
                    ingresoId = ingresoId,
                    viewModel = ingresosViewModel
                )
            }

            composable(AppRoutes.EDITAR_INGRESO) { backStackEntry ->
                val ingresoId = backStackEntry.arguments?.getString("ingresoId")?.toIntOrNull() ?: 0

                EditarIngresoScreen(
                    navController = navController,
                    ingresoId = ingresoId,
                    viewModel = ingresosViewModel
                )
            }

            composable(AppRoutes.ELIMINAR_INGRESO) { backStackEntry ->
                val ingresoId = backStackEntry.arguments?.getString("ingresoId")?.toIntOrNull() ?: 0

                EliminarIngresoScreen(
                    navController = navController,
                    ingresoId = ingresoId,
                    viewModel = ingresosViewModel
                )
            }

///                 COTIZACIONES
///                 COTIZACIONES
///                 COTIZACIONES

            composable(AppRoutes.COTIZACIONES) {
                CotizacionesScreen(
                    navController = navController,
                    viewModel = cotizacionesViewModel
                )
            }

            composable(AppRoutes.NUEVA_COTIZACION) {
                NuevaCotizacionScreen(
                    navController = navController,
                    viewModel = cotizacionesViewModel
                )
            }

            composable(
                route = AppRoutes.DETALLE_COTIZACION,
                arguments = listOf(
                    navArgument("cotizacionId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val cotizacionId = backStackEntry.arguments?.getInt("cotizacionId") ?: 0

                DetalleCotizacionScreen(
                    navController = navController,
                    cotizacionId = cotizacionId,
                    viewModel = cotizacionesViewModel
                )
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
                    cotizacionId = cotizacionId,
                    viewModel = cotizacionesViewModel
                )
            }

            composable("${AppRoutes.ELIMINAR_COTIZACION}/{cotizacionId}") { backStackEntry ->
                val cotizacionId = backStackEntry.arguments
                    ?.getString("cotizacionId")
                    ?.toIntOrNull() ?: 0

                EliminarCotizacionScreen(
                    navController = navController,
                    cotizacionId = cotizacionId,
                    viewModel = cotizacionesViewModel
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

            // EMPLEADOS

            composable(route = AppRoutes.EMPLEADOS) {
                EmpleadosScreen(
                    navController = navController,
                    viewModel = empleadosViewModel
                )
            }

            composable(route = AppRoutes.NUEVO_EMPLEADO) {
                NuevoEmpleadoScreen(
                    navController = navController,
                    viewModel = empleadosViewModel
                )
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
                    empleadoId = empleadoId,
                    viewModel = empleadosViewModel
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
                    empleadoId = empleadoId,
                    viewModel = empleadosViewModel
                )
            }

            composable(
                route = AppRoutes.ELIMINAR_EMPLEADO,
                arguments = listOf(
                    navArgument("empleadoId") {
                        type = NavType.IntType
                    }
                )
            ) {
                val empleadoId = it.arguments?.getInt("empleadoId") ?: 0

                EliminarEmpleadoScreen(
                    navController = navController,
                    empleadoId = empleadoId,
                    viewModel = empleadosViewModel
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

            /// PROYECTOS
            composable(AppRoutes.PROYECTOS) {
                ProyectosScreen(
                    navController = navController,
                    viewModel = proyectosViewModel
                )
            }

            composable(AppRoutes.NUEVO_PROYECTO) {
                NuevoProyectoScreen(
                    navController = navController,
                    viewModel = proyectosViewModel
                )
            }

            composable(
                route = AppRoutes.DETALLE_PROYECTO,
                arguments = listOf(
                    navArgument("proyectoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val proyectoId = backStackEntry.arguments?.getInt("proyectoId") ?: 0

                DetalleProyectoScreen(
                    navController = navController,
                    viewModel = proyectosViewModel,
                    proyectoId = proyectoId
                )
            }

            composable(
                route = AppRoutes.EDITAR_PROYECTO,
                arguments = listOf(
                    navArgument("proyectoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val proyectoId = backStackEntry.arguments?.getInt("proyectoId") ?: 0

                EditarProyectoScreen(
                    navController = navController,
                    viewModel = proyectosViewModel,
                    proyectoId = proyectoId
                )
            }

            composable(
                route = AppRoutes.NUEVO_PROYECTO_DESDE_COTIZACION,
                arguments = listOf(
                    navArgument("cotizacionId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val cotizacionId = backStackEntry.arguments
                    ?.getInt("cotizacionId")

                NuevoProyectoScreen(
                    navController = navController,
                    viewModel = proyectosViewModel,
                    cotizacionId = cotizacionId
                )
            }

            ///         projects

            composable(
                route = AppRoutes.ASIGNAR_EMPLEADO_PROYECTO,
                arguments = listOf(
                    navArgument("proyectoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val proyectoId = backStackEntry.arguments?.getInt("proyectoId") ?: 0

                AsignarEmpleadoProyectoScreen(
                    proyectoId = proyectoId,
                    navController = navController,
                    proyectosViewModel = proyectosViewModel
                )
            }

            composable(
                route = AppRoutes.REGISTRAR_MATERIAL_PROYECTO,
                arguments = listOf(
                    navArgument("proyectoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val proyectoId = backStackEntry.arguments?.getInt("proyectoId") ?: 0

                RegistrarMaterialProyectoScreen(
                    proyectoId = proyectoId,
                    navController = navController,
                    proyectosViewModel = proyectosViewModel
                )
            }

            composable(
                route = AppRoutes.AGREGAR_COSTO_PROYECTO,
                arguments = listOf(
                    navArgument("proyectoId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val proyectoId = backStackEntry.arguments?.getInt("proyectoId") ?: 0

                AgregarCostoProyectoScreen(
                    proyectoId = proyectoId,
                    navController = navController,
                    proyectosViewModel = proyectosViewModel
                )
            }


            composable(
                route = AppRoutes.NUEVO_GASTO_CON_PROYECTO,
                arguments = listOf(
                    navArgument("proyectoId") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->

                val proyectoId = backStackEntry.arguments?.getInt("proyectoId") ?: 0

                NuevoGastoScreen(
                    navController = navController,
                    viewModel = gastosViewModel,
                    proyectoIdRelacionado = proyectoId.takeIf { it != 0 }
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