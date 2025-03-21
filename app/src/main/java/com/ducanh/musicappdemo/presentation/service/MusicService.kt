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
import com.ducanh.musicappdemo.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {

    @Inject
    lateinit var viewModel: MainViewModel

    private var mediaPlayer: MediaPlayer? = null
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var mediaSession: MediaSessionCompat

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, "MusicService")
        viewModel.url.observeForever { newUrl ->
            newUrl?.let {
                startForegroundService()
                playAudio(it)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("action")
        val progress = intent?.getIntExtra("progress", 0)

        when (action) {
            "PLAY" -> {
                viewModel.url.value?.let {
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
        mediaPlayer?.release()
        mediaPlayer = null
        viewModel.updatePlayingState(false)

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    viewModel.updatePlayingState(true)
                    start()
                    updateSeekBar()
                }
                setOnCompletionListener {
                    viewModel.updatePlayingState(false)
                    playNextTrack()
                }
            }
        } else {
            mediaPlayer?.start()
            viewModel.updatePlayingState(true)
        }
    }

    private fun playNextTrack() {
        viewModel.songs.value?.let { songsList ->
            viewModel.currentTrackIndex.value?.let{
                val currentSongIndex = it
                if (currentSongIndex + 1 < songsList.size) {
                    val nextSong = currentSongIndex + 1
                    viewModel.updateCurrentTrackIndex(nextSong)
                    viewModel.getSongApi("https://zingmp3.vn${songsList[nextSong].path}")
                }
//                else {
//                    viewModel.updateCurrentTrackIndex(0)
//                    viewModel.getSongApi("https://zingmp3.vn${songsList[0].path}")
//                }
            }
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        viewModel.updatePlayingState(false)
        updateNotification(isPlaying = false)
    }

    private fun resumeMusic() {
        mediaPlayer?.start()
        viewModel.updatePlayingState(true)
        updateNotification(isPlaying = true)
        updateSeekBar()
    }

    private fun stopMusic() {
//        mediaPlayer?.release()
//        mediaPlayer = null
        mediaPlayer?.pause()
        viewModel.updatePlayingState(false)
        stopForeground(true) // Xóa thông báo
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(1)
    }

    private fun updateSeekBar() {
        handler.postDelayed({
            mediaPlayer?.let {
                viewModel.updateSeekPosition(it.currentPosition)
                if (it.isPlaying) updateSeekBar()
            }
        }, 1000)
    }

    override fun onDestroy() {
        stopMusic()
        mediaSession.release()
        stopForeground(true)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancel(1)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    private fun startForegroundService() {
        val notification = buildNotification(isPlaying = true)
        startForeground(1, notification)
    }

    private fun updateNotification(isPlaying: Boolean) {
        val notification = buildNotification(isPlaying)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
            R.drawable.ic_close, "Stop",
            PendingIntent.getService(
                this, 2, Intent(this, MusicService::class.java).putExtra("action", "STOP"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val song = viewModel.songs.value?.get(viewModel.currentTrackIndex.value?:0)
        return NotificationCompat.Builder(this, "MUSIC_CHANNEL")
            .setContentTitle(song?.title ?: "Bài hát")
            .setContentText(song?.artist ?: "Tác giả")
            .setSmallIcon(R.drawable.ic_music_note)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.bg_notification))
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(1)
            )
            .addAction(R.drawable.ic_back_arrow, "Previous", null)
            .addAction(playPauseAction)
            .addAction(R.drawable.ic_next_arrow, "Next", null)
            .addAction(stopAction)
            .setOngoing(isPlaying)
            .build()
    }
}
