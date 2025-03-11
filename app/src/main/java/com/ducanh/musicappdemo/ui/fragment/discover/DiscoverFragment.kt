package com.ducanh.musicappdemo.ui.fragment.discover

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ducanh.musicappdemo.data.api.ApiService
import com.ducanh.musicappdemo.data.entity.MenuItem
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.databinding.FragmentDiscoverBinding
import com.ducanh.musicappdemo.presentation.repository.SongRepositoryApiImpl
import com.ducanh.musicappdemo.ui.adapter.OnSongClickListener
import com.ducanh.musicappdemo.ui.adapter.SongAdapter
import com.ducanh.musicappdemo.ui.viewmodel.DiscoverViewModel
import com.ducanh.musicappdemo.ui.viewmodel.DiscoverViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoverFragment : Fragment(), OnSongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DiscoverViewModel> {
        DiscoverViewModelFactory(
            SongRepositoryApiImpl(
                ApiService.create()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        binding.rvSongs.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.swipeRefreshLayout.setOnRefreshListener(this)

        binding.progressBar.visibility = View.VISIBLE

//        binding.rvSongs.setOnTouchListener { _, event ->
//            if (binding.rvSongs.adapter?.itemCount == 0) {
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> binding.swipeRefreshLayout.isRefreshing = true
//                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                        binding.swipeRefreshLayout.isRefreshing = false
//                        onRefresh()
//                    }
//                }
//            }
//            false
//        }

        viewModel.songs.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.ivNoSong.visibility = View.GONE
                    binding.txtNoSong.visibility = View.GONE
                    if (isAdded) {
                        delay(3000)
                        binding.progressBar.visibility = View.GONE
                        if (it.isNullOrEmpty()) {
                            binding.ivNoSong.visibility = View.VISIBLE
                            binding.txtNoSong.visibility = View.VISIBLE
                        } else {
                            binding.ivNoSong.visibility = View.GONE
                            binding.txtNoSong.visibility = View.GONE
                            binding.rvSongs.adapter = SongAdapter(it, this@DiscoverFragment)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DiscoverFragment", "Lỗi API: ${e.message}", e)
                }
            }
        }

        viewModel.getAllSongApi()

        return binding.root
    }

    override fun onRefresh() {
        viewModel.songs.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                try {
                    binding.ivNoSong.visibility = View.GONE
                    binding.txtNoSong.visibility = View.GONE
                    if (isAdded) {
                        delay(3000)
                        if (it.isNullOrEmpty()) {
                            binding.ivNoSong.visibility = View.VISIBLE
                            binding.txtNoSong.visibility = View.VISIBLE
                        } else {
                            binding.ivNoSong.visibility = View.GONE
                            binding.txtNoSong.visibility = View.GONE
                            binding.rvSongs.adapter = SongAdapter(it, this@DiscoverFragment)
                        }
                    }
                    binding.swipeRefreshLayout.isRefreshing = false
                } catch (e: Exception) {
                    Log.e("DiscoverFragment", "Lỗi API: ${e.message}", e)
                }
            }
        }
    }

    override fun onItemClick(song: Song) {

    }
}