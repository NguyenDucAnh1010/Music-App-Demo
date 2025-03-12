package com.ducanh.musicappdemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.presentation.repository.SongRepository
import com.ducanh.musicappdemo.presentation.repository.SongRepositoryApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repositoryApi: SongRepositoryApi,
    private val repository: SongRepository
) :
    ViewModel() {
    private val _songs = MutableLiveData<List<Song>>(listOf())
    val songs: LiveData<List<Song>> get() = _songs

    private val _url = MutableLiveData<String>(null)
    val url: LiveData<String> get() = _url

    private val _favoriteSong= MutableLiveData<Song>(null)
    val favoriteSong: LiveData<Song> get() = _favoriteSong

    fun getAllSongApi() {
        viewModelScope.launch(Dispatchers.IO) {
            _songs.postValue(repositoryApi.fetchSongs())
        }
    }

    fun getSongApi(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryApi.getSongInfo(url)?.let {
                _url.postValue(it)
            }
        }
    }

    fun insertFavoriteSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSong(song)
        }
    }

    fun getFavoriteSong(songId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _favoriteSong.postValue(repository.getFavoriteSongById(songId))
        }
    }
}