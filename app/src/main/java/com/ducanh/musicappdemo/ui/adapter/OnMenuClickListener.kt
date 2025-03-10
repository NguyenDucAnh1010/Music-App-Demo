package com.ducanh.musicappdemo.ui.adapter

import com.ducanh.musicappdemo.data.entity.MenuItem

interface OnMenuClickListener {
    fun onItemClick(menuItem: MenuItem)
}