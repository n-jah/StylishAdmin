package com.example.stylishadmin.viewModel.items

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stylishadmin.model.items.Item
import com.example.stylishadmin.repository.items.ItemsRepositoryInterface
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ItemsViewModel(private val itemsRep: ItemsRepositoryInterface): ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _items = MutableLiveData<List<Item>?>()
    val items: MutableLiveData<List<Item>?> get() = _items

    private val _item = MutableLiveData<Item?>()
    val item: MutableLiveData<Item?> get() = _item
    
    private val _itemsByBrand = MutableLiveData<List<Item>?>()
    val itemsByBrand: MutableLiveData<List<Item>?> get() = _itemsByBrand


    init {
        getItems()
    }

    fun getItems() {

        _loading.value = true
        viewModelScope.launch {
            try {
                val result = itemsRep.getItems()
                if (result.isSuccess) {
                    _items.postValue(result.getOrNull())
                    _loading.postValue(false)
                } else {
                    _items.postValue(null)
                    _loading.postValue(false)
                }
            } catch (e: Exception) {

                _items.postValue(null)
                _loading.postValue(false)

            }finally {
                _loading.postValue(false)
            }
        }
    }

    fun getItem(itemId: String){
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = itemsRep.getItem(itemId)
                if (result.isSuccess) {
                    _item.postValue(result.getOrNull())
                    _loading.postValue(false)
                } else {
                    _item.postValue(null)
                    _loading.postValue(false)
                }

        }catch (e: Exception) {
            _item.postValue(null)
            _loading.postValue(false)
        }finally {

            _loading.postValue(false)
        }


    }
    }
    fun getItemsByBrand(brand: String) {
        _loading.value = true

        viewModelScope.launch {
            try {
                val result = itemsRep.getItemsByBrand(brand)
                if (result.isSuccess) {
                    _itemsByBrand.postValue(result.getOrNull())
                    _loading.postValue(false)
                } else {
                    _itemsByBrand.postValue(null)
                    _loading.postValue(false)
                }
            }catch (e : Exception){
                _itemsByBrand.postValue(null)
                _loading.postValue(false)
            }finally {
                _loading.postValue(false)
            }
        }
    }
    fun addItemsToOnlineDB(item : Item): Result<String>{
        _loading.value = true
        var uiResult = Result.failure<String>(Exception("Not yet implemented"))
        viewModelScope.launch {
            try {
                val result = itemsRep.addItem(item)
                if (result.isSuccess){
                    _loading.postValue(false)
                    uiResult = Result.success("Item added successfully")
                }else{
                    _loading.postValue(false)
                    uiResult = Result.failure(Exception("Failed to add item"))
                }
            }catch (e: Exception){
                _loading.postValue(false)
                uiResult = Result.failure(e)
            }finally {
                _loading.postValue(false)
            }
        }
        return  uiResult

    }

    fun addItemsToOnlineDB(item: Item, onResult: (Result<String>) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = itemsRep.addItem(item)
                if (result.isSuccess) {
                    _loading.postValue(false)
                    onResult(Result.success("Item added successfully"))
                } else {
                    _loading.postValue(false)
                    onResult(Result.failure(Exception("Failed to add item")))
                }
            } catch (e: Exception) {
                _loading.postValue(false)
                onResult(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun updateItem(itemId: String, item: Item, onResult: (Result<String>) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = itemsRep.updateItem(itemId, item)
                if (result.isSuccess) {
                    _loading.postValue(false)
                    onResult(Result.success("Item updated successfully"))
                } else {
                    _loading.postValue(false)
                    onResult(Result.failure(Exception("Failed to update item")))
                }
            } catch (e: Exception) {
                _loading.postValue(false)
                onResult(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }

    //delete item
    fun deleteItem(itemId: String, onResult: (Result<String>) -> Unit)
    {
        _loading.value = true

        viewModelScope.launch {
            try {
                val result = itemsRep.deleteItem(itemId)
                if (result.isSuccess) {
                    _loading.postValue(false)
                    onResult(Result.success("Item deleted successfully"))
                } else {
                    _loading.postValue(false)
                }

            } catch (e: Exception) {
                _loading.postValue(false)
                onResult(Result.failure(e))
            } finally {
                _loading.postValue(false)
            }
        }
    }

    // upload image uri and back with url
    fun uploadImagesBackWithUrls(imageUris: List<String>, itemId: String , onResult: (Result<List<String>>) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = itemsRep.uploadImagesOfItemBackWithUrls(imageUris,itemId)
                if (result.isSuccess) {
                    _loading.postValue(false)
                    onResult(Result.success(result.getOrNull() ?: emptyList()))
                } else {
                    _loading.postValue(false)
                    onResult(Result.failure(Exception("Failed to upload image" ) ))
                }

            }catch (e: Exception) {
                _loading.postValue(false)
                onResult(Result.failure(e))
            }finally {
                _loading.postValue(false)
            }
        }
    }

    // get the urls of item in remote storage
    fun getImagesUrlsOfItem(itemId: String, onResult: (Result<List<String>>) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = itemsRep.getImagesOfItemFromRemoteStorage(itemId)
                if (result.isSuccess) {
                    _loading.postValue(false)
                    onResult(Result.success(result.getOrNull() ?: emptyList()))
                } else {
                    _loading.postValue(false)
                    onResult(Result.failure(Exception("Failed to get image urls")))
                }
            }catch (e: Exception) {
                _loading.postValue(false)
                onResult(Result.failure(e))
            }finally {
                _loading.postValue(false)
            }
        }
    }

    //put urls of image to certin item in remote realtime db
    fun putImagesUrlsInRemoteStorage(imageUrls: List<String>, itemId: String, onResult: (Result<String>) -> Unit) {
        _loading.value = true

        viewModelScope.launch {
            try {
                val result = itemsRep.putItemImagesURLsInRemoteStorage(imageUrls, itemId)
                if (result.isSuccess) {
                    _loading.postValue(false)
                    onResult(Result.success("Image URLs Added"))
                } else {
                    _loading.postValue(false)
                    onResult(Result.failure(Exception("Failed to put image URLs")))

                }
            } catch (e: Exception) {
                _loading.postValue(false)
                onResult(Result.failure(e))
            } finally {
                _loading.postValue(false)

            }
        }
    }

    //one function to upload to firebase Storage and store urls to realtime db
    fun uploadImagesAndPutUrlsInRemoteStorage(imageUris: List<String>, itemId: String, onResult: (Result<String>) -> Unit) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val uploadResult = itemsRep.uploadImagesOfItemBackWithUrls(imageUris, itemId)
                if (uploadResult.isSuccess) {
                    val imageUrls = uploadResult.getOrNull() ?: emptyList()
                    val putResult = itemsRep.putItemImagesURLsInRemoteStorage(imageUrls, itemId)
                    if (putResult.isSuccess) {
                        _loading.postValue(false)
                        onResult(Result.success("Images uploaded and URLs stored successfully"))
                    } else {
                        _loading.postValue(false)
                        onResult(Result.failure(Exception("Failed to store image URLs")))
                    }
                }else{
                    _loading.postValue(false)
                    onResult(Result.failure(Exception("Failed to upload images to storage")))
                }

            }catch (e: Exception) {
                _loading.postValue(false)
                onResult(Result.failure(e))
            }finally {
                _loading.postValue(false)
            }
        }
    }

}
