package com.example.stylishadmin.repository.items

import androidx.core.net.toUri
import com.example.stylishadmin.model.items.Item
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class ItemsRepoImp : ItemsRepositoryInterface {
    private val db = Firebase.database
    private val itemsRef = db.getReference("items")

    override suspend fun getItems(): Result<List<Item>> {

        return try {
            val snapshot = itemsRef.get().await()
            val items = snapshot.children.mapNotNull { it.getValue(Item::class.java) }
            Result.success(items)


        }catch (e: Exception){
            Result.failure(e)
        }

    }

    override suspend fun getItem(itemId: String): Result<Item> {

        val itemRef = itemsRef.child(itemId)
        return try {
            val snapshot = itemRef.get().await()
            val item = snapshot.getValue(Item::class.java)
            if (item != null) {
                Result.success(item)
            } else {
                Result.failure(Exception("Item not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun getItemsByBrand(brand: String): Result<List<Item>> {
        return try {
            val itemsResult = getItems()
            if (itemsResult.isSuccess) {
                val filteredItems = itemsResult.getOrNull()?.filter { item ->
                    item.brand == brand
                }
                Result.success(filteredItems ?: emptyList()) // Ensure to not return null
            } else {
                Result.failure(itemsResult.exceptionOrNull() ?: Exception("Unknown error occurred"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun addItem(item: Item): Result<Boolean> {
        val newItemRef = itemsRef.push() // Generates a new unique key
        val itemId = newItemRef.key ?: return Result.failure(Exception("Failed to generate item ID")) // Ensure key is non-null

        val updatedItem = item.copy(id = itemId)

        return try {
            newItemRef.setValue(updatedItem).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun updateItem(itemId: String, item: Item): Result<Boolean> {
        val itemRef = itemsRef.child(itemId)

        return try {
            itemRef.setValue(item).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
    override suspend fun deleteItem(itemId: String): Result<Boolean> {
        val itemRef = itemsRef.child(itemId)
        return try {
            itemRef.removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun uploadImagesOfItemBackWithUrls(
        imageUris: List<String>,
        itemId: String
    ): Result<List<String>> {
        val firebaseStorage = Firebase.storage
        val storageRef = firebaseStorage.reference
        val uploadedImageUrls = mutableListOf<String>()

        return try {
            for((index,imageUri) in imageUris.withIndex()){
                val imageRef = storageRef.child("items/$itemId/image$index")
                val uploadTask = imageRef.putFile(imageUri.toUri()).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
                uploadedImageUrls.add(downloadUrl)
            }
            Result.success(uploadedImageUrls)
        }catch (e: Exception){
            Result.failure(e)

        }
    }

    override suspend fun putItemImagesURLsInRemoteStorage(
        imageUrls: List<String>,
        itemId: String
    ): Result<Boolean> {
        return try {
            val itemRef = itemsRef.child(itemId)
            itemRef.child("imgUrl").setValue(imageUrls).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getImagesOfItemFromRemoteStorage(itemId: String): Result<List<String>> {
        val firebaseStorage = Firebase.storage
        val storageRef = firebaseStorage.reference
        val imageUrls = mutableListOf<String>()
        return try {
            val listResult = storageRef.child("items/$itemId").listAll().await()
            for (item in listResult.items) {
                val downloadUrl = item.downloadUrl.await().toString()
                imageUrls.add(downloadUrl)
            }
            Result.success(imageUrls)
        }catch (e: Exception){
            Result.failure(e)
        }
    }


}
