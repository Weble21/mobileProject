package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class ShelterResponse(
    @SerializedName("header")
    val header: ShelterHeader,
    @SerializedName("totalCount")
    val totalCount: Int,
    @SerializedName("body")
    val body: List<Row_shelter>
)

data class ShelterHeader(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String
)
