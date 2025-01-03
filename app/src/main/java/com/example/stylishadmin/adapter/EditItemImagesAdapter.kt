package com.example.stylishadmin.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.stylishadmin.R

class EditItemImagesAdapter (   private val images: MutableList<String>, // List of image URLs
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
            holder.bind(imageUrl.toString(), onRemoveImageClick)


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
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val removeImage: ImageView = itemView.findViewById(R.id.removeButtonImage)


        fun bind(imageUrl: String, onRemoveImageClick: (String) -> Unit) {
             Glide.with(itemView.context).load(imageUrl).apply(
                 RequestOptions()
                     .override(400, 400) // Resize the image to 400x400 pixels
                     .centerCrop()       // Crop the image to fit the dimensions
             ).into(imageView)

            removeImage.setOnClickListener { onRemoveImageClick(imageUrl) }

        }


    }

    fun addImage(image: String) {
        //add the image in first

        images.add(0, image)
        notifyDataSetChanged()

    }

    fun removeImage(image: String) {
        images.remove(image)
        notifyDataSetChanged()

    }
}