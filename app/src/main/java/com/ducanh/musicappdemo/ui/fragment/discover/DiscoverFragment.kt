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

//        songAdapter = SongAdapter(songList, this)
        binding.rvSongs.layoutManager = GridLayoutManager(requireContext(), 2)
//        binding.rvSongs.adapter = songAdapter
        fetchSongs()

        return binding.root
    }

//    private fun fetchSongs() {
//        val url = "https://m.vuiz.net/getlink/mp3zing/api.php?hotsong"
//        val client = OkHttpClient()
//        val request = Request.Builder().url(url).build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("API_ERROR", "Lỗi khi gọi API: ${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.body?.use { responseBody ->
//                    val jsonString = responseBody.string()
//
//                    val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
//                    val jsonList: List<Map<String, Any>> = Gson().fromJson(jsonString, listType)
//
//                    val songs = jsonList.mapNotNull { jsonObject ->
//                        val id = jsonObject["id"] as? String ?: return@mapNotNull null
//                        val title = jsonObject["title"] as? String ?: return@mapNotNull null
//                        val artist =
//                            jsonObject["artists_names"] as? String ?: return@mapNotNull null
//                        val thumbnail = jsonObject["thumbnail"] as? String ?: return@mapNotNull null
//                        Song(id, title, artist, thumbnail)
//                    }
//
//                    activity?.runOnUiThread {
//                        songList.clear()
//                        songList.addAll(songs)
//                        songAdapter.notifyDataSetChanged()
//                    }
//                }
//            }
//        })
//    }

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