package com.example.stylishadmin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.creageek.segmentedbutton.SegmentedButton
import com.example.stylishadmin.R
import com.example.stylishadmin.model.orders.Order
import com.google.android.material.button.MaterialButton

class OrdersAdapter(
    private var orderList: List<Order>,
    private val onStatusChanged: (Order, String) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.order_id)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        val orderTotalPrice: TextView = itemView.findViewById(R.id.order_total_price)
        val orderStatus: TextView = itemView.findViewById(R.id.order_status)
        val orderItemsRecyclerView: RecyclerView = itemView.findViewById(R.id.orderItemsRecyclerView)
        val context : Context = itemView.context
        val orderAddress: TextView = itemView.findViewById(R.id.orderAddress)
        val card_order: ConstraintLayout = itemView.findViewById(R.id.inside_order_card)
        val orderStatusSwitch: androidx.appcompat.widget.SwitchCompat = itemView.findViewById(R.id.order_status_switch)
        val materialButtonToggleGroup: com.google.android.material.button.MaterialButtonToggleGroup = itemView.findViewById(R.id.materialButtonToggleGroup)
        val btnPending: MaterialButton = itemView.findViewById(R.id.btn_pending)
        val btnOnDelivery: MaterialButton = itemView.findViewById(R.id.btn_on_delivery)



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
        holder.orderStatus.text = "Status: ${order.status}"
        holder.orderAddress.text = "Address: ${order.address.detailedAddress}"


        val orderItemsAdapter = OrderItemsAdapter(order.orderItems)

        // Set up the Segmented Button with material
        holder.materialButtonToggleGroup.check(
            if (order.status == "pending") R.id.btn_pending else R.id.btn_on_delivery
        )
        holder.materialButtonToggleGroup.addOnButtonCheckedListener { _,checkedId, isChecked ->
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
                    holder.orderStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.context,
                            if (newStatus == "on delivery") android.R.color.holo_green_dark
                            else android.R.color.holo_red_dark
                        )
                    )

                }
                }

        }



        // Update background based on the current status
        holder.card_order.setBackgroundResource(
            if (order.status == "on delivery") R.drawable.card_bg
            else R.drawable.card_pending_bg
        )

        // Change text color based on status
        if (order.status == "on delivery") {
            holder.orderStatus.setTextColor(
                ContextCompat.getColor(
                    holder.context,
                    android.R.color.holo_green_dark
                )
            )
        } else {
            holder.orderStatus.setTextColor(
                ContextCompat.getColor(
                    holder.context,
                    android.R.color.holo_red_dark
                )
            )
        }

        // Set the switch state
        holder.orderStatusSwitch.setOnCheckedChangeListener(null) // Prevent re-triggering during binding
        holder.orderStatusSwitch.isChecked = order.status == "on delivery"


        // Listen to status changes
        holder.orderStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newStatus = if (isChecked) "on delivery" else "pending"
            holder.card_order.setBackgroundResource(
                if (isChecked) R.drawable.card_bg else R.drawable.card_pending_bg
            )
            onStatusChanged(order, newStatus)

            // Change text color based on status
            if (order.status == "on delivery") {
                holder.orderStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.context,
                        android.R.color.holo_green_dark
                    )
                )
            } else {
                holder.orderStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.context,
                        android.R.color.holo_red_dark
                    )
                )
            }
        }

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
