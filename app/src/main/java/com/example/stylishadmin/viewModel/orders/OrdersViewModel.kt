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

    private val _activeOrder = MutableLiveData<Boolean?>()
    val activeOrder: MutableLiveData<Boolean?> get() = _activeOrder

    init {
        getOrder("-OCHdZkkTI9vzvbXyLOs")
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


    fun setOrderState(orderId: String, status: Boolean) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = ordersRepository.setOrderStatues(orderId, status)
                if (result.isSuccess) {
                    _activeOrder.postValue(result.getOrNull())
                    Log.d("OrdersViewModel", "Order state updated successfully: ${_activeOrder.value}")
                } else {
                    _activeOrder.postValue(null)
                }
            } catch (e: Exception) {
                _activeOrder.postValue(null)
                Log.d("OrdersViewModel", "Error updating order state: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }


}