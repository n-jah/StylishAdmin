package com.example.stylishadmin.viewModel.brands

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stylishadmin.model.brands.Brand
import com.example.stylishadmin.repository.brands.BrandsRepoInterface
import kotlinx.coroutines.launch

class BrandsViewModel(private val brandsRepository: BrandsRepoInterface ): ViewModel() {
    private val _brands = MutableLiveData<List<Brand>?>()
    val brands: MutableLiveData<List<Brand>?> get() = _brands
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    init {

        getBrands()
    }
    fun getBrands() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = brandsRepository.getBrands()
                if (result.isSuccess) {
                    _brands.postValue(result.getOrNull())
                    _loading.postValue(false)
                } else {
                    _brands.postValue(null)
                    _loading.postValue(false)
                }
            } catch (e: Exception) {
                _brands.postValue(null)
                _loading.postValue(false)
            }finally {

                _loading.postValue(false)
            }
        }
    }

    fun addBrand(brand: Brand){
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = brandsRepository.addBrand(brand)
                if (result.isSuccess){
                    _loading.postValue(false)
                }else{
                    _loading.postValue(false)
                }
            }catch (e: Exception){
                _loading.postValue(false)
            }finally {
                _loading.postValue(false)
                }
        }
    }

    fun deleteBrand(brand: Brand){
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = brandsRepository.deleteBrand(brand)
                if (result.isSuccess){
                    _loading.postValue(false)
                }else{
                    _loading.postValue(false)
                }
            }catch (e: Exception){
                _loading.postValue(false)
            }finally {
                _loading.postValue(false)
            }
        }
    }

    fun uploadCompresdImageUriAndGetURL(byteArray: ByteArray, brandName : String, getUrl :(String)-> Unit){
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = brandsRepository.uploadCompressedImageUriAndGetURL(byteArray,brandName)
                if (result.isSuccess){
                    getUrl(result.getOrNull().toString())
                    _loading.postValue(false)
                }else{
                    _loading.postValue(false)
                }
            }catch (e: Exception){
                _loading.postValue(false)
            }finally {
                _loading.postValue(false)
            }

        }
    }

    fun uploadCompressedImageUriAndGetURL(
        byteArray: ByteArray,
        brandName: String,
        callBack: (String) -> Unit
    ) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = brandsRepository.uploadCompressedImageUriAndGetURL(byteArray, brandName)
                if (result.isSuccess) {
                    callBack(result.getOrNull().toString())
                    _loading.postValue(false)
                }else if (result.isFailure){
                    Log.d("BrandsViewModel", "uploadCompressedImageUriAndGetURL: ${result.exceptionOrNull()}")
                    _loading.postValue(false)
                }
            } catch (e: Exception) {
                Log.d("BrandsViewModel", "uploadCompressedImageUriAndGetURL: $e")
                _loading.postValue(false)
            } finally {
                _loading.postValue(false)
            }
        }
    }


}