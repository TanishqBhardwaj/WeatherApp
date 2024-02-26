package com.example.apps10x.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apps10x.data.entity.WeatherEntity
import com.example.apps10x.domain.repository.WeatherRepository
import com.example.apps10x.presentation.model.ForecastData
import com.example.apps10x.presentation.state.ForecastUiState
import com.example.apps10x.presentation.state.WeatherUiState
import com.example.apps10x.utils.Constants
import com.example.apps10x.utils.Messages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
    ViewModel() {

    // used live data in case of single object
    private val _weatherData = MutableLiveData<WeatherUiState>()
    val weatherData: LiveData<WeatherUiState> = _weatherData

    // used flow in case of list of item
    private val _forecastData: MutableStateFlow<ForecastUiState> =
        MutableStateFlow(ForecastUiState.Loading)
    val forecastData get() = _forecastData.asStateFlow()

    init {
        fetchWeatherData()
        fetchForecastData()
    }

    private fun fetchWeatherData() {
        _weatherData.value = WeatherUiState.Loading
        viewModelScope.launch {
            try {
                val fetchedWeatherData = weatherRepository.getWeatherData()
                if (fetchedWeatherData?.main?.temp != null) {
                    val temperature = fetchedWeatherData.main.temp.div(10).toInt()
                    _weatherData.value = WeatherUiState.Success(temperature)
                } else {
                    _weatherData.value = WeatherUiState.Empty
                }
            } catch (e: Exception) {
                _weatherData.value = WeatherUiState.Error(Messages.SOMETHING_WENT_WRONG)
            }
        }
    }

    private fun fetchForecastData() {
        _forecastData.update { ForecastUiState.Loading }
        viewModelScope.launch {
            try {
                weatherRepository.getForecastData().collect { list ->
                    if (!list.isNullOrEmpty()) {
                        _forecastData.update { ForecastUiState.Success(calculateForecastData(list)) }
                    } else {
                        _forecastData.update { ForecastUiState.Empty }
                    }
                }
            } catch (e: Exception) {
                _forecastData.update { ForecastUiState.Error(Messages.SOMETHING_WENT_WRONG) }
            }
        }
    }

    // Function to calculate forecast data (containing Day of week, Avg. time)
    private fun calculateForecastData(weatherList: List<WeatherEntity?>): List<ForecastData> {
        val currentDate = LocalDate.now()
        val forecastList = mutableListOf<ForecastData>()
        var dayCounter = 1L
        var sameDayCounter = 0
        var sumOfTemp = 0.0
        weatherList.map { weatherEntity ->
            weatherEntity?.let {
                val fetchedDateString = weatherEntity.dateText?.split(" ")?.get(0)
                val formattedDate = LocalDate.parse(
                    fetchedDateString,
                    DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)
                )
                // To consider after today's date & max 4 days forecast
                if (formattedDate.isAfter(currentDate) && dayCounter <= Constants.MAX_FORECAST_DAYS) {
                    val day = currentDate.plusDays(dayCounter)
                    if (fetchedDateString == day.toString()) {
                        sumOfTemp += weatherEntity.main?.temp ?: 0.0
                        ++sameDayCounter
                    } else {
                        // Insert in list after converting Day into Pascal case, and avg of forecast temperature
                        forecastList.add(
                            ForecastData(
                                day.dayOfWeek.toString().lowercase().capitalize(),
                                sumOfTemp.div(sameDayCounter * 10).toInt()
                            )
                        )
                        sameDayCounter = 1
                        sumOfTemp = weatherEntity.main?.temp ?: 0.0
                        dayCounter++
                    }
                }
            }
        }
        // To consider if any data left after above iteration
        if (forecastList.size <= Constants.MAX_FORECAST_DAYS && sumOfTemp > 0.0) {
            val day = currentDate.plusDays(dayCounter)
            forecastList.add(
                ForecastData(
                    day.dayOfWeek.toString().lowercase().capitalize(),
                    sumOfTemp.div(sameDayCounter * 10).toInt()
                )
            )
        }
        return forecastList
    }
}