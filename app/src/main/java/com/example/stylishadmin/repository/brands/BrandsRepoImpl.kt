package com.example.stylishadmin.repository.brands

import com.example.stylishadmin.model.brands.Brand
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class BrandsRepoImpl : BrandsRepoInterface {
    private val db = Firebase.database
    private val brandsRef = db.getReference("brands")

    override suspend fun getBrands(): Result<List<Brand>> {
        return try {
            val snapshot = brandsRef.get().await()
            val brands = snapshot.children.mapNotNull { it.getValue(Brand::class.java) }
            Result.success(brands)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun addBrand(brand: Brand): Result<Boolean> {
        val newBrandRef = brandsRef.push()
        return try {
            newBrandRef.setValue(brand).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addImageFromDevice(): Result<String> {
        // Add image from device

    }
}