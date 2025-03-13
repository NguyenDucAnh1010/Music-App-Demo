package com.ducanh.musicappdemo.ui.fragment.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.databinding.FragmentFavoriteBinding
import com.ducanh.musicappdemo.ui.adapter.OnSongClickListener
import com.ducanh.musicappdemo.ui.adapter.SongAdapter
import com.ducanh.musicappdemo.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment(), OnSongClickListener {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        binding.rvSongs.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.progressBar.visibility = View.VISIBLE

        viewModel.favoriteSongs.observe(viewLifecycleOwner) {
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
                            binding.rvSongs.adapter = SongAdapter(it, this@FavoriteFragment)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DiscoverFragment", "Lỗi API: ${e.message}", e)
                }
            }
        }

        viewModel.getAllFavoriteSong()

        return binding.root
    }

    override fun onItemClick(song: Song, position: Int) {

    }
}