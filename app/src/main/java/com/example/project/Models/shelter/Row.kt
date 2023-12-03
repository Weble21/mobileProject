package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class Row(
    @SerializedName("address")
    val address: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lenth")
    val lenth: Int,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("remarks")
    val remarks: String,
    @SerializedName("seismic")
    val seismic: String,
    @SerializedName("shel_av")
    val shelAv: Int,
    @SerializedName("shel_div_type")
    val shelDivType: String,
    @SerializedName("shel_nm")
    val shelNm: String,
    @SerializedName("sido_name")
    val sidoName: String,
    @SerializedName("sigungu_name")
    val sigunguName: String
)