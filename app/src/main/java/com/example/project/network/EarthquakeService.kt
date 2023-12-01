package com.example.project.network

import com.example.project.Models.Earthquake.EarthquakeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthquakeService {
    @GET("1360000/EqkInfoService/getEqkMsg")
    fun getMessage (
        @Query("ServiceKey") serviceKey : String,
        @Query("pageNo") pageNo : Int,
        @Query("numOfRows") numOfRows : Int,
        @Query("dataType") dataType : String,
        @Query("fromTmFc") fromTmFc : Int,
        @Query("toTmFc") toTmFc : Int
    ) : Call<EarthquakeResponse>
}