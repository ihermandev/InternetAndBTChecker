package com.illiaherman.connection.internet

/**
 * Interface definition for a callback when internet availability state changed
 * Created by illia.herman on 02.10.2019
 */
interface InternetConnectionListener {
    fun isInternetAvailable(isConnected: Boolean)
}