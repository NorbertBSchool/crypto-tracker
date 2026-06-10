package com.example.cryptotracker.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.repository.CryptoRepository
import com.example.cryptotracker.domain.model.PortfolioItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PortfolioUiState(
    val isLoading: Boolean = false,
    val items: List<PortfolioItem> = emptyList(),
    val totalValue: Double = 0.0,
    val totalCost: Double = 0.0,
    val totalPnlUsd: Double = 0.0,
    val totalPnlPercent: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repository: CryptoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PortfolioUiState())
    val uiState: StateFlow<PortfolioUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null
    private var observeDbJob: Job? = null

    init {
        observeHoldingsDb()
        loadPortfolio()
    }

    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(30_000)
                loadPortfolio()
            }
        }
    }

    private fun observeHoldingsDb() {
        observeDbJob?.cancel()
        observeDbJob = viewModelScope.launch {
            repository.getHoldingsFlow()
                .distinctUntilChanged { old, new -> old.size == new.size && old.map { it.pairAddress } == new.map { it.pairAddress } }
                .collect {
                    loadPortfolio()
                }
        }
    }

    fun loadPortfolio() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val items = repository.getPortfolioItems()
                val totalValue = items.sumOf { it.currentValue }
                val totalCost = items.sumOf { it.totalCost }
                val totalPnl = totalValue - totalCost
                val totalPnlPct = if (totalCost > 0) (totalPnl / totalCost) * 100.0 else 0.0

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = items,
                    totalValue = totalValue,
                    totalCost = totalCost,
                    totalPnlUsd = totalPnl,
                    totalPnlPercent = totalPnlPct,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load portfolio"
                )
            }
        }
    }

    fun removeFromPortfolio(pairAddress: String) {
        viewModelScope.launch {
            repository.removeHolding(pairAddress)
        }
    }
}
