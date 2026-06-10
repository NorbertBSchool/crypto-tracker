package com.example.cryptotracker

import com.example.cryptotracker.data.repository.CryptoRepository
import com.example.cryptotracker.domain.model.PortfolioItem
import com.example.cryptotracker.ui.portfolio.PortfolioViewModel
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
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CryptoRepository

    private val testItem = PortfolioItem(
        pairAddress = "0x123",
        chainId = "ethereum",
        dexId = "uniswap",
        tokenSymbol = "USDC",
        tokenName = "USD Coin",
        quoteTokenSymbol = "WETH",
        buyPriceUsd = 1.0,
        quantity = 100.0,
        currentPriceUsd = 1.05,
        imageUrl = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        whenever(repository.getHoldingsFlow()).thenReturn(flowOf(emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPortfolio success updates state with items and PNL`() = runTest {
        whenever(repository.getPortfolioItems()).thenReturn(listOf(testItem))

        val viewModel = PortfolioViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, viewModel.uiState.value.items.size)
        assertEquals(105.0, viewModel.uiState.value.totalValue, 0.01)
        assertEquals(100.0, viewModel.uiState.value.totalCost, 0.01)
        assertEquals(5.0, viewModel.uiState.value.totalPnlUsd, 0.01)
        assertEquals(5.0, viewModel.uiState.value.totalPnlPercent, 0.01)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadPortfolio empty result shows zero totals`() = runTest {
        whenever(repository.getPortfolioItems()).thenReturn(emptyList())

        val viewModel = PortfolioViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.items.isEmpty())
        assertEquals(0.0, viewModel.uiState.value.totalValue, 0.01)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadPortfolio exception shows error`() = runTest {
        whenever(repository.getPortfolioItems()).thenThrow(RuntimeException("API error"))

        val viewModel = PortfolioViewModel(repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("API error", viewModel.uiState.value.error)
    }

    @Test
    fun `loadPortfolio zero cost items shows zero PNL percent`() = runTest {
        val zeroCostItem = PortfolioItem(
            pairAddress = "0x999",
            chainId = "ethereum",
            dexId = "uniswap",
            tokenSymbol = "FREE",
            tokenName = "Free Token",
            quoteTokenSymbol = "WETH",
            buyPriceUsd = 0.0,
            quantity = 100.0,
            currentPriceUsd = 0.5,
            imageUrl = null
        )
        whenever(repository.getPortfolioItems()).thenReturn(listOf(zeroCostItem))

        val viewModel = PortfolioViewModel(repository)
        advanceUntilIdle()

        assertEquals(50.0, viewModel.uiState.value.totalValue, 0.01)
        assertEquals(0.0, viewModel.uiState.value.totalCost, 0.01)
        assertEquals(50.0, viewModel.uiState.value.totalPnlUsd, 0.01)
        assertEquals(0.0, viewModel.uiState.value.totalPnlPercent, 0.01)
    }

    @Test
    fun `removeFromPortfolio calls repository and reloads`() = runTest {
        whenever(repository.getPortfolioItems()).thenReturn(emptyList())

        val viewModel = PortfolioViewModel(repository)
        advanceUntilIdle()

        viewModel.removeFromPortfolio("0x123")
        advanceUntilIdle()

        verify(repository).removeHolding("0x123")
    }
}
