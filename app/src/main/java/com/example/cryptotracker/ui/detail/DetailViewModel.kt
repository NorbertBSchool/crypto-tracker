package com.example.cryptotracker.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.repository.CryptoRepository
import com.example.cryptotracker.domain.model.CryptoCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val isLoading: Boolean = false,
    val crypto: CryptoCurrency? = null,
    val isFavorite: Boolean = false,
    val isInPortfolio: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CryptoRepository
) : ViewModel() {

    private val pairAddress: String = savedStateHandle["pairAddress"] ?: ""
    private val chainId: String = savedStateHandle["chainId"] ?: ""

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        loadDetails()
        observeFavorite()
        observePortfolio()
    }

    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(30_000)
                refreshDetails()
            }
        }
    }

    private fun refreshDetails() {
        viewModelScope.launch {
            val result = repository.getPairData(chainId, pairAddress)
            result.fold(
                onSuccess = { currency ->
                    _uiState.value = _uiState.value.copy(
                        crypto = currency,
                        error = null
                    )
                },
                onFailure = { }
            )
        }
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getPairData(chainId, pairAddress)
            result.fold(
                onSuccess = { currency ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        crypto = currency,
                        error = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load details"
                    )
                }
            )
        }
    }

    private fun observeFavorite() {
        viewModelScope.launch {
            repository.isFavorite(pairAddress).collect { isFav ->
                _uiState.value = _uiState.value.copy(isFavorite = isFav)
            }
        }
    }

    private fun observePortfolio() {
        viewModelScope.launch {
            repository.isHolding(pairAddress).collect { isHolding ->
                _uiState.value = _uiState.value.copy(isInPortfolio = isHolding)
            }
        }
    }

    fun toggleFavorite() {
        val currency = _uiState.value.crypto ?: return
        viewModelScope.launch {
            repository.toggleFavorite(currency)
        }
    }

    fun addToPortfolio(buyPriceUsd: Double, quantity: Double) {
        val currency = _uiState.value.crypto ?: return
        viewModelScope.launch {
            repository.addHolding(currency, buyPriceUsd, quantity)
        }
    }

    fun removeFromPortfolio() {
        viewModelScope.launch {
            repository.removeHolding(pairAddress)
        }
    }
}
