package com.example.apps10x.presentation.state

import com.example.apps10x.presentation.model.ForecastData

// Class for managing forecast state
sealed class ForecastUiState {
    data object Loading : ForecastUiState()
    data object Empty : ForecastUiState()
    data class Success(val data: List<ForecastData>) : ForecastUiState()
    data class Error(val message: String) : ForecastUiState()
}