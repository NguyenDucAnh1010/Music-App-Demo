package com.ducanh.musicappdemo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ducanh.musicappdemo.data.entity.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM favoriteSong")
    suspend fun getAllFavoriteSong(): List<Song>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: Song)

    @Query("SELECT * FROM favoriteSong WHERE id = :songId")
    suspend fun getFavoriteSongById(songId: String): Song?
}