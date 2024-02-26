package com.example.apps10x.domain.repository

import com.example.apps10x.BuildConfig
import com.example.apps10x.data.api.WeatherAPI
import com.example.apps10x.data.entity.WeatherEntity
import com.example.apps10x.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// Repository class for fetching data from API
class WeatherRepository @Inject constructor(private val weatherAPI: WeatherAPI) {

    suspend fun getWeatherData(): WeatherEntity? {
        // passed APP_ID from BuildConfig which is generated from local.properties
        return weatherAPI.getWeatherData(Constants.BENGALURU, BuildConfig.WEATHER_APP_ID)
    }

    fun getForecastData(): Flow<List<WeatherEntity?>?> = flow {
        // passed APP_ID from BuildConfig which is generated from local.properties
        val forecastData =
            weatherAPI.getForecastData(Constants.BENGALURU, BuildConfig.WEATHER_APP_ID)
        forecastData?.let {
            emit(it.list)
        }
    }
}