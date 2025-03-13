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

    private val _discoverySongs = MutableLiveData<List<Song>>(listOf())
    val discoverySongs: LiveData<List<Song>> get() = _discoverySongs

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
            val songs = songOnlinePository.fetchSongs()
            _discoverySongs.postValue(songs)
            _songs.postValue(songs)
        }
    }

    fun getSongApi(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            songOnlinePository.getSongInfo(url)?.let {
                if (_url.value != it) {
                    _url.postValue(it)
                }
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
            val songs = songFavoritePository.getAllFavoriteSong()
            _favoriteSongs.postValue(songs)
            _songs.postValue(songs)
        }
    }

    fun getAllMySongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = songOfflinePository.getAllMySongs()
            _mySongs.postValue(songs)
            _songs.postValue(songs)
        }
    }

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> get() = _currentPosition

    private val _currentTrackIndex = MutableLiveData<Int>(0)
    val currentTrackIndex: LiveData<Int> get() = _currentTrackIndex

    fun updatePlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun updateSeekPosition(position: Int) {
        _currentPosition.value = position
    }

    fun updateCurrentTrackIndex(index: Int) {
        if (_currentTrackIndex.value != index)
            _currentTrackIndex.value = index
    }
}