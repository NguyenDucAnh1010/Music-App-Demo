package com.ducanh.musicappdemo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.presentation.repository.SongFavoriteRepository
import com.ducanh.musicappdemo.presentation.repository.SongOfflineRepository
import com.ducanh.musicappdemo.presentation.repository.SongOnlineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val songOnlinePository: SongOnlineRepository,
    private val songOfflinePository: SongOfflineRepository,
    private val songFavoritePository: SongFavoriteRepository
) :
    ViewModel() {
    private val _songs = MutableLiveData<List<Song>>(listOf())
    val songs: LiveData<List<Song>> get() = _songs

    private val _favoriteSongs = MutableLiveData<List<Song>>(listOf())
    val favoriteSongs: LiveData<List<Song>> get() = _favoriteSongs

    private val _mySongs = MutableLiveData<List<Song>>(listOf())
    val mySongs: LiveData<List<Song>> get() = _mySongs

    private val _url = MutableLiveData<String>(null)
    val url: LiveData<String> get() = _url

    private val _favoriteSong = MutableLiveData<Song?>(null)
    val favoriteSong: LiveData<Song?> get() = _favoriteSong

    fun getAllSongApi() {
        viewModelScope.launch(Dispatchers.IO) {
            _songs.postValue(songOnlinePository.fetchSongs())
        }
    }

    fun getSongApi(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            songOnlinePository.getSongInfo(url)?.let {
                _url.postValue(it)
            }
        }
    }

    fun insertFavoriteSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            songFavoritePository.insertSong(song)
        }
    }

    fun getFavoriteSong(songId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _favoriteSong.postValue(songFavoritePository.getFavoriteSongById(songId))
        }
    }

    fun deleteFavoriteSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            songFavoritePository.deleteSong(song)
        }
    }

    fun getAllFavoriteSong() {
        viewModelScope.launch(Dispatchers.IO) {
            _favoriteSongs.postValue(songFavoritePository.getAllFavoriteSong())
        }
    }

    fun getAllMySongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _mySongs.postValue(songOfflinePository.getAllMySongs())
        }
    }
}