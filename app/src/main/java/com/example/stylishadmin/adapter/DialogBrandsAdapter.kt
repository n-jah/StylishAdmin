package com.example.stylishadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.example.stylishadmin.R
import com.example.stylishadmin.model.brands.Brand

class DialogBrandsAdapter (
    var brands : List<Brand>, val onDelete: (brand: Brand) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class BrandViewHolder (itemView  :View) :  RecyclerView.ViewHolder(itemView) {
        val brandIcon: ImageView = itemView.findViewById(R.id.brand_img_icon)
        val brandName: TextView = itemView.findViewById(R.id.brand_name)
        val deleteBrand: ImageView = itemView.findViewById(R.id.removeButtonImagebrand)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return BrandViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_brandedite,parent,false)
        )

    }

    override fun getItemCount()= brands.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         val brand = brands[position]
        val brandViewHolder = holder as BrandViewHolder
        brandViewHolder.brandName.text = brand.brandName

        val requestOptions = RequestOptions().transforms(CenterInside())
        Glide.with(holder.itemView.context)
            .load(brand.imgIcon)
            .apply(requestOptions)
            .into(brandViewHolder.brandIcon)

        holder.itemView.setBackgroundResource(
            R.drawable.default_item_background
        )
        holder.deleteBrand.setOnClickListener {
            onDelete(brand)
        }

    }
    fun update(list : List<Brand>){
        this.brands = list
        notifyDataSetChanged()
    }


}