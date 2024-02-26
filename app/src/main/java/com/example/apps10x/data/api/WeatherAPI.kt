package com.example.apps10x.data.api

import com.example.apps10x.data.entity.ForecastEntity
import com.example.apps10x.data.entity.WeatherEntity
import com.example.apps10x.utils.Constants
import retrofit2.http.GET
import retrofit2.http.Query

// Interface denoting API endpoints
interface WeatherAPI {

    @GET(Constants.WEATHER_BASE_URL + Constants.WEATHER)
    suspend fun getWeatherData(
        @Query(Constants.QUERY) city: String,
        @Query(Constants.APP_ID) appId: String
    ): WeatherEntity?

    @GET(Constants.WEATHER_BASE_URL + Constants.FORECAST)
    suspend fun getForecastData(
        @Query(Constants.QUERY) city: String,
        @Query(Constants.APP_ID) appId: String
    ): ForecastEntity?
}