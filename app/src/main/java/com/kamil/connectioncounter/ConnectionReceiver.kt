package com.kamil.connectioncounter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

const val HEADPHONES_MAC_ADDRESS = "00:1A:7D:E0:35:5F"
class ConnectionReceiver : BroadcastReceiver() {
    private lateinit var serviceIntent: Intent
    override fun onReceive(context: Context, intent: Intent) {
        serviceIntent = Intent(context, ConnectionMonitorService::class.java)
        val action = intent.action
        logDebug("onReceive: $action")
        when(action){
            BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> processConnectionStateChangedEvent(context, intent)
            BluetoothAdapter.ACTION_STATE_CHANGED -> processStateChangedEvent(context, intent)
        }
    }

    private fun processConnectionStateChangedEvent(context: Context, intent: Intent){
        if(intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE).address
                != HEADPHONES_MAC_ADDRESS) {
            logDebug("Not target device")
            return
        }

        val currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1)
        val previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, -1)
        logDebug("Current state $currentState, previous state $previousState")

        if(currentState == previousState){
            logDebug("State not changed")
            return
        }

        if(currentState == BluetoothAdapter.STATE_CONNECTED){
            logDebug("Target device connected")
            context.startService(serviceIntent)
        } else if (currentState == BluetoothAdapter.STATE_DISCONNECTED) {
            logDebug("Target device disconnected")
            context.stopService(serviceIntent)
        }

    }

    private fun processStateChangedEvent(context: Context, intent: Intent){
        val currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
        if(currentState == BluetoothAdapter.STATE_OFF){
            logDebug("Bluetooth turned off")
            context.stopService(serviceIntent)
        }
    }
}
