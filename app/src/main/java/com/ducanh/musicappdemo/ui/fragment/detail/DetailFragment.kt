package com.ducanh.musicappdemo.ui.fragment.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.ducanh.musicappdemo.R
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.databinding.FragmentDetailBinding
import com.ducanh.musicappdemo.ui.adapter.ImagePagerAdapter
import com.ducanh.musicappdemo.ui.viewmodel.MainViewModel
import com.ducanh.musicappdemo.utlis.Utils.convertSecondsToMinutes
import com.ducanh.musicappdemo.utlis.Utils.sendMusicCommand

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.ivExit.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        var song = arguments?.getSerializable("song") as? Song

        viewModel.favoriteSong.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.barMusicPlayerSong.ivHeart.setImageResource(R.drawable.ic_select_favorite)
                binding.barMusicPlayerSong.ivHeart.setOnClickListener {
                    song?.let {
                        viewModel.deleteFavoriteSong(it)
                        viewModel.getFavoriteSong(it.id)
                    }
                }
            } else {
                binding.barMusicPlayerSong.ivHeart.setImageResource(R.drawable.ic_unselect_favorite)
                binding.barMusicPlayerSong.ivHeart.setOnClickListener {
                    song?.let {
                        viewModel.insertFavoriteSong(it)
                        viewModel.getFavoriteSong(it.id)
                    }
                }
            }
        }

        binding.viewPager.apply {
            viewModel.songs.value?.let {
                adapter = ImagePagerAdapter(it)
            }
            offscreenPageLimit = 3
            setPageTransformer { page, position ->
                val scale = 1 - 0.2f * kotlin.math.abs(position)
                page.scaleY = scale
                page.alpha = 0.7f + (1 - kotlin.math.abs(position)) * 0.3f
            }
        }

        binding.barMusicPlayerSong.seekSpeed.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) sendMusicCommand(requireContext(), "SEEK", progress = progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.barMusicPlayerSong.ivPlayPause.setOnClickListener {
            sendMusicCommand(
                requireContext(),
                if (viewModel.isPlaying.value ?: true) "PAUSE" else "RESUME"
            )
        }

        binding.barMusicPlayerSong.ivNext.setOnClickListener {
            val nextItem = binding.viewPager.currentItem + 1
            if (nextItem < viewModel.songs.value?.count() ?: 0) {
                binding.viewPager.setCurrentItem(nextItem)
            }else{
                binding.viewPager.setCurrentItem(0)
            }
        }

        binding.barMusicPlayerSong.ivPrev.setOnClickListener {
            val nextItem = binding.viewPager.currentItem - 1
            if (nextItem >= 0) {
                viewModel.currentPosition.value?.let {
                    if (it > 20000) {
                        sendMusicCommand(requireContext(), "SEEK", progress = 0)
                    } else {
                        binding.viewPager.setCurrentItem(nextItem)
                    }
                }
            }
        }

        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.barMusicPlayerSong.ivPlayPause.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow_filled
            )
        }

        viewModel.currentPosition.observe(viewLifecycleOwner) { currentPosition ->
            binding.barMusicPlayerSong.seekSpeed.progress = currentPosition
            binding.barMusicPlayerSong.tvTimeRun.text =
                convertSecondsToMinutes(currentPosition / 1000)
        }

        viewModel.currentTrackIndex.observe(viewLifecycleOwner) {
            if (it != binding.viewPager.currentItem) {
                binding.viewPager.post {
                    binding.viewPager.setCurrentItem(it)
                }
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val currentSong = viewModel.songs.value?.get(position)
                currentSong?.let {
                    song = currentSong
                    binding.barMusicPlayerSong.tvSongTitle.text = it.title
                    binding.barMusicPlayerSong.tvArtist.text = it.artist
                    binding.barMusicPlayerSong.seekSpeed.progress = 0
                    binding.barMusicPlayerSong.seekSpeed.max = (it.duration - 1) * 1000
                    binding.barMusicPlayerSong.tvTimeSong.text =
                        convertSecondsToMinutes(it.duration - 1)
                    viewModel.updateCurrentTrackIndex(position)
                    viewModel.getSongApi("https://zingmp3.vn${it.path}")
                    viewModel.getFavoriteSong(it.id)
                }
            }
        })

        return binding.root
    }

    companion object {
        fun newInstance(song: Song): DetailFragment {
            val args = Bundle().apply {
                putSerializable("song", song)
            }

            val fragment = DetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}