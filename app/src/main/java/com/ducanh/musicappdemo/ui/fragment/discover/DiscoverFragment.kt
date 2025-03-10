package com.ducanh.musicappdemo.ui.fragment.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ducanh.musicappdemo.data.entity.MenuItem
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.databinding.FragmentDiscoverBinding
import com.ducanh.musicappdemo.presentation.service.ApiService
import com.ducanh.musicappdemo.ui.adapter.OnSongClickListener
import com.ducanh.musicappdemo.ui.adapter.SongAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverFragment : Fragment(), OnSongClickListener {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<Song>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        binding.rvSongs.layoutManager = GridLayoutManager(requireContext(), 2)
        fetchSongs()

        return binding.root
    }

    private fun fetchSongs() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val songs = withContext(Dispatchers.IO) { ApiService.getInstance().getSongs() }
                binding.rvSongs.adapter = SongAdapter(songs, this@DiscoverFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi API: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClick(menuItem: MenuItem) {

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiscoverFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}