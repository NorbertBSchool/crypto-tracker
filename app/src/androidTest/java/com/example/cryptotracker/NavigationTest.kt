package com.example.cryptotracker

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun appLaunches() {
        composeRule.setContent {
            CryptoTrackerTheme(darkTheme = true) {
                com.example.cryptotracker.ui.navigation.CryptoTrackerNavGraph()
            }
        }
        composeRule.onNodeWithText("CryptoTracker").assertIsDisplayed()
    }

    @Test
    fun bottomNav_showsAllTabs() {
        composeRule.setContent {
            CryptoTrackerTheme(darkTheme = true) {
                com.example.cryptotracker.ui.navigation.CryptoTrackerNavGraph()
            }
        }
        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithText("Search").assertIsDisplayed()
        composeRule.onNodeWithText("Favorites").assertIsDisplayed()
    }

    @Test
    fun navigateToSearch_showsSearchBar() {
        composeRule.setContent {
            CryptoTrackerTheme(darkTheme = true) {
                com.example.cryptotracker.ui.navigation.CryptoTrackerNavGraph()
            }
        }
        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNode(hasText("Search", substring = true)).assertIsDisplayed()
    }

    @Test
    fun navigateToFavorites_showsTitle() {
        composeRule.setContent {
            CryptoTrackerTheme(darkTheme = true) {
                com.example.cryptotracker.ui.navigation.CryptoTrackerNavGraph()
            }
        }
        composeRule.onNodeWithText("Favorites").performClick()
        composeRule.onNode(hasText("No favorites yet", substring = true)).assertIsDisplayed()
    }

    @Test
    fun navigateToSearch_thenHome_showsCryptoTracker() {
        composeRule.setContent {
            CryptoTrackerTheme(darkTheme = true) {
                com.example.cryptotracker.ui.navigation.CryptoTrackerNavGraph()
            }
        }
        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithText("Home").performClick()
        composeRule.onNodeWithText("CryptoTracker").assertIsDisplayed()
    }
}

