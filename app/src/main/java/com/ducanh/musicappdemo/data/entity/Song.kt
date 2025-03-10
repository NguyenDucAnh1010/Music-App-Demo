package com.ducanh.musicappdemo.data.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Song(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("artists_names") val artist: String,
    @SerializedName("thumbnail") val thumbnail: String
):Serializable
