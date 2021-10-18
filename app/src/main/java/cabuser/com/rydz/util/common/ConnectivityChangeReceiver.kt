package cabuser.com.rydz.util.common

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler

class ConnectivityReceiver constructor() : BroadcastReceiver() {

    var mHandler: Handler?=null
    var mActivity: Activity?=null



    override fun onReceive(context: Context?, intent: Intent?) {

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context!!))
        }

    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnectedOrConnecting
    }


    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
    constructor(activity: Activity, handler: Handler) : this() {
        mHandler = handler
        mActivity = activity
    }


    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }






}