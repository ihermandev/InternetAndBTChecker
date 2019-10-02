package com.illiaherman.connection.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast


/**
 * For init give context to listener: 1.BluetoothConnectionChecker(context), 2. Register a callback - register(callback)
 * Created by illia.herman on 02.10.2019
 */
class BluetoothConnectionChecker(private val mContext: Context) {
    private var mConnectionListener: BluetoothConnectionListener? = null
    private var mReceiver: BluetoothReceiver? = null

    /**
     * Register this BT connectionListener to the context with a connectionListener.
     */
    fun register(connectionListener: BluetoothConnectionListener) {
        mConnectionListener = connectionListener

        mConnectionListener?.isBluetoothAvailable(checkBluetoothState())

        if (mReceiver == null) {
            mReceiver = BluetoothReceiver()
            val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            mContext.registerReceiver(mReceiver, filter)
        }
    }

    /**
     * Unregister this BT
     */
    fun unregister() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver)
            mReceiver = null
        }
    }

    /**
     * Receives the [android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED]
     */
    private inner class BluetoothReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR
            )
            when (state) {
                BluetoothAdapter.STATE_TURNING_ON -> mConnectionListener?.isBluetoothAvailable(true)
                BluetoothAdapter.STATE_TURNING_OFF -> mConnectionListener?.isBluetoothAvailable(false)
            }
        }
    }

    /**
     * Check BR current state if Bluetooth Adapter is missed return toast with corresponding info
     */
    private fun checkBluetoothState(): Boolean {
        return if (BluetoothAdapter.getDefaultAdapter() == null) {
            Toast.makeText(mContext, "BluetoothAdapter is missed", Toast.LENGTH_SHORT).show()
            false
        } else BluetoothAdapter.getDefaultAdapter().isEnabled
    }

}