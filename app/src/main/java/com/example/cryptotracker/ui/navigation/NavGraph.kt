package com.example.cryptotracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cryptotracker.ui.detail.DetailScreen
import com.example.cryptotracker.ui.favorites.FavoritesScreen
import com.example.cryptotracker.ui.home.HomeScreen
import com.example.cryptotracker.ui.portfolio.PortfolioScreen
import com.example.cryptotracker.ui.search.SearchScreen

@Composable
fun CryptoTrackerNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onCryptoClick = { currency ->
                        navController.navigate(
                            Screen.Detail.createRoute(currency.pairAddress, currency.chainId)
                        )
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onCryptoClick = { currency ->
                        navController.navigate(
                            Screen.Detail.createRoute(currency.pairAddress, currency.chainId)
                        )
                    }
                )
            }

            composable(Screen.Portfolio.route) {
                PortfolioScreen()
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onCryptoClick = { currency ->
                        navController.navigate(
                            Screen.Detail.createRoute(currency.pairAddress, currency.chainId)
                        )
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("pairAddress") { type = NavType.StringType },
                    navArgument("chainId") { type = NavType.StringType }
                )
            ) {
                DetailScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
