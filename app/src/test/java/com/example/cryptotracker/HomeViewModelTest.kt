package com.example.cryptotracker

import com.example.cryptotracker.data.repository.CryptoRepository
import com.example.cryptotracker.domain.model.CryptoCurrency
import com.example.cryptotracker.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CryptoRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCryptoData success updates state with data`() = runTest {
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
        whenever(repository.getTrackedPairs()).thenReturn(mockData)

        val viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceTimeBy(1)

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.cryptoData.size)
        assertEquals("USDC", viewModel.uiState.value.cryptoData[0].baseTokenSymbol)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadCryptoData empty result shows error`() = runTest {
        whenever(repository.getTrackedPairs()).thenReturn(emptyList())

        val viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceTimeBy(1)

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("No data available", viewModel.uiState.value.error)
    }

    @Test
    fun `loadCryptoData exception shows error`() = runTest {
        whenever(repository.getTrackedPairs()).thenThrow(RuntimeException("Network error"))

        val viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceTimeBy(1)

        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals("Network error", viewModel.uiState.value.error)
    }
}
