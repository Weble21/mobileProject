package com.example.project.network

import com.example.project.Models.MessageResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MessageService {
    @GET("1741000/DisasterMsg3/getDisasterMsg1List")
    fun getMessage (
        @Query("ServiceKey") serviceKey : String,
        @Query("pageNo") pageNo : Int,
        @Query("numOfRows") numOfRows : Int,
        @Query("type") type : String
    ) : Call<MessageResponse>

}