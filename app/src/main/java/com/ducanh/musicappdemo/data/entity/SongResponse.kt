package com.ducanh.musicappdemo.data.entity

import com.google.gson.annotations.SerializedName

data class SongResponse(
    @SerializedName("success") val success: String
)