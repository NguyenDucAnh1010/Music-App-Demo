package com.ducanh.musicappdemo.presentation.repository

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.ducanh.musicappdemo.data.api.ApiService
import com.ducanh.musicappdemo.data.dao.SongDao
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.utlis.Utils.extractAudioSrc
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.MultipartBody
import javax.inject.Inject

@ViewModelScoped
class SongRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val dao: SongDao,
    private val context: Context
) : SongRepository {
    override suspend fun getAllFavoriteSong(): List<Song> {
        return dao.getAllFavoriteSong()
    }

    override suspend fun insertSong(song: Song) {
        dao.insertSong(song)
    }

    override suspend fun getFavoriteSongById(songId: String): Song? {
        return dao.getFavoriteSongById(songId)
    }

    override suspend fun deleteSong(song: Song) {
        dao.deleteSong(song)
    }

    override suspend fun fetchSongs(): List<Song> {
        return api.getSongs()
    }

    override suspend fun getSongInfo(
        link: String
    ): String? {
        val linkPart = MultipartBody.Part.createFormData(
            "link", link
        )

        var songUrl: String? = null

        val response = api.getSongInfo(linkPart)
        if (response.isSuccessful) {
            val htmlResponse = response.body()?.success
            songUrl = extractAudioSrc(htmlResponse ?: "")

            if (songUrl != null) {
                Log.d("API", "🎵 Link bài hát: $songUrl")
            } else {
                Log.e("API", "❌ Không tìm thấy link nhạc!")
            }
        } else {
            Log.e("API", "Lỗi: ${response.errorBody()?.string()}")
        }

        return songUrl
    }

    override fun getAllMySongs(): List<Song> {
        val songList = mutableListOf<Song>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val data = cursor.getString(dataColumn)
                val duration = cursor.getInt(durationColumn)

                songList.add(Song(id, title, artist, "", data, duration))
            }
        }
        return songList
    }
}