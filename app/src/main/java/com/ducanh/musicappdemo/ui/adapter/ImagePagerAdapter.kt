package com.ducanh.musicappdemo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.databinding.ItemImageBinding

class ImagePagerAdapter(private val images: List<Song>) :
    RecyclerView.Adapter<ImagePagerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = images[position]

        Glide.with(holder.itemView.context).load(item.thumbnail).into(holder.binding.imageView)
    }

    override fun getItemCount(): Int = images.size
}