package com.example.wallpapers.API

import com.example.wallpapers.Response.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/api")
    suspend fun loadAll(
        @Query("key") apiKey: String,
        @Query("orientation") orientation : String,
        @Query("q") q : String
    ) : Response<ApiResponse>

    @GET("/api")
    suspend fun loadCategory(
        @Query("key") apiKey: String,
        @Query("orientation") orientation : String,
        @Query("category") category : String
    ) : Response<ApiResponse>

}