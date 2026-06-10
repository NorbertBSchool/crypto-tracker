package com.example.cryptotracker

import com.example.cryptotracker.data.repository.CryptoRepository
import com.example.cryptotracker.domain.model.CryptoCurrency
import com.example.cryptotracker.ui.favorites.FavoritesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CryptoRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        whenever(repository.getFavorites()).thenReturn(flowOf(emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFavorites success updates state with data`() = runTest {
        val mockData = listOf(
            CryptoCurrency(
                pairAddress = "0x123",
                chainId = "ethereum",
                dexId = "uniswap",
                baseTokenSymbol = "USDC",
                baseTokenName = "USD Coin",
                baseTokenAddress = "0xabc",
                quoteTokenSymbol = "WETH",
                priceUsd = "1.0",
                priceNative = "0.0005",
                priceChange24h = 0.5,
                priceChange1h = 0.1,
                volume24h = 1000000.0,
                liquidityUsd = 5000000.0,
                fdv = 1000000.0,
                marketCap = null,
                url = null,
                imageUrl = null
            )
        )
        whenever(repository.getFavoritePairs()).thenReturn(mockData)

        val viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.favorites.size)
        assertEquals("USDC", viewModel.uiState.value.favorites[0].baseTokenSymbol)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadFavorites empty result shows empty list`() = runTest {
        whenever(repository.getFavoritePairs()).thenReturn(emptyList())

        val viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.favorites.isEmpty())
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadFavorites exception shows error`() = runTest {
        whenever(repository.getFavoritePairs()).thenThrow(RuntimeException("Database error"))

        val viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Database error", viewModel.uiState.value.error)
    }
}
