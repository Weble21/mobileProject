package com.example.project.Models


import com.google.gson.annotations.SerializedName

data class Row(
    @SerializedName("create_date")
    val createDate: String,
    @SerializedName("location_id")
    val locationId: String,
    @SerializedName("location_name")
    val locationName: String,
    @SerializedName("md101_sn")
    val md101Sn: String,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("send_platform")
    val sendPlatform: String
)