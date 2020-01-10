package com.andrei.entities.components.products

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.andrei.entities.components.data.Product
import com.andrei.entities.components.data.ProductRepository
import com.andrei.entities.components.data.local.AbstractProductDatabase
import com.andrei.entities.components.data.remote.ProductApi
import com.andrei.entities.core.Result
import com.andrei.entities.core.TAG
import kotlinx.coroutines.launch

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableProducts = MutableLiveData<List<Product>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val products: LiveData<List<Product>> = mutableProducts
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    val productRepository: ProductRepository

    init {
        val productDao =
            AbstractProductDatabase.getDatabase(application, viewModelScope).productDao()
        productRepository = ProductRepository(productDao)
    }

    fun refresh() {

        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = productRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded")
                    mutableProducts.value = result.data
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }

    fun addProduct(product: Product){

        viewModelScope.launch {
            productRepository.saveLocalStorage(product)
            mutableProducts.value?.let {
                val newList = it.toMutableList()
                newList.add(product)
                mutableProducts.value = newList
            }
        }
    }

    fun updateProduct(product: Product) {

        viewModelScope.launch {
            productRepository.updateLocalStorage(product)
            mutableProducts.value?.let {
                val newList = it.toMutableList()
                val index = newList.indexOf(product)
                newList[index] = product
                mutableProducts.value = newList
            }
        }
    }
}