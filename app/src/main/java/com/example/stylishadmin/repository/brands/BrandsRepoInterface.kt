package com.example.stylishadmin.repository.brands

import com.example.stylishadmin.model.brands.Brand

interface BrandsRepoInterface {
    suspend fun getBrands(): Result<List<Brand>>
    suspend fun addBrand(brand: Brand): Result<Boolean>
    suspend fun uploadImageUriAndGetURL(uri: String, brandId : String):Result<String>
     suspend fun addImageUrlToBrand(brandId: String, url: String): Result<Boolean>
    suspend fun uploadCompressedImageUriAndGetURL(byteArray: ByteArray, brandName: String):Result<String>
    suspend fun deleteBrand(brand: Brand): Result<Boolean>
}