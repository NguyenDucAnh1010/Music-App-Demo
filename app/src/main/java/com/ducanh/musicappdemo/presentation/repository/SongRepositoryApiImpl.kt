package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.api.ApiService
import com.ducanh.musicappdemo.data.entity.Song
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SongRepositoryApiImpl @Inject constructor(private val api: ApiService):SongRepositoryApi {
    override suspend fun fetchSongs(): List<Song> {
        return api.getSongs()
    }
}