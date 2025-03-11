package com.ducanh.musicappdemo.presentation.repository

import com.ducanh.musicappdemo.data.entity.Song

interface SongRespositoryStore {
    fun getAllMySongs(): List<Song>
}