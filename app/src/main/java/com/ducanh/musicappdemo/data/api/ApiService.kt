package com.ducanh.musicappdemo.data.api

import com.ducanh.musicappdemo.data.entity.Song
import retrofit2.http.GET

interface ApiService {
    @GET("api.php?hotsong")
    suspend fun getSongs(): List<Song>

//    companion object {
//        fun create(): ApiService {
//            return RetrofitInstance.retrofit.create(ApiService::class.java)
//        }
//    }
}