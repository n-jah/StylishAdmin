package com.example.stylishadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.request.RequestOptions
import com.example.stylishadmin.R
import com.example.stylishadmin.model.brands.Brand
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.color.MaterialColors

class BrandsAdapter(
    private var brandList: List<Brand> = emptyList(),
    private var isLoading: Boolean = true,
    private val onBrandSelected: (Brand) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_SHIMMER = 1
    private val VIEW_TYPE_MANAGEMENT = 2

    // ViewHolder for brand items
    inner class BrandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val brandIcon: ImageView = itemView.findViewById(R.id.brand_img_icon)
        val brandName: TextView = itemView.findViewById(R.id.brand_name)

        init {
            itemView.setOnClickListener {
                val adjustedPosition = adapterPosition - 1 // Adjust for "Manage Brands"
                if (adjustedPosition >= 0 && adjustedPosition < brandList.size) {
                    val brand = brandList[adjustedPosition]
                    onBrandSelected(brand)

                    // Update selection
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    // ViewHolder for the "Manage Brands" button
    inner class ManageBrandsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                // Handle "Manage Brands" button click
            }
        }
    }

    // ViewHolder for shimmer placeholders
    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shimmerFrameLayout: ShimmerFrameLayout = itemView.findViewById(R.id.shimmer_item_brand)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> BrandViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_brand, parent, false)
            )
            VIEW_TYPE_MANAGEMENT -> ManageBrandsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_manage_brands, parent, false)
            )
            else -> ShimmerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.sample_shimmer_item_brand, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_ITEM -> {
                val brand = brandList[position - 1] // Adjust for "Manage Brands"
                val brandViewHolder = holder as BrandViewHolder
                brandViewHolder.brandName.text = brand.brandName

                val requestOptions = RequestOptions().transforms(CenterInside())
                Glide.with(holder.itemView.context)
                    .load(brand.imgIcon)
                    .placeholder(R.drawable.adidas)
                    .apply(requestOptions)
                    .into(brandViewHolder.brandIcon)

                // Highlight selection
                holder.itemView.setBackgroundResource(
                    if (position == selectedPosition) R.drawable.selected_item_background
                    else R.drawable.default_item_background
                )
            }
            VIEW_TYPE_MANAGEMENT -> {
                // No specific data binding needed for "Manage Brands"
            }
            VIEW_TYPE_SHIMMER -> {
                val shimmerViewHolder = holder as ShimmerViewHolder
                if (isLoading) shimmerViewHolder.shimmerFrameLayout.startShimmer()
                else shimmerViewHolder.shimmerFrameLayout.stopShimmer()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isLoading -> VIEW_TYPE_SHIMMER
            position == 0 -> VIEW_TYPE_MANAGEMENT
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) 5 // Number of shimmer placeholders
        else brandList.size + 1 // Add 1 for the "Manage Brands" button
    }

    // Update the adapter's data
    fun updateBrands(newBrandList: List<Brand>) {
        this.brandList = newBrandList
        notifyDataSetChanged()
    }

    fun setLoadingState(loading: Boolean) {
        this.isLoading = loading
        notifyDataSetChanged()
    }
}
