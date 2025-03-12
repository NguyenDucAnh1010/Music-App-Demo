package com.ducanh.musicappdemo.presentation.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.ducanh.musicappdemo.ui.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    @Inject
    lateinit var viewModelMusic: MusicViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")
        val url = intent?.getStringExtra("url")
        val progress = intent?.getIntExtra("progress", 0)

        when (action) {
            "PLAY" -> {
                url?.let { playAudio(it) }
            }

            "PAUSE" -> pauseMusic()
            "RESUME" -> resumeMusic()
            "STOP" -> stopMusic()
            "SEEK" -> mediaPlayer?.seekTo(progress ?: 0)
        }

        return START_STICKY
    }

    private fun playAudio(url: String) {
        stopMusic()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                viewModelMusic.updatePlayingState(true)
                start()
                updateSeekBar()
            }
            setOnCompletionListener {
                viewModelMusic.updatePlayingState(false)
            }
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        viewModelMusic.updatePlayingState(false)
    }

    private fun resumeMusic() {
        mediaPlayer?.start()
        viewModelMusic.updatePlayingState(true)
    }

    private fun stopMusic() {
        mediaPlayer?.release()
        mediaPlayer = null
        viewModelMusic.updatePlayingState(false)
    }

    private fun updateSeekBar() {
        handler.postDelayed({
            mediaPlayer?.let {
                viewModelMusic.updateSeekPosition(it.currentPosition)
                if (it.isPlaying) updateSeekBar()
            }
        }, 1000)
    }

    override fun onDestroy() {
        stopMusic()
        super.onDestroy()
    }
}
