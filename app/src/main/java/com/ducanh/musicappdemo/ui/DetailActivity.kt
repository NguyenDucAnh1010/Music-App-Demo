package com.ducanh.musicappdemo.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ducanh.musicappdemo.R
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.databinding.ActivityDetailBinding
import com.ducanh.musicappdemo.databinding.ActivityMainBinding
import com.ducanh.musicappdemo.ui.adapter.ImagePagerAdapter
import com.ducanh.musicappdemo.ui.viewmodel.DiscoverViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

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

        var images = mutableListOf<Song>()

        binding.viewPager.apply {
            adapter = ImagePagerAdapter(images)
            offscreenPageLimit = 3
            setPageTransformer { page, position ->
                val scale = 1 - 0.2f * kotlin.math.abs(position)
                page.scaleY = scale
                page.alpha = 0.7f + (1 - kotlin.math.abs(position)) * 0.3f
            }
        }

        viewModel.songs.observe(this){
            binding.viewPager.adapter = ImagePagerAdapter(it)
        }

        viewModel.getAllSongApi()
    }
}