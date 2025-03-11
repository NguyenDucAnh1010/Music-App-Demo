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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ducanh.musicappdemo.data.api.ApiService
import com.ducanh.musicappdemo.data.entity.MenuItem
import com.ducanh.musicappdemo.databinding.FragmentDiscoverBinding
import com.ducanh.musicappdemo.presentation.repository.SongRepositoryApiImpl
import com.ducanh.musicappdemo.ui.adapter.OnSongClickListener
import com.ducanh.musicappdemo.ui.adapter.SongAdapter
import com.ducanh.musicappdemo.ui.viewmodel.SongViewModel
import com.ducanh.musicappdemo.ui.viewmodel.SongViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoverFragment : Fragment(), OnSongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SongViewModel> {
        SongViewModelFactory(
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

    }

    override fun onItemClick(menuItem: MenuItem) {

    }
}