package com.example.cryptotracker

import androidx.lifecycle.SavedStateHandle
import com.example.cryptotracker.data.repository.CryptoRepository
import com.example.cryptotracker.domain.model.CryptoCurrency
import com.example.cryptotracker.ui.detail.DetailViewModel
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
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: CryptoRepository
    private val testCurrency = CryptoCurrency(
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private suspend fun createViewModel(): DetailViewModel {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "pairAddress" to "0x123",
                "chainId" to "ethereum"
            )
        )
        whenever(repository.getPairData("ethereum", "0x123")).thenReturn(Result.success(testCurrency))
        whenever(repository.isFavorite("0x123")).thenReturn(flowOf(false))
        whenever(repository.isHolding("0x123")).thenReturn(flowOf(false))
        return DetailViewModel(savedStateHandle, repository)
    }

    @Test
    fun `loadDetails success updates state with data`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("USDC", viewModel.uiState.value.crypto?.baseTokenSymbol)
        assertEquals("USD Coin", viewModel.uiState.value.crypto?.baseTokenName)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `loadDetails failure shows error`() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "pairAddress" to "0x999",
                "chainId" to "ethereum"
            )
        )
        whenever(repository.getPairData("ethereum", "0x999")).thenReturn(
            Result.failure(Exception("Pair not found"))
        )
        whenever(repository.isFavorite("0x999")).thenReturn(flowOf(false))
        whenever(repository.isHolding("0x999")).thenReturn(flowOf(false))

        val viewModel = DetailViewModel(savedStateHandle, repository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Pair not found", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.crypto)
    }

    @Test
    fun `observeFavorite updates isFavorite state`() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "pairAddress" to "0x123",
                "chainId" to "ethereum"
            )
        )
        whenever(repository.getPairData("ethereum", "0x123")).thenReturn(Result.success(testCurrency))
        whenever(repository.isFavorite("0x123")).thenReturn(flowOf(true))
        whenever(repository.isHolding("0x123")).thenReturn(flowOf(false))

        val viewModel = DetailViewModel(savedStateHandle, repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isFavorite)
    }

    @Test
    fun `toggleFavorite calls repository`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        verify(repository).toggleFavorite(testCurrency)
    }

    @Test
    fun `addToPortfolio calls repository addHolding`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.addToPortfolio(buyPriceUsd = 1.5, quantity = 50.0)
        advanceUntilIdle()

        verify(repository).addHolding(testCurrency, 1.5, 50.0)
    }

    @Test
    fun `addToPortfolio does nothing when crypto is null`() = runTest {
        val savedStateHandle = SavedStateHandle(
            mapOf("pairAddress" to "0x999", "chainId" to "ethereum")
        )
        whenever(repository.getPairData("ethereum", "0x999")).thenReturn(
            Result.failure(Exception("Not found"))
        )
        whenever(repository.isFavorite("0x999")).thenReturn(flowOf(false))
        whenever(repository.isHolding("0x999")).thenReturn(flowOf(false))

        val viewModel = DetailViewModel(savedStateHandle, repository)
        advanceUntilIdle()

        viewModel.addToPortfolio(buyPriceUsd = 1.0, quantity = 10.0)
        advanceUntilIdle()

        verify(repository, never()).addHolding(any(), any(), any())
    }

    @Test
    fun `removeFromPortfolio calls repository removeHolding`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.removeFromPortfolio()
        advanceUntilIdle()

        verify(repository).removeHolding("0x123")
    }
}
