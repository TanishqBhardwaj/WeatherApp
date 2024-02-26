package com.example.apps10x.data.entity

import com.google.gson.annotations.SerializedName

data class WeatherEntity(
    val main: MainEntity?,
    @SerializedName("dt_txt")
    val dateText: String?
)