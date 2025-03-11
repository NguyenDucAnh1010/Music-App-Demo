package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.entity.Song

interface SongRepository {
    suspend fun getAllFavoriteSong(): List<Song>
    suspend fun updateSong(song: Song)
}