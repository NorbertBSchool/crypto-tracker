package com.example.cryptotracker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cryptotracker.ui.navigation.Screen
import com.example.cryptotracker.ui.navigation.bottomNavItems
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Composable
    private fun TestNavGraph() {
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
                composable(Screen.Home.route) { Text("CryptoTracker") }
                composable(Screen.Search.route) { Text("Search results placeholder") }
                composable(Screen.Favorites.route) { Text("No favorites yet") }
                composable(Screen.Portfolio.route) { Text("Portfolio placeholder") }
            }
        }
    }

    @Test
    fun appLaunches() {
        composeRule.setContent { CryptoTrackerTheme(darkTheme = true) { TestNavGraph() } }
        composeRule.onNodeWithText("CryptoTracker").assertIsDisplayed()
    }

    @Test
    fun bottomNav_showsAllTabs() {
        composeRule.setContent { CryptoTrackerTheme(darkTheme = true) { TestNavGraph() } }
        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithText("Search").assertIsDisplayed()
        composeRule.onNodeWithText("Favorites").assertIsDisplayed()
    }

    @Test
    fun navigateToSearch_showsSearchBar() {
        composeRule.setContent { CryptoTrackerTheme(darkTheme = true) { TestNavGraph() } }
        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNode(hasText("Search results placeholder", substring = true)).assertIsDisplayed()
    }

    @Test
    fun navigateToFavorites_showsTitle() {
        composeRule.setContent { CryptoTrackerTheme(darkTheme = true) { TestNavGraph() } }
        composeRule.onNodeWithText("Favorites").performClick()
        composeRule.onNode(hasText("No favorites yet", substring = true)).assertIsDisplayed()
    }

    @Test
    fun navigateToSearch_thenHome_showsCryptoTracker() {
        composeRule.setContent { CryptoTrackerTheme(darkTheme = true) { TestNavGraph() } }
        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithText("Home").performClick()
        composeRule.onNodeWithText("CryptoTracker").assertIsDisplayed()
    }
}
