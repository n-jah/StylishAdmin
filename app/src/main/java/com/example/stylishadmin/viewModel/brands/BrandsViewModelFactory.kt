package com.example.stylishadmin.viewModel.brands

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stylishadmin.repository.brands.BrandsRepoInterface

class BrandsViewModelFactory (private val brandsRep : BrandsRepoInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrandsViewModel::class.java)) {
            return BrandsViewModel(brandsRep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}