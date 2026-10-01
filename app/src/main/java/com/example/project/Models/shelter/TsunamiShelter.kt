package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class TsunamiShelter(
    @SerializedName("row")
    val rowShelter: List<Row_shelter>
)