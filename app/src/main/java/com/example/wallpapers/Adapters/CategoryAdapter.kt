package com.example.wallpapers.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.InputStreamBitmapImageDecoderResourceDecoder
import com.example.wallpapers.R
import com.example.wallpapers.databinding.ActivityMainBinding
import com.example.wallpapers.databinding.CategoryButtonItemBinding

class CategoryAdapter (private val list: List<String>, private val listener : RecyclerViewEvent) : RecyclerView.Adapter<CategoryAdapter.ImageViewHolder>(){

    inner class ImageViewHolder(val binding: CategoryButtonItemBinding): RecyclerView.ViewHolder(binding.root) , View.OnClickListener{

        init {
            binding.button.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            CategoryButtonItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.button.text = list[position]
    }

    interface RecyclerViewEvent{
        fun onItemClick(position: Int)
    }
}