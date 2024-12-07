package com.example.stylishadmin.viewModel.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stylishadmin.repository.items.ItemsRepositoryInterface

class ItemsViewModelFactory(private val itemsRep : ItemsRepositoryInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsViewModel::class.java)) {
            return ItemsViewModel(itemsRep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}