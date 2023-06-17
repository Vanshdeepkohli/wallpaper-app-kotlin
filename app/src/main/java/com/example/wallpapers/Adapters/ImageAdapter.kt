package com.example.wallpapers.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wallpapers.Response.Hit
import com.example.wallpapers.databinding.ImageItemBinding

class ImageAdapter(private val list : List<Hit>, private val context : Context, private val listener : ImageAdapterEvent) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(val binding : ImageItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.image.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onImageClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(context).load(list[position].largeImageURL).into(holder.binding.image)
    }

    interface ImageAdapterEvent{
        fun onImageClick(position: Int)
    }
}