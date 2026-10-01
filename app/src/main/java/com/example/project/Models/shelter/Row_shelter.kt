package com.example.project.Models.shelter


import com.google.gson.annotations.SerializedName

data class Row_shelter(
    @SerializedName("SHNT_PLACE_NM")
    val name: String?,
    @SerializedName("SHNT_PLACE_DTL_POSITION")
    val address: String?,
    @SerializedName("RN_DTL_ADRES")
    val roadAddress: String?,
    @SerializedName("PSBL_NMPR")
    val capacity: Int?
)
