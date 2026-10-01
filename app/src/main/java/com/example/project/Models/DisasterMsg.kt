package com.example.project.Models


import com.google.gson.annotations.SerializedName

data class DisasterMsg(
    @SerializedName("row")
    val row: List<Row>
)