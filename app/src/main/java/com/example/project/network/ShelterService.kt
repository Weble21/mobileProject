package com.example.project.network

import com.example.project.Models.shelter.shelterResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ShelterService {
    @GET("1741000/TsunamiShelter3/getTsunamiShelter1List")
    fun getMessage (
        @Query("ServiceKey") serviceKey : String,
        @Query("pageNo") pageNo : Int,
        @Query("numOfRows") numOfRows : Int,
        @Query("type") type : String,
    ) : Call<shelterResponse>
}