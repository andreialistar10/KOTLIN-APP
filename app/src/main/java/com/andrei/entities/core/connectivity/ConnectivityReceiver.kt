package com.andrei.entities.core.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

class ConnectivityReceiver private constructor(context: Context) {

    init {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (isConnectedToWifi(network, connectivityManager))
                    println("CONNECTED")
                else
                    println("NOT CONNECTED TO WIFI")
            }

            override fun onLost(network: Network?) {
                println("NOT CONNECTED TO WIFI")
            }

            private fun isConnectedToWifi(activeNetwork: Network, connectiviyManager: ConnectivityManager): Boolean {

                val networkCapabilities = connectiviyManager.getNetworkCapabilities(activeNetwork)
                if (networkCapabilities != null)
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                return false
            }
        })
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
    }
}