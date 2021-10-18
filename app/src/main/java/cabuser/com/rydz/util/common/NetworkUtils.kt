package com.whatsappclone.utils.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by priyanka on 13/4/18.
 */
@Suppress("DEPRECATION")
class NetworkUtils {

    companion object {


        fun checkInternet(context: Context): Boolean {
            var cm: ConnectivityManager
            cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeNetwork: NetworkInfo = cm.activeNetworkInfo!!
            var isConnected: Boolean = activeNetwork.isConnectedOrConnecting
            return isConnected
        }
    }
}