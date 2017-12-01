package com.kamil.connectioncounter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.samsung.android.sdk.b2b.samples.wificlientserverdemo.logDebug

const val HEADPHONES_MAC_ADDRESS = "00:1A:7D:E0:35:5F"
class ConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        logDebug("onReceive: $action")
        when(action){
            BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> processConnectionStateChangedEvent(intent)
            BluetoothAdapter.ACTION_STATE_CHANGED -> processStateChangedEvent(intent)
        }
    }

    private fun processConnectionStateChangedEvent(intent: Intent){
        if(intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE).address != HEADPHONES_MAC_ADDRESS) return

    }

    private fun processStateChangedEvent(intent: Intent){

    }
}
