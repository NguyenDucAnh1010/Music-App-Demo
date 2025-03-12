package com.ducanh.musicappdemo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "favoriteSong")
data class Song(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("artists_names") val artist: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("link") val path: String,
    @SerializedName("duration") val duration: Int
):Serializable
