package com.example.stylishadmin.repository.orders
import com.example.stylishadmin.model.orders.Order

interface OrdersRepositoryInterface {

    suspend fun getOrders(): Result<List<Order>>
    suspend fun getOrder(orderId: String): Result<Order>
    suspend fun setOrderStatues(orderId: String , status: String): Result<Boolean>



}