package com.example.stylishadmin.repository.items

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.stylishadmin.model.items.Item
import com.example.stylishadmin.utils.ImageUtils.getResizedAndCompressedBitmap
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    override suspend fun addItem(item: Item): Result<Int> {
        return try {
            // Fetch all existing IDs
            val itemsSnapshot = itemsRef.get().await()
            val existingIds = itemsSnapshot.children.mapNotNull { it.key?.toIntOrNull() }.toSet()

            // Generate a unique ID
            var newId = (existingIds.maxOrNull() ?: 0) + 1
            while (existingIds.contains(newId)) {
                newId++ // Increment ID until it's unique
            }

            // Prepare the item with the unique ID
            val updatedItem = item.copy(id = newId.toString())

            // Save the item to the database under the unique ID
            val newItemRef = itemsRef.child(newId.toString())
            newItemRef.setValue(updatedItem).await()

            // Return success with the new ID
            Result.success(newId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    override suspend fun addItem(item: Item): Result<Boolean> {
//        val newItemRef = itemsRef.push() // Generates a new unique key
//        val itemId = newItemRef.key ?: return Result.failure(Exception("Failed to generate item ID")) // Ensure key is non-null
//
//        val updatedItem = item.copy(id = itemId)
//
//        return try {
//            newItemRef.setValue(updatedItem).await()
//            Result.succfess(true)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//

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

    override suspend fun uploadImagesUrisOfItemBackWithUrls(
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

    override suspend fun uploadImagesOfItemBackWithUrls(
        compressedImages: List<ByteArray>,
        itemId: String
    ): Result<List<String>> {
        val firebaseStorage = Firebase.storage
        val storageRef = firebaseStorage.reference
        val uploadedImageUrls = mutableListOf<String>()
        return try {
            for((index,image) in compressedImages.withIndex()){
                val imageRef = storageRef.child("items/$itemId/image$index")
                val uploadTask = imageRef.putBytes(image).await()
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

    override suspend fun getItemsStatistics(): Result<Map<String, Int>> {
        return try {

            //every brand with total items
            val snapshot = itemsRef.get().await()
            val items = snapshot.children.mapNotNull { it.getValue(Item::class.java) }
            val statistics = items.groupingBy { it.brand }.eachCount()
            Result.success(statistics)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    //delete item images from the storage by the id
    override suspend fun deleteItemImagesFromStorage(itemId: String): Result<Boolean> {
        val firebaseStorage = Firebase.storage
        val storageRef = firebaseStorage.reference
        val itemRef = storageRef.child("items/$itemId")

        return try {
            // List all files under the folder
            val files = itemRef.listAll().await()

            // Delete all files within the folder
            files.items.forEach { file ->
                file.delete().await()
                Log.d("ItemsRepoImp", "Deleted file: ${file.path}")
            }

            // Ensure the folder itself is "deleted" (it's virtual, so removing its contents suffices)
            Log.d("ItemsRepoImp", "Folder items/$itemId cleared successfully")

            // Remove the corresponding entry from the database
            itemsRef.child(itemId).removeValue().await()
            Log.d("ItemsRepoImp", "Item entry removed from database")

            Result.success(true)
        } catch (e: Exception) {
            Log.d("ItemsRepoImp", "Failed to delete item images: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteItemImageFromStorage(imageUrl: String): Result<Boolean> {

        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        return try {
            storageRef.delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }


    }


}
