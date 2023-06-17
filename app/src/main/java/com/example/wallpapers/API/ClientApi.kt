package com.example.wallpapers.API

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ClientApi {

    private const val BASE_URL = "https://pixabay.com/"

    val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    }
}