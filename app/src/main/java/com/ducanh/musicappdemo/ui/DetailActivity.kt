package com.ducanh.musicappdemo.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.ducanh.musicappdemo.R
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.databinding.ActivityDetailBinding
import com.ducanh.musicappdemo.ui.adapter.ImagePagerAdapter
import com.ducanh.musicappdemo.ui.viewmodel.DiscoverViewModel
import com.ducanh.musicappdemo.utlis.Utils.convertSecondsToMinutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var handler: Handler

    private val viewModel: DiscoverViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handler = Handler(Looper.getMainLooper())

        val song = intent.getSerializableExtra("song") as? Song
        song?.let {
            binding.barMusicPlayerSong.tvSongTitle.text = song.title
            binding.barMusicPlayerSong.tvArtist.text = song.artist
            binding.barMusicPlayerSong.tvTimeSong.text = convertSecondsToMinutes(it.duration)
            binding.barMusicPlayerSong.seekSpeed.max = it.duration *1000
        }

//        mediaPlayer?.setOnPreparedListener {
//            mediaPlayer?.start()
//            updateSeekBar()
//        }

        binding.barMusicPlayerSong.seekSpeed.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val images = mutableListOf<Song>()

        binding.viewPager.apply {
            adapter = ImagePagerAdapter(images)
            offscreenPageLimit = 3
            setPageTransformer { page, position ->
                val scale = 1 - 0.2f * kotlin.math.abs(position)
                page.scaleY = scale
                page.alpha = 0.7f + (1 - kotlin.math.abs(position)) * 0.3f
            }
        }

        viewModel.songs.observe(this) {
            binding.viewPager.adapter = ImagePagerAdapter(it)

            val currentSongIndex = it.indexOfFirst { it.id == song?.id }

            if (currentSongIndex != -1) {
                binding.viewPager.post {
                    binding.viewPager.setCurrentItem(currentSongIndex, false)
                }
            } else {
                binding.viewPager.post {
                    binding.viewPager.setCurrentItem(0, false)
                }
            }
        }

        viewModel.url.observe(this) {
            it?.let {
                playAudio(it)
            }
        }

        song?.path?.let {
            viewModel.getSongApi("https://zingmp3.vn${song.path}")
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val currentSong = viewModel.songs.value?.get(position)
                currentSong?.let {
                    binding.barMusicPlayerSong.tvSongTitle.text = it.title
                    binding.barMusicPlayerSong.tvArtist.text = it.artist
                    binding.barMusicPlayerSong.tvTimeSong.text =
                        convertSecondsToMinutes(it.duration)
                }
            }
        })

        viewModel.getAllSongApi()
    }

    private fun playAudio(audioUrl: String) {
        try {
            // Nếu đang phát nhạc, dừng lại trước
            mediaPlayer?.release()

            // Tạo MediaPlayer mới
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl) // Cài đặt URL
                prepareAsync() // Chuẩn bị phát nhạc (không chặn UI)
                setOnPreparedListener {
                    start() // Phát khi đã sẵn sàng
                    updateSeekBar()
                }
                setOnCompletionListener {
                    Log.d("MediaPlayer", "Nhạc đã phát xong")
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("MediaPlayer", "Lỗi phát nhạc: what=$what, extra=$extra")
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateSeekBar() {
        mediaPlayer?.let { player ->
            binding.barMusicPlayerSong.seekSpeed.progress = player.currentPosition
            binding.barMusicPlayerSong.tvTimeRun.text = convertSecondsToMinutes(player.currentPosition/1000)

            if (player.isPlaying) {
                handler.postDelayed({ updateSeekBar() }, 1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}