package com.ducanh.musicappdemo.ui

import android.content.Intent
import android.os.Bundle
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
import com.ducanh.musicappdemo.presentation.service.MusicService
import com.ducanh.musicappdemo.ui.adapter.ImagePagerAdapter
import com.ducanh.musicappdemo.ui.viewmodel.DiscoverViewModel
import com.ducanh.musicappdemo.ui.viewmodel.MusicViewModel
import com.ducanh.musicappdemo.utlis.Utils.convertSecondsToMinutes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    @Inject
    lateinit var viewModelMusic: MusicViewModel

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


        val song = intent.getSerializableExtra("song") as? Song
        song?.let {
            binding.barMusicPlayerSong.tvSongTitle.text = it.title
            binding.barMusicPlayerSong.tvArtist.text = it.artist
            binding.barMusicPlayerSong.tvTimeSong.text = convertSecondsToMinutes(it.duration)
            binding.barMusicPlayerSong.seekSpeed.max = it.duration * 1000
            viewModel.getFavoriteSong(it.id)
            viewModel.getSongApi("https://zingmp3.vn${song.path}")
        }

        viewModel.favoriteSong.observe(this) {
            it?.let {
                binding.barMusicPlayerSong.ivHeart.setImageResource(R.drawable.ic_select_favorite)
            }
                ?: binding.barMusicPlayerSong.ivHeart.setImageResource(R.drawable.ic_unselect_favorite)
        }

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
                sendMusicCommand("PLAY", it)
            }
        }

        binding.barMusicPlayerSong.ivHeart.setOnClickListener {
            song?.let {
                viewModel.insertFavoriteSong(it)
                binding.barMusicPlayerSong.ivHeart.setImageResource(R.drawable.ic_select_favorite)
            }
        }

        binding.barMusicPlayerSong.seekSpeed.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) sendMusicCommand("SEEK", progress = progress)

                if (progress == seekBar?.max) {
                    val nextItem = binding.viewPager.currentItem + 1
                    if (nextItem < viewModel.songs.value?.count() ?: 0) {
                        binding.viewPager.setCurrentItem(nextItem)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.barMusicPlayerSong.ivPlayPause.setOnClickListener {
            sendMusicCommand(if (viewModelMusic.isPlaying.value ?: true) "PAUSE" else "RESUME")
        }

        binding.barMusicPlayerSong.ivNext.setOnClickListener {
            val nextItem = binding.viewPager.currentItem + 1
            if (nextItem < viewModel.songs.value?.count() ?: 0) {
                binding.viewPager.setCurrentItem(nextItem)
            }
        }

        binding.barMusicPlayerSong.ivPrev.setOnClickListener {
            val nextItem = binding.viewPager.currentItem - 1
            if (nextItem >= 0) {
                viewModelMusic.currentPosition.value?.let {
                    if (it > 20000) {
                        sendMusicCommand("SEEK", progress = 0)
                    } else {
                        binding.viewPager.setCurrentItem(nextItem)
                    }
                }
            }
        }

        viewModelMusic.isPlaying.observe(this) { isPlaying ->
            binding.barMusicPlayerSong.ivPlayPause.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow_filled
            )
        }

        viewModelMusic.currentPosition.observe(this) { currentPosition ->
            binding.barMusicPlayerSong.seekSpeed.progress = currentPosition
            binding.barMusicPlayerSong.tvTimeRun.text =
                convertSecondsToMinutes(currentPosition / 1000)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val currentSong = viewModel.songs.value?.get(position)
                currentSong?.let {
                    binding.barMusicPlayerSong.tvSongTitle.text = it.title
                    binding.barMusicPlayerSong.tvArtist.text = it.artist
                    binding.barMusicPlayerSong.tvTimeRun.text = "0:00"
                    binding.barMusicPlayerSong.seekSpeed.progress = 0
                    binding.barMusicPlayerSong.seekSpeed.max = it.duration * 1000
                    binding.barMusicPlayerSong.tvTimeSong.text =
                        convertSecondsToMinutes(it.duration)
                    viewModel.getSongApi("https://zingmp3.vn${it.path}")
                    viewModel.getFavoriteSong(it.id)
                }
            }
        })

        viewModel.getAllSongApi()
    }

    private fun sendMusicCommand(action: String, url: String? = null, progress: Int? = null) {
        val intent = Intent(this, MusicService::class.java).apply {
            putExtra("action", action)
            url?.let { putExtra("url", it) }
            progress?.let { putExtra("progress", progress) }
        }
        startService(intent)
    }
}