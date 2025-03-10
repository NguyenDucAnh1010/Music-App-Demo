package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.api.ApiService
import com.ducanh.musicappdemo.data.entity.Song

class SongRepositoryApiImpl (private val api: ApiService):SongRepository {
    override suspend fun fetchSongs(): List<Song> {
        return api.getSongs()
    }
}