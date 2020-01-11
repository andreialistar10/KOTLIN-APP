package com.andrei.entities.core.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, arg1: Intent) {

//        if (connectivityReceiverListener != null) {
//            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedToWifi(context))
//        }
        connectivityReceiverListener?.onNetworkConnectionChanged(isConnectedToWifi(context))
    }

    private fun isConnectedToWifi(context: Context): Boolean {

        return connectedToWifi(context)
    }

    interface ConnectivityReceiverListener {

        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null

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