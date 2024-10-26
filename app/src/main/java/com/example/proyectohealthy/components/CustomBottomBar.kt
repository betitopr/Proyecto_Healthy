package com.example.proyectohealthy.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(val route: String, val title: String, val icon: ImageVector)

@Composable
fun CustomBottomBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", "Plan", Icons.Filled.Home),
        BottomNavItem("alimentos", "Alimento", Icons.Filled.Search),
        BottomNavItem("recetas", "Recetas", Icons.Filled.AccountBox),
        BottomNavItem("teams", "Teams", Icons.Filled.Person),
        BottomNavItem("progreso", "Progreso", Icons.Filled.DateRange)
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}