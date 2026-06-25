package com.example.apprecetas.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    INGREDIENTES("ingredientes", "Ingredientes", Icons.Filled.Kitchen, Icons.Outlined.Kitchen),
    RECETAS("recetas", "Recetas", Icons.Filled.RestaurantMenu, Icons.Outlined.RestaurantMenu)
}