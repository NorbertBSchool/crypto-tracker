package com.example.cryptotracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Search : Screen("search", "Search", Icons.Default.Search)
    data object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    data object Detail : Screen("detail/{pairAddress}/{chainId}", "Detail", Icons.Default.Home) {
        fun createRoute(pairAddress: String, chainId: String) = "detail/$pairAddress/$chainId"
    }
}

val bottomNavItems = listOf(Screen.Home, Screen.Search, Screen.Favorites)
