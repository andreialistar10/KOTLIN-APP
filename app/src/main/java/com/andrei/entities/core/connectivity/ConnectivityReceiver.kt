package com.andrei.entities.core.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ConnectivityReceiver private constructor(context: Context) {

    private val mutableConnectedToWifi = MutableLiveData<Boolean>().apply { value = false }
    val connectedToWifi: LiveData<Boolean> = mutableConnectedToWifi

    init {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val connected = isConnectedToWifi(network, connectivityManager)
                if (connected != mutableConnectedToWifi.value)
                    mutableConnectedToWifi.postValue(connected)
            }

            override fun onLost(network: Network?) {

                if (network == null) {
                    if (mutableConnectedToWifi.value != false)
                        mutableConnectedToWifi.postValue(false)
                } else {
                    val connected = isConnectedToWifi(network,connectivityManager)
                    if (connected != mutableConnectedToWifi.value)
                    mutableConnectedToWifi.postValue(connected)
                }
            }

            private fun isConnectedToWifi(
                activeNetwork: Network,
                connectivityManager: ConnectivityManager
            ): Boolean {

                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                if (networkCapabilities != null)
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                return false
            }
        })
    }

    fun connectedToWifi(context: Context): Boolean {

        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connMgr.activeNetwork

        if (activeNetwork != null) {
            val networkCapabilities = connMgr.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities != null)
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
    }

    companion object {

        @Volatile
        private var INSTANCE: ConnectivityReceiver? = null

        fun getInstance(context: Context): ConnectivityReceiver {

            val inst = INSTANCE
            if (inst != null)
                return inst
            val instance = ConnectivityReceiver(context)
            INSTANCE = instance
            return instance
        }
    }
}