package com.example.stylishadmin.repository.items

import com.example.stylishadmin.model.items.Item

interface ItemsRepositoryInterface {

    //get
    suspend fun getItems(): Result<List<Item>>
    suspend fun getItem(itemId: String): Result<Item>
    suspend fun getItemsByBrand(brand: String): Result<List<Item>>
    //adding
    suspend fun addItem(item: Item): Result<Boolean>
    //update
    suspend fun updateItem(itemId: String, item: Item): Result<Boolean>
    //remove
    suspend fun deleteItem(itemId: String): Result<Boolean>
    //upload image in certen item and get url
    suspend fun uploadImagesOfItemBackWithUrls(imageUri: List<String>, itemId: String): Result<List<String>>
    //get spasifec item images
    suspend fun getImagesOfItemFromRemoteStorage(itemId: String): Result<List<String>>


}
