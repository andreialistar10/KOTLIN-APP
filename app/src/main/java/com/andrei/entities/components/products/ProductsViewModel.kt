package com.andrei.entities.components.products

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.andrei.entities.components.data.Product
import com.andrei.entities.components.data.ProductRepository
import com.andrei.entities.components.data.local.AbstractProductDatabase
import com.andrei.entities.components.data.remote.ProductApi
import com.andrei.entities.core.Result
import com.andrei.entities.core.TAG
import com.andrei.entities.core.connectivity.ConnectivityReceiver
import com.andrei.entities.core.synchronizing.MessageWorker
import com.andrei.entities.core.synchronizing.WebSocketApi
import com.andrei.entities.core.synchronizing.notification.MyNotification
import com.andrei.entities.core.synchronizing.notification.Type
import kotlinx.coroutines.launch

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val mutableProducts = MutableLiveData<List<Product>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }
    private val networkChangeReceiverListener= NetworkChangeReceiverListener()

    val products: LiveData<List<Product>> = mutableProducts
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    val productRepository: ProductRepository

    init {
        val productDao =
            AbstractProductDatabase.getDatabase(application, viewModelScope).productDao()
        productRepository = ProductRepository(productDao)
        ConnectivityReceiver.connectivityReceiverListener = networkChangeReceiverListener
    }

    fun refresh() {

        viewModelScope.launch {
            Log.v(TAG, "refresh...");
            mutableLoading.value = true
            mutableException.value = null
            if (ConnectivityReceiver.connectedToWifi(getApplication()))
                useOnlineSupport()
            else{
                useOfflineSupport()
            }
            mutableLoading.value = false
        }
    }

    private suspend fun useOfflineSupport() {

        when (val result = productRepository.getAll()) {
            is Result.Success -> {
                Log.d(TAG, "load from local storage succeeded")
                mutableProducts.value = result.data
            }
            is Result.Error -> {
                Log.w(TAG, "load from local storage failed", result.exception)
                mutableException.value = result.exception
            }
        }
    }

    private suspend fun useOnlineSupport() {

        when (val result = productRepository.refresh()) {
            is Result.Success -> {
                Log.d(TAG, "refresh succeeded")
                mutableProducts.value = result.data
                val myMessageWorker = MyMessageWorker()
                WebSocketApi.connectToWebSocket(myMessageWorker)
            }
            is Result.Error -> {
                Log.w(TAG, "refresh failed", result.exception)
                mutableException.value = result.exception
            }
        }
    }

    private class NetworkChangeReceiverListener: AppCompatActivity() ,ConnectivityReceiver.ConnectivityReceiverListener{

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            registerReceiver(ConnectivityReceiver(),
//                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }

        override fun onNetworkConnectionChanged(isConnected: Boolean) {

            Log.v(TAG, "connected to wifi: $isConnected")
        }

        override fun onResume() {
            super.onResume()

            ConnectivityReceiver.connectivityReceiverListener = this
        }

    }

    private inner class MyMessageWorker : MessageWorker {

        override fun onMessageArrived(notification: MyNotification) {

            notification.entity.let {
                val product = Product(it.id, it.name, it.price)
                when (notification.type) {
                    Type.ADD -> notifyAddProduct(product)
                    Type.UPDATE -> notifyUpdateProduct(product)
                }
            }
        }

        private fun notifyAddProduct(product: Product) {

            Log.v(TAG, "Add notification arrived...")
            viewModelScope.launch {
                productRepository.saveLocalStorage(product)
                mutableProducts.value?.let {
                    val newList = it.toMutableList()
                    newList.add(product)
                    mutableProducts.value = newList
                }
            }
        }

        private fun notifyUpdateProduct(product: Product) {

            Log.v(TAG, "Update notification arrived...")
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
}