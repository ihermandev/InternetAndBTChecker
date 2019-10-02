package com.illiaherman.connection.internet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import java.lang.ref.WeakReference


/**
 * Network Receiver which processes NetworkListener
 * Created by illia.herman on 02.10.2019
 */
internal class NetworkChangeReceiver : BroadcastReceiver() {

    private var mNetworkListenerWeakReference: WeakReference<NetworkListener>? = null

    override fun onReceive(context: Context, intent: Intent) {
        mNetworkListenerWeakReference?.get()?.onNetworkChange(isNetworkConnected(context))
    }

    fun setNetworkChangeListener(networkListener: NetworkListener) {
        mNetworkListenerWeakReference = WeakReference(networkListener)
    }

    fun removeNetworkChangeListener() {
        if (mNetworkListenerWeakReference != null) {
            mNetworkListenerWeakReference?.clear()
        }
    }

    /**
     * For now(02.10.2019) getActiveNetworkInfo() and getNetworkInfo() is deprecated,
     * but official Google documentation doesn't provide any other solution.
     * For potential update check
     * @see <a href="https://developer.android.com/training/basics/network-ops/managing#kotlin">Google</a>
     */
    @Suppress("DEPRECATION")
    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        // null checking because of airplane mode
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected


    }

    //Interface definition for a callback when network status changes
    internal interface NetworkListener {
        fun onNetworkChange(isNetworkAvailable: Boolean)
    }
}