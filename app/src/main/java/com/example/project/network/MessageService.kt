package com.example.project.network

import com.example.project.Models.MessageResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MessageService {
    @GET("/V2/api/DSSP-IF-00247")
    fun getMessage (
        @Query("serviceKey") serviceKey : String,
        @Query("returnType") returnType : String,
        @Query("pageNo") pageNo : Int,
        @Query("numOfRows") numOfRows : Int
    ) : Call<MessageResponse>

}