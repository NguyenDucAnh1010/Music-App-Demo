package com.ducanh.musicappdemo.ui

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ducanh.musicappdemo.R
import com.ducanh.musicappdemo.data.entity.MenuItem
import com.ducanh.musicappdemo.databinding.ActivityMainBinding
import com.ducanh.musicappdemo.ui.adapter.MenuAdapter
import com.ducanh.musicappdemo.ui.adapter.OnMenuClickListener
import com.ducanh.musicappdemo.ui.fragment.discover.DiscoverFragment
import com.ducanh.musicappdemo.ui.fragment.favorite.FavoriteFragment
import com.ducanh.musicappdemo.ui.fragment.mymusic.MyMusicFragment
import com.ducanh.musicappdemo.ui.viewmodel.MainViewModel
import com.ducanh.musicappdemo.utlis.Utils.sendMusicCommand
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMenuClickListener {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.main,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.main.addDrawerListener(toggle)
        toggle.syncState()

        binding.navHeader.ivClose.setOnClickListener {
            binding.main.closeDrawer(GravityCompat.START)
        }

        val menuItems = listOf(
            MenuItem(R.drawable.ic_bulb_on, R.string.kham_pha),
            MenuItem(R.drawable.ic_user, R.string.nhac_cua_toi),
            MenuItem(R.drawable.ic_heart, R.string.bai_hat_yeu_thich),
            MenuItem(R.drawable.ic_globe_americas, R.string.ngon_ngu)
        )

        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = MenuAdapter(menuItems, this)

        replaceFragment(DiscoverFragment())

        sendMusicCommand(this)

        viewModel.currentTrackIndex.observe(this) {
            binding.barMusicPlayer.musicPlayer.visibility = View.VISIBLE
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            binding.barMusicPlayer.btnPlayPause.setImageResource(
                if (isPlaying) R.drawable.ic_pause_small else R.drawable.ic_play_arrow_filled_small
            )
        }

        viewModel.currentPosition.observe(this) { currentPosition ->
            binding.barMusicPlayer.seekSpeed.progress = currentPosition
        }

        binding.barMusicPlayer.seekSpeed.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) sendMusicCommand(this@MainActivity, "SEEK", progress = progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewModel.songs.observe(this) { songs ->
            if (songs.isNotEmpty()) {
                viewModel.currentTrackIndex.observe(this) { index ->
                    if (index in songs.indices) {
                        var song = songs[index]
                        Glide.with(this).load(song.thumbnail).into(binding.barMusicPlayer.ivImgSong)
                        binding.barMusicPlayer.tvSongTitle.text = song.title
                        binding.barMusicPlayer.tvArtist.text = song.artist
                        binding.barMusicPlayer.seekSpeed.max = song.duration * 1000

                        binding.barMusicPlayer.btnNext.setOnClickListener {
                            val nextItem = index + 1
                            if (nextItem in songs.indices) {
                                viewModel.updateCurrentTrackIndex(nextItem)
                                viewModel.getSongApi("https://zingmp3.vn${songs[nextItem].path}")
                            } else {
                                viewModel.updateCurrentTrackIndex(0)
                                viewModel.getSongApi("https://zingmp3.vn${songs[0].path}")
                            }
                        }

                        binding.barMusicPlayer.btnPrev.setOnClickListener {
                            val prevItem = index - 1
                            if (prevItem in songs.indices) {
                                viewModel.updateCurrentTrackIndex(prevItem)
                                viewModel.getSongApi("https://zingmp3.vn${songs[prevItem].path}")
                            } else {
                                sendMusicCommand(this, "SEEK", progress = 0)
                            }
                        }

                        binding.barMusicPlayer.btnPlayPause.setOnClickListener {
                            sendMusicCommand(
                                this,
                                if (viewModel.isPlaying.value ?: true) "PAUSE" else "RESUME"
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(menuItem: MenuItem) {
        when (menuItem.icon) {
            R.drawable.ic_bulb_on -> replaceFragment(DiscoverFragment())
            R.drawable.ic_user -> replaceFragment(MyMusicFragment())
            R.drawable.ic_heart -> replaceFragment(FavoriteFragment())
            else -> replaceFragment(DiscoverFragment())
        }
        binding.main.closeDrawer(GravityCompat.START)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}