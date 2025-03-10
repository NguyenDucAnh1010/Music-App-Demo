package com.ducanh.musicappdemo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ducanh.musicappdemo.R
import com.ducanh.musicappdemo.data.entity.Song
import com.ducanh.musicappdemo.databinding.ItemSongBinding

class SongAdapter(
    private var items: List<Song>,
    private val listener: OnSongClickListener
) :
    RecyclerView.Adapter<SongAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.item = item

        Glide.with(holder.itemView.context).load(item.thumbnail).into(holder.binding.imgThumbnail)
//        var sharedPref =
//            context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
//
//        var textSize = sharedPref.getFloat("textSize", 19f)
//        holder.binding.tvWord.textSize = textSize
//        holder.binding.tvMean.textSize = textSize


//        holder.itemView.setOnClickListener {
//            listener.onItemClick(item)
//        }
    }

    override fun getItemCount(): Int = items.size
}