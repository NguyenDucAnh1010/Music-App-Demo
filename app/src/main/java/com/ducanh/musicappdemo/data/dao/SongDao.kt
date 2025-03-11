package com.ducanh.musicappdemo.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.ducanh.musicappdemo.data.entity.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM favoriteSong")
    suspend fun getAllFavoriteSong(): List<Song>

    @Update
    suspend fun updateSong(song: Song)
}