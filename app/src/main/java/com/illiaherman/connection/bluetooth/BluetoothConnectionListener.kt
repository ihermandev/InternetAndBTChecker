package com.illiaherman.connection.bluetooth

/**
 * Interface definition for a callback when bluetooth state changed
 * Created by illia.herman on 02.10.2019
 */
interface BluetoothConnectionListener {
    /**
     * Called when the BT turning on/off also for save current BT state when try to register listener
     */
    fun isBluetoothAvailable(isBluetoothTurnOn: Boolean)

}