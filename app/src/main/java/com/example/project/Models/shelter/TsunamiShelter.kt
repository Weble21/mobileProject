package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class TsunamiShelter(
    @SerializedName("head")
    val head: List<Head>,
    @SerializedName("row")
    val row: List<Row>
)