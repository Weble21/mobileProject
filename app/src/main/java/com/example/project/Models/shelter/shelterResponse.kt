package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class shelterResponse(
    @SerializedName("TsunamiShelter")
    val tsunamiShelter: List<TsunamiShelter>
)