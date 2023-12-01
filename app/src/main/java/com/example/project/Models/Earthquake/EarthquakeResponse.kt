package com.example.project.Models.Earthquake


import com.google.gson.annotations.SerializedName

data class EarthquakeResponse(
    @SerializedName("response")
    val response: Response
)