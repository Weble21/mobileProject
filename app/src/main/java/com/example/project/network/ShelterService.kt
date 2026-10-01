package com.example.project.network

import com.example.project.Models.shelter.ShelterResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ShelterService {
    @GET("V2/api/DSSP-IF-10944")
    fun getMessage (
        @Query("serviceKey") serviceKey : String,
        @Query("pageNo") pageNo : Int,
        @Query("numOfRows") numOfRows : Int,
        @Query("returnType") type : String,
    ) : Call<ShelterResponse>
}
