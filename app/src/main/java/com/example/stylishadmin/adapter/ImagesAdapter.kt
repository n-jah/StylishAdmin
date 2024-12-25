package com.example.stylishadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stylishadmin.R

class ImagesAdapter(
    private val images: MutableList<String>, // List of image URLs
    private val onAddClick: () -> Unit,     // Callback for Add button
    private val onRemoveImageClick: (String) -> Unit // Callback for image clicks
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ADD = 0
        private const val TYPE_IMAGE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ADD else TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ADD) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_button, parent, false)
            AddViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
            ImageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddViewHolder) {
            holder.bind(onAddClick)

        } else if (holder is ImageViewHolder) {
            val imageUrl = images[position - 1] // Offset by 1 for Add button
            holder.bind(imageUrl, onRemoveImageClick)


        }
    }

    override fun getItemCount(): Int = images.size + 1 // +1 for Add button

    // ViewHolder for Add Button
    class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bind(onAddClick: () -> Unit) {
            itemView.setOnClickListener { onAddClick() }
        }
    }

    // ViewHolder for Images
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private  val removeImage : ImageView = itemView.findViewById(R.id.removeButtonImage)
        fun bind(imageUrl: String, onRemoveImageClick: (String) -> Unit ) {
            Glide.with(itemView.context).load(imageUrl).into(imageView)
            removeImage.setOnClickListener { onRemoveImageClick(imageUrl) }

        }
    }


}