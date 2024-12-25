package com.example.stylishadmin.repository.brands

import com.example.stylishadmin.model.brands.Brand

interface BrandsRepoInterface {
    suspend fun getBrands(): Result<List<Brand>>
    suspend fun addBrand(brand: Brand): Result<Boolean>
    suspend fun addImageFromDevice(): Result<String>
}