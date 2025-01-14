package com.example.stylishadmin.model.orders

import com.example.stylishadmin.model.items.ItemDetail
import com.example.stylishadmin.model.user.UserAddress

data class Order(
    val orderId: String = "",
    val orderItems: List<ItemDetail> = emptyList(),  // Default to an empty list
    val userId: String = "",                            // Default to an empty string
    val date: String = "",                              // Default to an empty string
    val totalPrice: String = "",                        // Default to an empty string
    val address: UserAddress = UserAddress(),           // Default to a blank UserAddress object
    var status: String = "pending"                      // Default to "pending""              // Default value
)
