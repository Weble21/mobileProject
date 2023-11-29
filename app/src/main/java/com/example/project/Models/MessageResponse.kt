package com.example.project.Models


import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("DisasterMsg")
    val disasterMsg: List<DisasterMsg>
)