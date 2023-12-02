package com.example.project.Models.Earthquake


import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("body")
    val body: Body
)