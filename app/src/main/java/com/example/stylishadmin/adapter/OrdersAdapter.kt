package com.example.stylishadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stylishadmin.R
import com.example.stylishadmin.model.orders.Order

class OrdersAdapter(
    private var orderList: List<Order>,
    private val onStatusChanged: (Order, String) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.order_id)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val orderTotalPrice: TextView = itemView.findViewById(R.id.order_total_price)
        val orderItemsRecyclerView: RecyclerView =
            itemView.findViewById(R.id.orderItemsRecyclerView)
        val orderAddress: TextView = itemView.findViewById(R.id.orderAddress)
        val card_order: ConstraintLayout = itemView.findViewById(R.id.inside_order_card)
        val materialButtonToggleGroup: com.google.android.material.button.MaterialButtonToggleGroup =
            itemView.findViewById(R.id.materialButtonToggleGroup)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.orderId.text = "Order ID: ${order.orderId}"
        holder.orderDate.text = "Date: ${order.date}"
        holder.orderTotalPrice.text = "Total: ${order.totalPrice}"
        holder.orderAddress.text = "Address: ${order.address.detailedAddress}"
        val orderItemsAdapter = OrderItemsAdapter(order.orderItems)

        // Set up the Segmented Button with material
        holder.materialButtonToggleGroup.check(
            if (order.status == "pending") R.id.btn_pending else R.id.btn_on_delivery
        )
        holder.materialButtonToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val newStatus = when (checkedId) {
                    R.id.btn_pending -> "pending"
                    R.id.btn_on_delivery -> "on delivery"
                    else -> order.status

                }
                if (newStatus != order.status) {
                    order.status = newStatus
                    onStatusChanged(order, newStatus)
                    notifyItemChanged(holder.adapterPosition)
                    holder.card_order.setBackgroundResource(
                        if (newStatus == "on delivery") R.drawable.card_bg
                        else R.drawable.card_pending_bg
                    )

                }
            }
        }

        // Update background based on the current status
        holder.card_order.setBackgroundResource(
            if (order.status == "on delivery") R.drawable.card_bg
            else R.drawable.card_pending_bg
        )


        // Set up the nested RecyclerView with order items
        holder.orderItemsRecyclerView.layoutManager = LinearLayoutManager(
            holder.orderItemsRecyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        holder.orderItemsRecyclerView.adapter = orderItemsAdapter

    }


    override fun getItemCount(): Int = orderList.size
    fun updateOrders(newOrderList: List<Order>) {
        orderList = newOrderList
        notifyDataSetChanged()

    }
}
