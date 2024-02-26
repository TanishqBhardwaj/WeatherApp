package com.example.apps10x.presentation.state

// Class for managing weather state
sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data object Empty : WeatherUiState()
    data class Success(val data: Int) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()

}