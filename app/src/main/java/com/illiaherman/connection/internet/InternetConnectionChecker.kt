package com.illiaherman.connection.internet

import android.os.AsyncTask
import com.illiaherman.Const.BASE_URL
import com.illiaherman.Const.CONNECT_TIMEOUT
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


/**
 * The async task attempts to create a socket connection with http://clients3.google.com/generate_204.
 * Return true if succeeds otherwise false
 * Created by illia.herman on 02.10.2019
 */
internal class InternetConnectionChecker(mCallback: NetworkConnectionChecker.ConnectionCompleted<Boolean>) :
    AsyncTask<Void, Void, Boolean>() {

    private val mCallbackWeakReference: WeakReference<NetworkConnectionChecker.ConnectionCompleted<Boolean>> =
        WeakReference(mCallback)

    override fun doInBackground(vararg params: Void): Boolean? {
        try {
            val url = URL(BASE_URL)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.apply {
                setRequestProperty("SecurityStepsReport-Agent", "Android")
                setRequestProperty("Connection", "close")
                connectTimeout = CONNECT_TIMEOUT
                connect()
            }
            return urlConnection.responseCode == 204 && urlConnection.contentLength == 0
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            return false
        }

    }

    /**
     * Send internet availability state true or false to ConnectionCompleted callback.
     *
     */
    override fun onPostExecute(isInternetAvailable: Boolean?) {
        val callback = mCallbackWeakReference.get()
        isInternetAvailable?.let { callback?.onConnectionCompleted(it) }
    }
}

