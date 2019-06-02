package ru.karapetiandav.hearthstonecards.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectivityService(private val context: Context) {
    fun hasNetwork(): Boolean {
        var isConnected = false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) {
            isConnected = true
        }

        return isConnected
    }
}