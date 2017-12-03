package com.kamil.connectioncounter

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import kotlinx.android.synthetic.main.activity_main.*

const val BOLD_START_TAG = "<b>"
const val BOLD_END_TAG = "</b>"
const val NEW_LINE_TAG = "<br>"
class MainActivity : AppCompatActivity() {
    private lateinit var preferencesController: PreferencesController
    private lateinit var service: ConnectionMonitorService
    private var bound = false
    private val connection = object: ServiceConnection{
        override fun onServiceDisconnected(p0: ComponentName) {
            bound = false
        }

        override fun onServiceConnected(p0: ComponentName, p1: IBinder) {
            val binder = p1 as ConnectionMonitorService.LocalBinder
            service = binder.service
            bound = true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferencesController = PreferencesController(this)
    }

    override fun onStart() { //TODO
        super.onStart()
        val (playingDuration, standbyDuration) = preferencesController.getDurations()
        updateDurationTextView(playingDuration, standbyDuration)
        bindService(Intent(this, ConnectionMonitorService::class.java),
                connection, 0)
        //TODO start getting time
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        bound = false
    }

    private fun updateDurationTextView(playingDuration: Long, standbyDuration: Long){
        durationTextView.text = composeText(playingDuration, standbyDuration)
    }



    private fun composeText(playingDuration: Long, standbyDuration: Long): Spanned {
        val htmlText = "${BOLD_START_TAG}Total: $BOLD_END_TAG${formatDuration(playingDuration + standbyDuration)}"
                "$NEW_LINE_TAG${BOLD_START_TAG}Playing: $BOLD_END_TAG${formatDuration(playingDuration)}" +
                "$NEW_LINE_TAG${BOLD_START_TAG}Standby: $BOLD_END_TAG${formatDuration(standbyDuration)}"
        return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            Html.fromHtml(htmlText)
        } else {
            Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
        }
    }
}
