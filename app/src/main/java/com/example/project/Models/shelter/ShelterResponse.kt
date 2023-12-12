package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class ShelterResponse(
    @SerializedName("TsunamiShelter")
    val tsunamiShelter: List<TsunamiShelter>
)