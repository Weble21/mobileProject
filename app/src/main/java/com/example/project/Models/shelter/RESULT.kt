package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class RESULT(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("resultMsg")
    val resultMsg: String
)