package com.example.stylishadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stylishadmin.R
import com.example.stylishadmin.model.orders.Order

class DashboardOrdersAdapter(var ordersList : List<Order> ) :
    RecyclerView.Adapter<DashboardOrdersAdapter.MyOrderViewHolder>() {

    inner class MyOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.orderId_dash)
        val orderTotalPrice: TextView = itemView.findViewById(R.id.orderTotal_dash)
        val orderState : TextView = itemView.findViewById(R.id.orderStatus_dash)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_order_home_recent, parent, false)
        return MyOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        val order = ordersList[position]
        holder.orderId.text = "Order ID: ${order.orderId}"
        holder.orderTotalPrice.text = "Total: ${order.totalPrice}"

        if(order.status == "pending"){
            holder.orderState.text = "Status: Pending"
        }else{
            holder.orderState.text = "Status: On delivery"
        }
    }

    override fun getItemCount(): Int = ordersList.size
    fun updateOrders(orders: List<Order>?) {
        ordersList = orders!!
        notifyDataSetChanged()

    }

}