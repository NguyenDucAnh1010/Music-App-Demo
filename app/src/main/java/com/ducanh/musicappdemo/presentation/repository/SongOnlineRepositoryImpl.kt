package com.ducanh.musicappdemo.presentation.repository

import android.util.Log
import com.ducanh.musicappdemo.data.api.ApiService
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.utlis.Utils.extractAudioSrc
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.MultipartBody
import javax.inject.Inject

@ViewModelScoped
class SongOnlineRepositoryImpl @Inject constructor(
    private val api: ApiService
) : SongOnlineRepository {
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
}