package com.example.project.Models.Earthquake


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("cnt")
    val cnt: Int,
    @SerializedName("dep")
    val dep: Int,
    @SerializedName("fcTp")
    val fcTp: Int,
    @SerializedName("img")
    val img: String,
    @SerializedName("inT")
    val inT: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("loc")
    val loc: String,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("mt")
    val mt: Double,
    @SerializedName("rem")
    val rem: String,
    @SerializedName("stnId")
    val stnId: Int,
    @SerializedName("tmEqk")
    val tmEqk: Long,
    @SerializedName("tmFc")
    val tmFc: Long,
    @SerializedName("tmSeq")
    val tmSeq: Int
)