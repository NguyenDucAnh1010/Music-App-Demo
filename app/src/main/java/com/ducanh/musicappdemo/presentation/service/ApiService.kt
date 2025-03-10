package com.ducanh.musicappdemo.presentation.service

import com.ducanh.musicappdemo.data.entity.Song
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("api.php?hotsong")
    suspend fun getSongs(): List<Song>

    companion object {
        private var instance: ApiService? = null

        fun getInstance(): ApiService {
            return instance ?: synchronized(this) {
                val newInstance = Retrofit.Builder()
                    .baseUrl("https://m.vuiz.net/getlink/mp3zing/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
                instance = newInstance
                newInstance
            }
        }
    }
}