package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.entity.Song

interface SongRepository {
    suspend fun getAllFavoriteSong(): List<Song>
    suspend fun insertSong(song: Song)
    suspend fun getFavoriteSongById(songId: String):Song?
    suspend fun deleteSong(song: Song)
    suspend fun fetchSongs(): List<Song>
    suspend fun getSongInfo(
        link: String
    ): String?
    fun getAllMySongs(): List<Song>
}