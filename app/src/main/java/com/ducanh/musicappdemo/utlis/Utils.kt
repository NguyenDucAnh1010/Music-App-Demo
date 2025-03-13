package com.ducanh.musicappdemo.utlis

import android.content.Context
import android.content.Intent
import com.ducanh.musicappdemo.presentation.service.MusicService
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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

    fun sendMusicCommand(context: Context,action: String, url: String? = null, progress: Int? = null) {
        val intent = Intent(context, MusicService::class.java).apply {
            putExtra("action", action)
            url?.let { putExtra("url", it) }
            progress?.let { putExtra("progress", progress) }
        }
        context.startService(intent)
    }
}