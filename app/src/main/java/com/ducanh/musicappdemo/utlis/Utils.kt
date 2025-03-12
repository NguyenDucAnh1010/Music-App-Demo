package com.ducanh.musicappdemo.utlis

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
}