package com.example.stylishadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stylishadmin.databinding.ItemSizeBinding
import com.example.stylishadmin.model.items.Size

class SizeAdapter(
    private val sizes: MutableList<Size>, // List of size and stock
    private val onIncreaseClick: (Size) -> Unit,  // Callback for increasing stock
    private val onDecreaseClick: (Size) -> Unit,  // Callback for decreasing stock
    private val onDeleteClick: (Size) -> Unit     // Callback for deleting size
) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    inner class SizeViewHolder(private val binding: ItemSizeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Size) {
            binding.apply {
                textViewSize.text = item.size
                ETQuantity.setText(item.stock.toString()) // Display stock quantity

                btnIncrease.setOnClickListener {
                    onIncreaseClick(item) // Handle increase
                    ETQuantity.setText(item.stock.toString()) // Update stock UI
                    notifyItemChanged(adapterPosition) // Notify of update
                }

                btnDecrease.setOnClickListener {
                    onDecreaseClick(item) // Handle decrease
                    ETQuantity.setText(item.stock.toString()) // Update stock UI
                    notifyItemChanged(adapterPosition) // Notify of update
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(item) // Handle deletion

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val binding = ItemSizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.bind(sizes[position]) // Bind data to each item
    }

    override fun getItemCount() = sizes.size // Return size of the list

    // Method to add a new item
    fun addItem(item: Size) {
        sizes.add(item)
        notifyItemInserted(sizes.size - 1) // Notify that an item was added
    }

    // Method to get the current list of items
    fun getItems() = sizes.toList()

    // Method to update an existing item at a specific position
    fun updateItem(position: Int, newItem: Size) {
        sizes[position] = newItem
        notifyItemChanged(position) // Notify that item was updated
    }
}
