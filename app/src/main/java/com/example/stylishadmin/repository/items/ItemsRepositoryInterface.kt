package com.example.stylishadmin.repository.items

import com.example.stylishadmin.model.items.Item

interface ItemsRepositoryInterface {

    //get
    suspend fun getItems(): Result<List<Item>>
    suspend fun getItem(itemId: String): Result<Item>

    suspend fun getItemsByBrand(brand: String): Result<List<Item>>
    //adding
    suspend fun addItem(item: Item): Result<Int>
    //update
    suspend fun updateItem(itemId: String, item: Item): Result<Boolean>
    //remove
    suspend fun deleteItem(itemId: String): Result<Boolean>
    //upload image in certen item and get url
    suspend fun uploadImagesUrisOfItemBackWithUrls(imageUri: List<String>, itemId: String): Result<List<String>>
    suspend fun uploadImagesOfItemBackWithUrls(compressedImages: List<ByteArray>, itemId: String): Result<List<String>>
    //putItemImages in firebase realtime db
    suspend fun putItemImagesURLsInRemoteStorage(imageUrls: List<String>, itemId: String): Result<Boolean>
    //get spacifec item images
    suspend fun getImagesOfItemFromRemoteStorage(itemId: String): Result<List<String>>
    suspend fun getItemsStatistics(): Result<Map<String,Int>>


    suspend fun deleteItemImagesFromStorage(itemId: String): Result<Boolean>
    suspend fun deleteItemImageFromStorage(imageUrl: String): Result<Boolean>
}
