package com.ducanh.musicappdemo.presentation.repository

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.ducanh.musicappdemo.data.api.ApiService
import com.ducanh.musicappdemo.data.dao.SongDao
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.utlis.Utils.extractAudioSrc
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.MultipartBody
import javax.inject.Inject

@ViewModelScoped
class SongFavoriteRepositoryImpl @Inject constructor(
    private val dao: SongDao
) : SongFavoriteRepository {
    override suspend fun getAllFavoriteSong(): List<Song> {
        return dao.getAllFavoriteSong()
    }

    override suspend fun insertSong(song: Song) {
        dao.insertSong(song)
    }

    override suspend fun getFavoriteSongById(songId: String): Song? {
        return dao.getFavoriteSongById(songId)
    }

    override suspend fun deleteSong(song: Song) {
        dao.deleteSong(song)
    }
}