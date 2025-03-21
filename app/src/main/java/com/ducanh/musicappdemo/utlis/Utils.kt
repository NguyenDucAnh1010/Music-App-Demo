package com.ducanh.musicappdemo.utlis

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.ducanh.musicappdemo.presentation.service.MusicService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

object Utils {
    fun extractAudioSrc(html: String): String? {
        val document: Document = Jsoup.parse(html)
        return document.select(".amazingaudioplayer-source").attr("data-src")
    }

    fun convertSecondsToMinutes(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    fun sendMusicCommand(context: Context, action: String? = null, progress: Int? = null) {
        val intent = Intent(context, MusicService::class.java).apply {
            action?.let { putExtra("action", action) }
            progress?.let { putExtra("progress", progress) }
        }
        context.startService(intent)
    }

    fun downloadSong(context: Context, url: String, title: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setDescription("Đang tải xuống bài hát...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "$title.mp3")

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    fun deleteSong(context: Context, filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }
}