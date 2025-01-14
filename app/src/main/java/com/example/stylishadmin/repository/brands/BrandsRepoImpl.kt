package com.example.stylishadmin.repository.brands

import android.util.Log
import androidx.core.net.toUri
import com.example.stylishadmin.model.brands.Brand
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class BrandsRepoImpl : BrandsRepoInterface {
    private val db = Firebase.database
    private val brandsRef = db.getReference("brands")
    private val counterRef = db.getReference("brandCounter")


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

        return try {
            // Fetch the current brand ID counter
            val currentBrandIdSnapshot = counterRef.get().await()
            val currentBrandId = currentBrandIdSnapshot.value as? Long ?: 0L

            // Increment the brand ID counter for the next brand
            val newBrandId = currentBrandId + 1
            counterRef.setValue(newBrandId).await() // Update the counter in the database

            // Set the new numeric brand ID
            brand.id = newBrandId.toString()

            // Save the brand to the database
            brandsRef.child(newBrandId.toString()).setValue(brand).await()

            Result.success(true) // Operation was successful
        } catch (e: Exception) {
            Result.failure(e) // Operation failed
        }
    }



    override suspend fun uploadImageUriAndGetURL(uri: String, brandId : String): Result<String> {
        return try {
            val imageRef = Firebase.storage.reference.child("brands/$brandId/logo")
            imageRef.putFile(uri.toUri()).await()
            val url = imageRef.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    //add url to realtime db
    override suspend fun addImageUrlToBrand(brandId: String, url: String): Result<Boolean> {
        val brandRef = brandsRef.child(brandId)
        return try {
            brandRef.child("$brandId/logo").setValue(url).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadCompressedImageUriAndGetURL(
        compresdImage: ByteArray,
        brandName: String
    ): Result<String> {

        return try {
            val imageRef = Firebase.storage.reference.child("brands/$brandName/logo")
            imageRef.putBytes(compresdImage).await()
            val url = imageRef.downloadUrl.await().toString()
            Log.d("BrandsRepoImpl", "uploadCompressedImageUriAndGetURL: $url")
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBrand(brand: Brand): Result<Boolean> {

        return try {
            brandsRef.child(brand.id).removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}