package com.illiaherman.connection.internet

import android.content.Context
import android.content.IntentFilter
import android.util.Log
import java.lang.ref.WeakReference


/**
 *  This class processes data received from NetworkChangeReceiver and tries to check
 *  internet availability using InternetConnectionChecker async task.
 *  Created by illia.herman on 02.10.2019
 */
class NetworkConnectionChecker private constructor(context: Context) :
    NetworkChangeReceiver.NetworkListener {

    private val mContextWeakReference: WeakReference<Context>
    private val mWeakReferencesListener: MutableList<WeakReference<InternetConnectionListener>>? =
        ArrayList()
    private var mNetworkChangeReceiver: NetworkChangeReceiver? = null
    private var mCheckConnectivityCallback: ConnectionCompleted<Boolean>? = null
    private var mIsNetworkChangeRegistered = false

    init {
        val appContext = context.applicationContext
        mContextWeakReference = WeakReference(appContext)
    }

    /**
     * Use only if it's not added yet
     */
    fun addInternetConnectionListener(internetConnectionListener: InternetConnectionListener?) {
        if (internetConnectionListener == null) {
            return
        }
        mWeakReferencesListener?.add(WeakReference(internetConnectionListener))
        if (mWeakReferencesListener?.size == 1) {
            registerNetworkChangeReceiver()
        }
    }

    fun removeAllInternetConnectivityChangeListeners() {
        if (mWeakReferencesListener == null) {
            return
        }
        val iterator = mWeakReferencesListener.iterator()
        while (iterator.hasNext()) {
            iterator.next().clear()
            iterator.remove()
        }
        unregisterNetworkChangeReceiver()
    }

    /**
     * if not registered yet register NetworkChangeReceiver
     */
    private fun registerNetworkChangeReceiver() {
        val context = mContextWeakReference.get()
        if (context != null && !mIsNetworkChangeRegistered) {
            mNetworkChangeReceiver = NetworkChangeReceiver()
            mNetworkChangeReceiver.let {
                it?.setNetworkChangeListener(this)
            }
            context.registerReceiver(mNetworkChangeReceiver, IntentFilter(CONNECTIVITY_CHANGE))
            mIsNetworkChangeRegistered = true
        }
    }

    /**
     * unregisters NetworkChangeReceiver
     */
    private fun unregisterNetworkChangeReceiver() {
        val context = mContextWeakReference.get()
        if (context != null && mNetworkChangeReceiver != null && mIsNetworkChangeRegistered) {
            try {
                context.unregisterReceiver(mNetworkChangeReceiver)
            } catch (exception: IllegalArgumentException) {
                Log.d("IllegalArgumentExc", exception.message ?: "null")
            }

            mNetworkChangeReceiver?.removeNetworkChangeListener()
        }
        mNetworkChangeReceiver = null
        mIsNetworkChangeRegistered = false
        mCheckConnectivityCallback = null
    }


    override fun onNetworkChange(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            mCheckConnectivityCallback = object : ConnectionCompleted<Boolean> {
                override fun onConnectionCompleted(isInternetAvailable: Boolean) {
                    mCheckConnectivityCallback = null
                    publishInternetConnectionStatus(isInternetAvailable)
                }
            }
            mCheckConnectivityCallback.let { connection ->
                if (connection != null) {
                    InternetConnectionChecker(connection).execute()
                }
            }
        } else {
            publishInternetConnectionStatus(false)
        }
    }

    private fun publishInternetConnectionStatus(isInternetAvailable: Boolean) {
        if (mWeakReferencesListener == null) {
            return
        }

        val iterator = mWeakReferencesListener.iterator()
        while (iterator.hasNext()) {
            val reference = iterator.next()

            val listener = reference.get()
            if (listener == null) {
                iterator.remove()
                continue
            }

            listener.isInternetAvailable(isInternetAvailable)
        }

        if (mWeakReferencesListener.size == 0) {
            unregisterNetworkChangeReceiver()
        }
    }

    companion object {

        private val KEY = Any()
        @Volatile
        private var mInstance: NetworkConnectionChecker? = null

        private const val CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE"

        /**
         * Returns singleton networkInstance.
         *
         * @param context is needed for Connectivity broadcast
         * @return mInstance
         */
        fun init(context: Context?): NetworkConnectionChecker? {
            if (context == null) {
                throw NullPointerException("context can't be null")
            }

            if (mInstance == null) {
                synchronized(KEY) {
                    if (mInstance == null) {
                        mInstance = NetworkConnectionChecker(context)
                    }
                }
            }
            return mInstance
        }

        val networkInstance: NetworkConnectionChecker?
            get() {
                if (mInstance == null) {
                    throw IllegalStateException(" At first, call init(context: Context?)")
                }
                return mInstance
            }
    }

    /**
     * Interface definition for a callback when internet connection completed
     * Return true or false
     */
    internal interface ConnectionCompleted<Boolean> {
        fun onConnectionCompleted(isInternetAvailable: Boolean)
    }
}

