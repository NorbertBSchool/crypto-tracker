package com.example.cryptotracker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.repository.CryptoRepository
import com.example.cryptotracker.domain.model.CryptoCurrency
import com.example.cryptotracker.domain.model.TokenBoostItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val cryptoData: List<CryptoCurrency> = emptyList(),
    val boostedTokens: List<TokenBoostItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CryptoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigateToDetail = MutableSharedFlow<CryptoCurrency>()
    val navigateToDetail: SharedFlow<CryptoCurrency> = _navigateToDetail.asSharedFlow()

    private var autoRefreshJob: Job? = null

    init {
        loadCryptoData()
    }

    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (true) {
                delay(30_000)
                loadCryptoData()
            }
        }
    }

    fun loadCryptoData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val (data, boosted) = coroutineScope {
                    val trackedJob = async { repository.getTrackedPairs() }
                    val boostedJob = async { repository.getTopBoostedTokens() }
                    Pair(trackedJob.await(), boostedJob.await())
                }

                if (data.isEmpty() && boosted.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No data available"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        cryptoData = data,
                        boostedTokens = boosted,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load crypto data"
                )
            }
        }
    }

    fun onBoostedTokenClick(chainId: String, tokenAddress: String) {
        viewModelScope.launch {
            val currency = repository.resolveBoostedToken(chainId, tokenAddress)
            if (currency != null) {
                _navigateToDetail.emit(currency)
            }
        }
    }
}
