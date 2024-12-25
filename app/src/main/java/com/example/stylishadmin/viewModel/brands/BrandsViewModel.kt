package com.example.stylishadmin.viewModel.brands

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
    fun addImageFromDevice(brand: Brand){
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = brandsRepository.addImageFromDevice()
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

//    fun deleteBrand(brand: Brand){
//        _loading.value = true
//        viewModelScope.launch {
//            try {
//                val result = brandsRepository.deleteBrand(brand)
//                if (result.isSuccess){
//                    _loading.postValue(false)
//                }else{
//                    _loading.postValue(false)
//                }
//            }catch (e: Exception){
//                _loading.postValue(false)
//            }finally {
//                _loading.postValue(false)
//            }
//        }
//    }



}