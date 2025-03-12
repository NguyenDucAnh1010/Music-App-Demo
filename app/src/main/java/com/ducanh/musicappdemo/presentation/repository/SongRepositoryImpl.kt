package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.dao.SongDao
import com.ducanh.musicappdemo.data.entity.Song
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class SongRepositoryImpl @Inject constructor(private val dao: SongDao) : SongRepository {
    override suspend fun getAllFavoriteSong(): List<Song> {
        return dao.getAllFavoriteSong()
    }

    override suspend fun insertSong(song: Song) {
        dao.insertSong(song)
    }

    override suspend fun getFavoriteSongById(songId: String): Song? {
        return dao.getFavoriteSongById(songId)
    }
}