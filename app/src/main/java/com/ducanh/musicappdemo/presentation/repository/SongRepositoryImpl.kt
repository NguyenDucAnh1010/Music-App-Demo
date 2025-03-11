package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.dao.SongDao
import com.ducanh.musicappdemo.data.entity.Song

class SongRepositoryImpl(private val dao: SongDao) : SongRepository {
    override suspend fun getAllFavoriteSong(): List<Song> {
        return dao.getAllFavoriteSong()
    }

    override suspend fun updateSong(song: Song) {
        dao.updateSong(song)
    }
}