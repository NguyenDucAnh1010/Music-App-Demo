package com.ducanh.musicappdemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.presentation.repository.SongRepositoryApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiscoverViewModel(private val repository: SongRepositoryApi) : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>(listOf())
    val songs: LiveData<List<Song>> get() = _songs

    fun getAllSongApi() {
        viewModelScope.launch(Dispatchers.IO) {
            _songs.postValue(repository.fetchSongs())
        }
    }
}

class DiscoverViewModelFactory(
    private val repository: SongRepositoryApi
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DiscoverViewModel(repository) as T
    }
}