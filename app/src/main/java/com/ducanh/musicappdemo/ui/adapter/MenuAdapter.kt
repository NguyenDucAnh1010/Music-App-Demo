package com.ducanh.musicappdemo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ducanh.musicappdemo.data.entity.MenuItem
import com.ducanh.musicappdemo.databinding.NavItemBinding

class MenuAdapter(
    private var items: List<MenuItem>,
    private val listener: OnMenuClickListener
) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    class ViewHolder(var binding: NavItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NavItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.item = item

//        var sharedPref =
//            context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
//
//        var textSize = sharedPref.getFloat("textSize", 19f)
//        holder.binding.tvWord.textSize = textSize
//        holder.binding.tvMean.textSize = textSize


        holder.itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}