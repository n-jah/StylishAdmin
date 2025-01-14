package com.example.stylishadmin.viewModel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stylishadmin.repository.orders.OrdersRepoImp
import com.example.stylishadmin.repository.orders.OrdersRepositoryInterface
import com.example.stylishadmin.viewModel.users.UsersViewModel

class OrdersViewModelFactory(private val ordersRep : OrdersRepositoryInterface):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
            return OrdersViewModel(ordersRep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}