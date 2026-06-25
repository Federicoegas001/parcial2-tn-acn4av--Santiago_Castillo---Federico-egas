package com.example.apprecetas.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Timer
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.apprecetas.TimersActivity
import com.example.apprecetas.ui.home.HomeScreen
import com.example.apprecetas.ui.placeholder.PlaceholderScreen
import com.example.apprecetas.ui.theme.PurpleAccent
import com.example.apprecetas.ui.theme.SurfaceDark
import com.example.apprecetas.ui.theme.TextSecondary

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestination.HOME.route) { HomeScreen() }
            composable(AppDestination.INGREDIENTES.route) { PlaceholderScreen("Ingredientes") }
            composable(AppDestination.RECETAS.route) { PlaceholderScreen("Recetas") }
        }
    }
}

@Composable
private fun AppBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val context = LocalContext.current

    NavigationBar(containerColor = SurfaceDark) {
        AppDestination.entries.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurpleAccent,
                    selectedTextColor = PurpleAccent,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = SurfaceDark
                )
            )
        }

        NavigationBarItem(
            selected = false,
            onClick = {
                val intent = Intent(context, TimersActivity::class.java)
                context.startActivity(intent)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = "Timers"
                )
            },
            label = { Text("Timers") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = SurfaceDark
            )
        )
    }
}