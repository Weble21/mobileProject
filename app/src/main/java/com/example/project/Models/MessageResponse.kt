package com.example.project.Models

import java.io.Serializable


data class MessageResponse (
    val resultCode : String,
    val type : String,
    val pageNo : Int,

    ) : Serializable