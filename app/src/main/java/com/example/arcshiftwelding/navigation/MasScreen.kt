package com.example.arcshiftwelding.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

/**
 * Compatibilidad temporal con referencias anteriores.
 * La navegación principal ahora utiliza [ModulosScreen].
 */
@Deprecated("Usa ModulosScreen")
@Composable
fun MasScreen(navController: NavController) {
    ModulosScreen(navController = navController)
}
