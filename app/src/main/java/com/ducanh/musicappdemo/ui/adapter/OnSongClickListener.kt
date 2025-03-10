package com.ducanh.musicappdemo.ui.adapter

import com.ducanh.musicappdemo.data.entity.MenuItem

interface OnSongClickListener {
    fun onItemClick(menuItem: MenuItem)
}