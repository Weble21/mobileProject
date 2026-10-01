package com.example.project.Models

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("header")
    val header: MessageHeader?,

    @SerializedName("pageNo")
    val pageNo: Int,

    @SerializedName("numOfRows")
    val numOfRows: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("body")
    val body: List<DisasterMessage>?
)

data class MessageHeader(
    @SerializedName("resultCode")
    val resultCode: String?,

    @SerializedName("resultMsg")
    val resultMsg: String?,

    @SerializedName("errorMsg")
    val errorMsg: String?
)

data class DisasterMessage(
    @SerializedName("MSG_CN")
    val message: String?,

    @SerializedName("CRT_DT")
    val createdAt: String?,

    @SerializedName("RCPTN_RGN_NM")
    val regionName: String?
)
