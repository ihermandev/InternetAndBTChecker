package com.illiaherman

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.illiaherman.connection.bluetooth.BluetoothConnectionChecker
import com.illiaherman.connection.bluetooth.BluetoothConnectionListener
import com.illiaherman.connection.internet.InternetConnectionListener
import com.illiaherman.connection.internet.NetworkConnectionChecker
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by illia.herman on 02.10.2019
 */
class MainActivity : AppCompatActivity(), BluetoothConnectionListener, InternetConnectionListener {

    override fun isInternetAvailable(isConnected: Boolean) {
        when {
            isConnected -> handleInternetOn()
            else -> handleInternetOff()
        }
    }

    override fun isBluetoothAvailable(isBluetoothTurnOn: Boolean) {
        when {
            isBluetoothTurnOn -> handleBluetoothOn()
            else -> handleBluetoothOff()
        }
    }

    private var mNetworkConnectionChecker: NetworkConnectionChecker? = null
    private var mBluetoothConnectionChecker: BluetoothConnectionChecker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initConnectionChecker()
    }

    private fun initConnectionChecker() {
        NetworkConnectionChecker.init(this)
        mNetworkConnectionChecker = NetworkConnectionChecker.networkInstance
        mNetworkConnectionChecker?.addInternetConnectionListener(this)

        mBluetoothConnectionChecker = BluetoothConnectionChecker(this)
        mBluetoothConnectionChecker?.register(this)
    }

    private fun handleInternetOn() {
        tv_internet.apply {
            text = context.resources.getText(R.string.internet_on)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_on_24dp, 0, 0, 0)
        }
    }

    private fun handleInternetOff() {
        tv_internet.apply {
            text = context.resources.getText(R.string.internet_off)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_off_24dp, 0, 0, 0)
        }
    }

    private fun handleBluetoothOn() {
        tv_bluetooth.apply {
            text = context.resources.getText(R.string.bluetooth_on)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth_on_24dp, 0, 0, 0)
        }
    }

    private fun handleBluetoothOff() {
        tv_bluetooth.apply {
            text = context.resources.getText(R.string.bluetooth_off)
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bluetooth_off_24dp, 0, 0, 0)
        }
    }

    override fun onDestroy() {
        mNetworkConnectionChecker?.removeAllInternetConnectivityChangeListeners()
        mBluetoothConnectionChecker?.unregister()
        super.onDestroy()
    }
}
