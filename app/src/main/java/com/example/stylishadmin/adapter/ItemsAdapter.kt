    package com.example.stylishadmin.adapter

    import android.annotation.SuppressLint
    import android.content.Context
    import android.content.Intent
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.TextView
    import android.widget.Toast
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.bumptech.glide.load.resource.bitmap.CenterInside
    import com.bumptech.glide.request.RequestOptions
    import com.example.stylishadmin.R
    import com.example.stylishadmin.model.items.Item
    import com.facebook.shimmer.ShimmerFrameLayout
    import com.google.android.material.color.MaterialColors

    class ItemsAdapter(
        var itemList: List<Item> = emptyList(),
        private var isLoading: Boolean = true,  // Default value to avoid nulls
        private val onItemSelected: (Item) -> Unit // Callback for item selection
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var selectedPosition = RecyclerView.NO_POSITION // Track selected position

        private val VIEW_TYPE_ITEM = 0
        private val VIEW_TYPE_SHIMMER = 1
        private val shimmerItemCount = 10 // Number of shimmer placeholders

        // ViewHolder for item data
        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val itemImage: ImageView = itemView.findViewById(R.id.product_img_card)
            val itemName: TextView = itemView.findViewById(R.id.itemNameCard)
            val itemPrice: TextView = itemView.findViewById(R.id.itemPrice_incard)

            fun bind(itemModel: Item, context: Context) {
                itemName.text = itemModel.title
                itemPrice.text = itemModel.price.toString()

                // Load image using Glide
                val requestOptions = RequestOptions().transforms(CenterInside())
                Glide.with(context)
                    .load(itemModel.imgUrl[0].toString())
                    .apply(requestOptions)
                    .into(itemImage)

                // Highlight selected item
                itemView.setBackgroundResource(
                    if (adapterPosition == selectedPosition) R.drawable.selected_item_background
                    else R.drawable.default_item_background
                )

                // Handle item selection
                itemView.setOnClickListener {
                    onItemSelected(itemModel) // Pass selected item to callback
                }


            }
        }

        // ViewHolder for shimmer placeholders
        inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val shimmerFrameLayout: ShimmerFrameLayout = itemView.findViewById(R.id.shimmer_item_cloth)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_ITEM) {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
                ItemViewHolder(itemView)
            } else {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.sample_shimmer_card_cloth, parent, false)
                ShimmerViewHolder(itemView)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == VIEW_TYPE_ITEM) {
                val itemViewHolder = holder as ItemViewHolder
                val currentItem = itemList[position]
                itemViewHolder.bind(currentItem, holder.itemView.context)

            } else {
                val shimmerViewHolder = holder as ShimmerViewHolder
                shimmerViewHolder.shimmerFrameLayout.startShimmer() // Start shimmer animation
            }
        }

        override fun getItemCount(): Int {
            return if (isLoading) shimmerItemCount else itemList.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_ITEM
        }

        // Update items list and notify adapter
        fun updateItems(newItems: List<Item>) {
            itemList = newItems
            notifyDataSetChanged()
        }

        fun setLoadingState(state: Boolean) {
            isLoading = state
            notifyDataSetChanged()
        }
    }
