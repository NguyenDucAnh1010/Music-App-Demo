package com.ducanh.musicappdemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor() : ViewModel() {
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    fun updatePlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun updateSeekPosition(position: Int) {
        _currentPosition.value = position
    }
}