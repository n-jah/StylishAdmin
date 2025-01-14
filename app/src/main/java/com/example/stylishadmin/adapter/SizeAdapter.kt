package com.example.stylishadmin.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stylishadmin.databinding.ItemSizeBinding
import com.example.stylishadmin.model.items.Size

class SizeAdapter(
    private val sizes: MutableList<Size>, // List of size and stock
    private val onIncreaseClick: (Size) -> Unit,  // Callback for increasing stock
    private val onDecreaseClick: (Size) -> Unit,  // Callback for decreasing stock
    private val onStockChange: (Size) -> Unit,     // Callback for deleting size
    private val onDeleteClick: (Size) -> Unit     // Callback for deleting size
) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    inner class SizeViewHolder(private val binding: ItemSizeBinding) : RecyclerView.ViewHolder(binding.root) {
        private var textWatcher: TextWatcher? = null

        fun bind(item: Size) {
            binding.apply {
                textViewSize.text = item.size
                ETQuantity.setText(item.stock.toString()) // Display stock quantity


                // Remove any existing TextWatcher
                textWatcher?.let {
                    ETQuantity.removeTextChangedListener(it)
                }
                // Create a new TextWatcher and attach it
                textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val newStock = s?.toString()?.toIntOrNull() ?: 0
                        item.stock = newStock
                        onStockChange(item)
                    }

                    override fun afterTextChanged(s: Editable?) {}
                }
                ETQuantity.addTextChangedListener(textWatcher)





                btnIncrease.setOnClickListener {
                    onIncreaseClick(item) // Update the data
                    ETQuantity.setText(item.stock.toString()) // Update the UI directly
                }

                btnDecrease.setOnClickListener {
                    onDecreaseClick(item) // Update the data
                    ETQuantity.setText(item.stock.toString()) // Update the UI directly
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
