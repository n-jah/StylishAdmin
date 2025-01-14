package com.example.stylishadmin.viewModel.orders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylishadmin.model.orders.Order
import com.example.stylishadmin.repository.orders.OrdersRepositoryInterface
import kotlinx.coroutines.launch
class OrdersViewModel(private val ordersRepository: OrdersRepositoryInterface) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _orders = MutableLiveData<List<Order>?>()
    val orders: MutableLiveData<List<Order>?> get() = _orders

    private val _order = MutableLiveData<Order?>()
    val order: MutableLiveData<Order?> get() = _order
//
//    private val _activeOrder = MutableLiveData<Boolean?>()
//    val activeOrder: MutableLiveData<Boolean?> get() = _activeOrder

    private var totalOrders  : Int? = null
    private var totalPendingOrders : Int? = null
    private var totalDeliveredOrders : Int? = null

    init {
    getOrders()
    }

    fun getOrders() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = ordersRepository.getOrders()
                if (result.isSuccess) {
                    _orders.postValue(result.getOrNull())
                } else {
                    _orders.postValue(null)
                }
            } catch (e: Exception) {
                _orders.postValue(null)
                Log.d("OrdersViewModel", "Error fetching orders data: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun getOrder(orderId: String) {
        _loading.value = true

        viewModelScope.launch {

            try {
                val result = ordersRepository.getOrder(orderId)
                if (result.isSuccess) {
                    _order.postValue(result.getOrNull())
                    Log.d("OrdersViewModel", "Order fetched successfully: ${_order.value}")
                } else {
                    _order.postValue(null)
                }
            } catch (e: Exception) {
                _order.postValue(null)
                Log.d("OrdersViewModel", "Error fetching order data: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }


    fun setOrderState(orderId: String, status: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = ordersRepository.setOrderStatues(orderId, status)

                if (result.isSuccess) {
                    Log.d("OrdersViewModel", "Order status updated successfully")
                } else {
                    Log.d("OrdersViewModel", "Error updating order status")
                }
            } catch (e: Exception) {
                Log.d("OrdersViewModel", "Error updating order status: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }




    fun getStatistics(onResult: (Result<Map<String, Int>>) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = ordersRepository.getOrders()

                if (result.isSuccess) {
                    val orders = result.getOrNull()
                    totalOrders = orders?.size
                    totalPendingOrders = orders?.count { it.status == "pending" }
                    totalDeliveredOrders = orders?.count { it.status == "on delivery" }
                    val ordersData = mapOf( "Pending Orders" to totalPendingOrders!!, "Completed Orders" to totalDeliveredOrders!!)
                    onResult(Result.success(ordersData))
                } else {
                    onResult(Result.failure(Exception("Error fetching statistics data")))
                }
            } catch (e: Exception) {
                onResult(Result.failure(e))
                Log.d("OrdersViewModel", "Error fetching statistics data: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }


}