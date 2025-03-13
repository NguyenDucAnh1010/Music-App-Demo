package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.entity.Song

interface SongOfflineRepository {
    fun getAllMySongs(): List<Song>
}