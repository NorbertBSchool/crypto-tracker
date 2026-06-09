package com.example.cryptotracker

import com.example.cryptotracker.data.repository.CryptoRepository
import com.example.cryptotracker.domain.model.CryptoCurrency
import com.example.cryptotracker.ui.search.SearchViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

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

    private fun createViewModel() = SearchViewModel(repository)

    @Test
    fun `initial state has empty query and results`() = runTest {
        val viewModel = createViewModel()
        assertEquals("", viewModel.uiState.value.query)
        assertTrue(viewModel.uiState.value.results.isEmpty())
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onQueryChange updates query`() = runTest {
        whenever(repository.searchTokens("bitcoin")).thenReturn(Result.success(emptyList()))
        val viewModel = createViewModel()
        viewModel.onQueryChange("bitcoin")
        advanceUntilIdle()
        assertEquals("bitcoin", viewModel.uiState.value.query)
    }

    @Test
    fun `blank query clears results`() = runTest {
        val viewModel = createViewModel()
        viewModel.onQueryChange("")
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.results.isEmpty())
    }

    @Test
    fun `search success updates results`() = runTest {
        val mockResults = listOf(
            CryptoCurrency(
                pairAddress = "0x456",
                chainId = "ethereum",
                dexId = "uniswap",
                baseTokenSymbol = "BTC",
                baseTokenName = "Bitcoin",
                baseTokenAddress = "0xdef",
                quoteTokenSymbol = "USDT",
                priceUsd = "65000.0",
                priceNative = "32.5",
                priceChange24h = 2.5,
                priceChange1h = 0.5,
                volume24h = 50000000.0,
                liquidityUsd = 100000000.0,
                fdv = 1300000000.0,
                marketCap = 1300000000.0,
                url = null,
                imageUrl = null
            )
        )
        whenever(repository.searchTokens("bitcoin")).thenReturn(Result.success(mockResults))

        val viewModel = createViewModel()
        viewModel.onQueryChange("bitcoin")
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.results.size)
        assertEquals("Bitcoin", viewModel.uiState.value.results[0].baseTokenName)
    }

    @Test
    fun `search failure updates error`() = runTest {
        whenever(repository.searchTokens("invalid")).thenReturn(
            Result.failure(Exception("Search failed"))
        )

        val viewModel = createViewModel()
        viewModel.onQueryChange("invalid")
        advanceUntilIdle()

        assertEquals("Search failed", viewModel.uiState.value.error)
    }
}
