package com.example.stylishadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.stylishadmin.R
import com.example.stylishadmin.model.items.Size

class SizeStockAdapter(
    private val sizes: MutableList<Size>, // List of size and stock
    private val onEditClick: () -> Unit     // Callback for editing existing size and stock
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ADD = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ADD else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ADD) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_size_with_stock, parent, false)
            AddViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.size_stock_item, parent, false)
            SizeStockViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddViewHolder) {
            val imageResource = if (sizes.isEmpty()) {
                R.drawable.add_icon  // Image for adding new size
            } else {
                R.drawable.addand_edit  // Image for changing existing size
            }

            holder.bind(imageResource, onEditClick)

        } else if (holder is SizeStockViewHolder) {
            val sizeStock = sizes[position - 1] // Offset by 1 for Add New card
            holder.bind(sizeStock)
        }
    }

    override fun getItemCount(): Int = sizes.size + 1 // +1 for Add New card
    fun updateSizes(updatedSizes: List<Size>) {

        sizes.clear()
        sizes.addAll(updatedSizes)
        notifyDataSetChanged()
    }

    // ViewHolder for Add New card
    class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardAdd: CardView = itemView.findViewById(R.id.add_image_card)
        private val imageAdd : ImageView = itemView.findViewById(R.id.imge_of_adding_size)
        fun bind( imageResource: Int ,onEditClick: () -> Unit ) {
            cardAdd.setOnClickListener { onEditClick() }
            imageAdd.setImageResource(imageResource)
        }
    }

    // ViewHolder for Size and Stock cards
    class SizeStockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSize: TextView = itemView.findViewById(R.id.tv_size_content)
        private val tvStock: TextView = itemView.findViewById(R.id.tv_stock_content)

        fun bind(sizeStock: Size) {
            tvSize.text = sizeStock.size
            tvStock.text = sizeStock.stock.toString()
        }
    }
}
