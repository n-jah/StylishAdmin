package com.example.stylishadmin.repository.orders

import com.example.stylishadmin.model.orders.Order
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class OrdersRepoImp : OrdersRepositoryInterface {

    private val db = Firebase.database
    private val ordersRef = db.getReference("orders")
    override suspend fun getOrders(): Result<List<Order>> {
        return try {
            val snapshot = ordersRef.get().await()
            val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrder(orderId: String): Result<Order> {
        val orderRef = ordersRef.child(orderId)
        return try {
            val snapshot = orderRef.get().await()
            val order = snapshot.getValue(Order::class.java)
            if (order != null) {
                Result.success(order)
            } else {
                Result.failure(Exception("Order not found"))
            }
        }
        catch (e: Exception) {
            Result.failure(e)
        }
     }

    override suspend fun setOrderStatues(orderId: String, status: String): Result<Boolean> {
        val orderRef = ordersRef.child(orderId)
        return try {

            orderRef.child("status").setValue(status).await()
            val snapshot = orderRef.child("status").get().await()
            val statusValue = snapshot.getValue(String::class.java)

            if (statusValue != null) {
                Result.success(true)
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

}