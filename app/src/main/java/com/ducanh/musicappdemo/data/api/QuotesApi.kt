package com.ducanh.musicappdemo.data.api

import com.ducanh.musicappdemo.data.entity.Song
import retrofit2.Response
import retrofit2.http.GET

interface QuotesApi {
    @GET("/getlink/mp3zing/api.php?hotsong")
    suspend fun getSongs() : Response<Song>
}