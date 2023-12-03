package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class Head(
    @SerializedName("numOfRows")
    val numOfRows: String,
    @SerializedName("pageNo")
    val pageNo: String,
    @SerializedName("RESULT")
    val rESULT: RESULT,
    @SerializedName("totalCount")
    val totalCount: Int,
    @SerializedName("type")
    val type: String
)