package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.entity.Song

interface SongRepositoryApi {
    suspend fun fetchSongs(): List<Song>
}