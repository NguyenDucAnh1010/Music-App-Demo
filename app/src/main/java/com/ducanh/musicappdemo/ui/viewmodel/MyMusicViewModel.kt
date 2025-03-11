package com.ducanh.musicappdemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.presentation.repository.SongRespositoryStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyMusicViewModel(private val repository: SongRespositoryStore) : ViewModel() {
    private val _songs = MutableLiveData<List<Song>>(listOf())
    val songs: LiveData<List<Song>> get() = _songs

    fun getAllMySongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _songs.postValue(repository.getAllMySongs())
        }
    }
}

class MyMusicViewModelFactory(
    private val repository: SongRespositoryStore
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyMusicViewModel(repository) as T
    }
}