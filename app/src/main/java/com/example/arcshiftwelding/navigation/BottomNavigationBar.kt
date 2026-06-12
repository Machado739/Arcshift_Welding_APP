package com.example.arcshiftwelding.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    var mostrarMenuMas by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val masSeleccionado = currentRoute in listOf(
        AppRoutes.INGRESOS,
        AppRoutes.COTIZACIONES,
        AppRoutes.EMPLEADOS,
        AppRoutes.REPORTES,
        AppRoutes.MAS
    )

    NavigationBar(
        modifier = Modifier.height(74.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == AppRoutes.DASHBOARD,
            onClick = {
                navController.navigateBottomBar(AppRoutes.DASHBOARD)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Dashboard",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Dashboard",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == AppRoutes.INVENTARIO,
            onClick = {
                navController.navigateBottomBar(AppRoutes.INVENTARIO)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = "Inventario",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Inventario",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == AppRoutes.GASTOS,
            onClick = {
                navController.navigateBottomBar(AppRoutes.GASTOS)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Gastos",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Gastos",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == AppRoutes.CLIENTES,
            onClick = {
                navController.navigateBottomBar(AppRoutes.CLIENTES)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Clientes",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Clientes",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )

        NavigationBarItem(
            selected = masSeleccionado || mostrarMenuMas,
            onClick = {
                mostrarMenuMas = !mostrarMenuMas
            },
            icon = {
                Icon(
                    imageVector = if (mostrarMenuMas) Icons.Default.KeyboardArrowUp else Icons.Default.MoreHoriz,
                    contentDescription = "Más",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Más",
                    fontSize = 11.sp
                )
            },
            colors = bottomNavigationItemColors()
        )
    }

    if (mostrarMenuMas) {
        MenuMasFlotante(
            onDismiss = { mostrarMenuMas = false },
            onIngresosClick = {
                mostrarMenuMas = false
                navController.navigateBottomBar(AppRoutes.INGRESOS)
            },
            onCotizacionesClick = {
                mostrarMenuMas = false
                navController.navigateBottomBar(AppRoutes.COTIZACIONES)
            },
            onEmpleadosClick = {
                mostrarMenuMas = false
                navController.navigateBottomBar(AppRoutes.EMPLEADOS)
            },
            onReportesClick = {
                mostrarMenuMas = false
                navController.navigateBottomBar(AppRoutes.REPORTES)
            }
        )
    }
}

@Composable
fun MenuMasFlotante(
    onDismiss: () -> Unit,
    onIngresosClick: () -> Unit,
    onCotizacionesClick: () -> Unit,
    onEmpleadosClick: () -> Unit,
    onReportesClick: () -> Unit
) {
    Popup(
        alignment = Alignment.BottomEnd,
        offset = IntOffset(x = -10, y = -250),
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(end = 4.dp)
        ) {
            BotonMenuMasVertical(
                texto = "Ingresos",
                icono = Icons.Default.AttachMoney,
                onClick = onIngresosClick
            )

            BotonMenuMasVertical(
                texto = "Cotizaciones",
                icono = Icons.Default.Description,
                onClick = onCotizacionesClick
            )

            BotonMenuMasVertical(
                texto = "Empleados",
                icono = Icons.Default.Work,
                onClick = onEmpleadosClick
            )

            BotonMenuMasVertical(
                texto = "Reportes",
                icono = Icons.Default.Assessment,
                onClick = onReportesClick
            )
        }
    }
}
@Composable
fun BotonMenuMasVertical(
    texto: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFE9EFFB),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier.width(72.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = texto,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun bottomNavigationItemColors(): NavigationBarItemColors {
    return NavigationBarItemDefaults.colors(
        selectedIconColor = Color(0xFF2563EB),
        selectedTextColor = Color(0xFF2563EB),
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF64748B),
        indicatorColor = Color(0xFFE0ECFF)
    )
}

fun NavController.navigateBottomBar(route: String) {
    if (currentDestination?.route == route) return

    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}

fun Modifier.clickableSinRipple(
    onClick: () -> Unit
): Modifier {
    return this.clickable {
        onClick()
    }
}