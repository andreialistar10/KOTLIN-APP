package com.andrei.entities.components.product

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.andrei.entities.core.Result
import com.andrei.entities.core.TAG
import com.andrei.entities.components.data.Product
import com.andrei.entities.components.data.ProductRepository
import com.andrei.entities.components.data.local.AbstractProductDatabase
import kotlinx.coroutines.launch

class ProductEditViewModel(application: Application) : AndroidViewModel(application) {

//    private val mutableProduct = MutableLiveData<Product>().apply {
//        value =
//            Product("", 0)
//    }
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

//    val product: LiveData<Product> = mutableProduct
    val fetching: LiveData<Boolean> = mutableFetching
    val completed: LiveData<Boolean> = mutableCompleted
    val exception: LiveData<Exception> = mutableException

    val productRepository: ProductRepository

    init {
        val productDao = AbstractProductDatabase.getDatabase(application,viewModelScope).productDao()
        productRepository = ProductRepository(productDao)
    }

    fun getProductById(productId: Int): LiveData<Product>{
        Log.v(TAG, "getProductById...")
        return productRepository.getById(productId)
    }

    fun saveOrUpdateProduct(product: Product) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdateProduct..");
            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Product>
            if (product.id != 0) {
                result = productRepository.update(product)
            } else {
                result = productRepository.save(product)
            }
            when(result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdateProduct succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "saveOrUpdateProduct failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }

//    fun loadProduct(productId: Int) {
//
//        viewModelScope.launch {
//
//            Log.v(TAG, "loadProduct... id=$productId")
//            mutableFetching.value = true
//            mutableException.value = null
//
//            when (val result = ProductRepository.loadOne(productId)) {
//                is Result.Success -> {
//                    Log.d(TAG, "loadProduct succeeded... id=$productId")
//                    mutableProduct.value = result.data
//                }
//                is Result.Error -> {
//                    Log.w(TAG, "loadProduct failed... id=$productId")
//                    mutableException.value = result.exception
//                }
//            }
//
//            mutableFetching.value = false
//        }
//    }
//
//    fun saveOrUpdateProduct(name: String, price: Int) {
//
//        viewModelScope.launch {
//
//            Log.v(TAG, "saveOrUpdateProduct... {name:$name, price:$price}")
//            val product = mutableProduct.value ?: return@launch
//            product.name = name
//            product.price = price
//            mutableFetching.value = true
//            mutableException.value = null
//            mutableCompleted.value = false
//
//            val result: Result<Product>
//            result = if (product.id != 0) {
//                ProductRepository.update(product)
//            } else
//                ProductRepository.save(product)
//
//            when (result) {
//                is Result.Success -> {
//                    Log.d(TAG, "saveOrUpdate succeeded... {name:$name, price:$price}")
//                    mutableProduct.value = result.data
//                    mutableCompleted.value = true
//                }
//                is Result.Error -> {
//                    Log.w(TAG, "saveOrUpdate failed... {name:$name, price:$price}")
//                    mutableException.value = result.exception
//                }
//            }
//
//            mutableFetching.value = false
//        }
//    }
}