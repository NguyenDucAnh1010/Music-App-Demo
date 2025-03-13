package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.entity.Song

interface SongOnlineRepository {
    suspend fun fetchSongs(): List<Song>
    suspend fun getSongInfo(
        link: String
    ): String?
}