package com.example.stylishadmin.adapter

import android.content.Context
import android.graphics.Bitmap
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
import com.example.stylishadmin.utils.ImageUtils.getResizedAndCompressedBitmap

import com.example.stylishadmin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun getResizedAndCompressedBitmapAsync(
        context : Context,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int,
        onComplete: (ByteArray?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = getResizedAndCompressedBitmap(context,uri, maxWidth, maxHeight, quality)
            withContext(Dispatchers.Main) {
                onComplete(result) // Call the callback on the UI thread
                if (result == null) {
                    Toast.makeText(context, "Failed to process the image.", Toast.LENGTH_SHORT).show()
                }
            }
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
        private  val removeImage : ImageView = itemView.findViewById(R.id.removeButtonImage)


        fun bind(imageUrl: String, onRemoveImageClick: (String) -> Unit ) {
            getResizedAndCompressedBitmapAsync(itemView.context,imageUrl.toUri(), 400, 400, 50) { compressedImage ->
                if (compressedImage != null) {
                    // Use the compressed image here
                    val image = BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.size)
                    imageView.setImageBitmap(image)

                } else {
                    Toast.makeText(itemView.context, "Failed to process the image.", Toast.LENGTH_SHORT).show()
                }
            }

           // Glide.with(itemView.context).load(imageUrl).into(imageView)
            removeImage.setOnClickListener { onRemoveImageClick(imageUrl) }

        }


    }
    fun addImage(image: String) {
        images.add(image)
        notifyDataSetChanged()

    }

    fun removeImage(image: String) {
        images.remove(image)
        notifyDataSetChanged()

    }


}