package com.example.wallpapers.Response

data class ApiResponse(
    val hits: List<Hit>,
    val total: Int,
    val totalHits: Int
)