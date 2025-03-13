package com.ducanh.musicappdemo.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.ducanh.musicappdemo.R
import com.ducanh.musicappdemo.ui.MainActivity
import com.ducanh.musicappdemo.ui.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    @Inject
    lateinit var viewModelMusic: MusicViewModel

    private var mediaPlayer: MediaPlayer? = null
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MusicService")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")
        val url = intent?.getStringExtra("url")
        val progress = intent?.getIntExtra("progress", 0)

        when (action) {
            "PLAY" -> {
                url?.let {
                    startForegroundService()
                    playAudio(it)
                }
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
                updateNotification(isPlaying = true)
            }
            setOnCompletionListener {
                viewModelMusic.updatePlayingState(false)
                updateNotification(isPlaying = false)
            }
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        viewModelMusic.updatePlayingState(false)
        updateNotification(isPlaying = false)
    }

    private fun resumeMusic() {
        mediaPlayer?.start()
        viewModelMusic.updatePlayingState(true)
        updateNotification(isPlaying = true)
    }

    private fun stopMusic() {
        mediaPlayer?.release()
        mediaPlayer = null
        viewModelMusic.updatePlayingState(false)
//        stopForeground(true)
//        stopSelf()
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
        mediaSession.release()
        super.onDestroy()
    }

    private fun startForegroundService() {
        createNotificationChannel()
        val notification = buildNotification(isPlaying = true)
        startForeground(1, notification)
    }

    private fun updateNotification(isPlaying: Boolean) {
        val notification = buildNotification(isPlaying)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun buildNotification(isPlaying: Boolean): Notification {
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_pause_small, "Pause",
                PendingIntent.getService(
                    this, 1, Intent(this, MusicService::class.java).putExtra("action", "PAUSE"),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play_arrow_filled_small, "Play",
                PendingIntent.getService(
                    this, 1, Intent(this, MusicService::class.java).putExtra("action", "RESUME"),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        val stopAction = NotificationCompat.Action(
            R.drawable.ic_pause_small, "Stop",
            PendingIntent.getService(
                this, 2, Intent(this, MusicService::class.java).putExtra("action", "STOP"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, "MUSIC_CHANNEL")
            .setContentTitle("Trình phát nhạc")
            .setContentText("Nhạc đang phát")
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_song))
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
//                    .setShowActionsInCompactView(1)
            )
            .addAction(NotificationCompat.Action(R.drawable.ic_back_arrow, "Previous", null))
            .addAction(playPauseAction)
            .addAction(NotificationCompat.Action(R.drawable.ic_next_arrow, "Next", null))
            .addAction(stopAction)
            .setOngoing(isPlaying)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "MUSIC_CHANNEL",
                "Nhạc nền",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
