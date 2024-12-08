package com.example.stylishadmin.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stylishadmin.R

class testImageAdapter(private val images: List<String>): RecyclerView.Adapter<testImageAdapter.ViewHolder>() {
    class ViewHolder(val imageView: View) : RecyclerView.ViewHolder(imageView){

        val image: ImageView =imageView.findViewById(R.id.imagefortestrv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = View.inflate(parent.context, R.layout.image_sample_rv, null)
        return ViewHolder(view)
    }

    override fun getItemCount()= images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.imageView.context).load(image).into(holder.image)
    }
}