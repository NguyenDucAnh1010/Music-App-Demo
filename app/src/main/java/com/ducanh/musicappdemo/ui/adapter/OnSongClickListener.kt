package com.ducanh.musicappdemo.ui.adapter

import com.ducanh.musicappdemo.data.entity.Song

interface OnSongClickListener {
    fun onItemClick(song: Song)
}