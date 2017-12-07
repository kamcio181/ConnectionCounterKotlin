package com.kamil.connectioncounter

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_set_time.view.*

const val BOLD_START_TAG = "<b>"
const val BOLD_END_TAG = "</b>"
const val NEW_LINE_TAG = "<br>"
class MainActivity : AppCompatActivity() {
    private lateinit var service: ConnectionMonitorService
    private val handler = Handler()
    private var bound = false
    private val connection = object: ServiceConnection{
        override fun onServiceDisconnected(p0: ComponentName) {
            bound = false
            handler.removeCallbacksAndMessages(null)
        }

        override fun onServiceConnected(p0: ComponentName, p1: IBinder) {
            val binder = p1 as ConnectionMonitorService.LocalBinder
            service = binder.service
            bound = true
            startUpdatingTextView()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, ConnectionMonitorService::class.java),
                connection, 0)
        if(!bound){
            updateDurationTextView(sharedPreferences.playingDuration, sharedPreferences.standbyDuration)
        }
    }

    private fun startUpdatingTextView() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateDurationTextView(service.playingDuration, service.standbyDuration)
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        bound = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_reset -> handleResetAction()
            R.id.action_set -> handleSetAction()
            R.id.action_log -> handleLogAction()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleResetAction() {
        AlertDialog.Builder(this).setTitle("Do you want to reset timer?")
                .setPositiveButton("Confirm", { _, _ -> clearConnectionDurations() })
                .setNegativeButton("Cancel", { _, _ -> toast("Cancelled")})
                .setCancelable(false)
                .create().show()
    }

    private fun clearConnectionDurations() {
        toast("Time cleared")
        sharedPreferences.durations(0,0)
        if (bound) service.resetTimer()
        updateDurationTextView(0, 0)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        logController.activityLog = ""
    }

    private fun handleSetAction() {
        val layout = inflate(R.layout.dialog_set_time)
        AlertDialog.Builder(this).setTitle("Set playing time")
                .setView(layout)
                .setPositiveButton("Confirm", { _, _ -> setPlayingTime(layout) })
                .setNegativeButton("Cancel", { _, _ -> toast("Cancelled")})
                .setCancelable(false)
                .create().show()
    }

    private fun setPlayingTime(layout: View) {
        var value = if (layout.timeEditText.isEmpty()) 0 else layout.timeEditText.trimmedText().toLong()
        if (layout.addTimeRadioButton.isChecked) {
            logController.activityLog = "Added $value min"
            value *= 60
            if(bound){
                value += service.playingDuration
                service.playingDuration = value
            } else {
                value += sharedPreferences.playingDuration
            }

        } else {
            logController.activityLog = ""
            logController.activityLog = "Set $value min"
            value *= 60
        }
        sharedPreferences.playingDuration = value //TODO is it needed here?
        toast("Time updated")
        updateDurationTextView(value, if (bound) service.standbyDuration else sharedPreferences.standbyDuration)
    }

    private fun handleLogAction() {
        val logs = logController.activityLog
        if(logs.isEmpty()){
            toast("Logs empty")
            return
        }
        showLogDialog(logs)
    }

    private fun updateDurationTextView(playingDuration: Long, standbyDuration: Long){
        durationTextView.text = composeText(playingDuration, standbyDuration)
    }

    private fun composeText(playingDuration: Long, standbyDuration: Long): Spanned {
        val htmlText = "${BOLD_START_TAG}Total: $BOLD_END_TAG${formatDuration(playingDuration + standbyDuration)}" +
                "$NEW_LINE_TAG${BOLD_START_TAG}Playing: $BOLD_END_TAG${formatDuration(playingDuration)}" +
                "$NEW_LINE_TAG${BOLD_START_TAG}Standby: $BOLD_END_TAG${formatDuration(standbyDuration)}"
        return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            Html.fromHtml(htmlText)
        } else {
            Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
        }
    }

    private fun showLogDialog(logs: String) {
        val dialog = AlertDialog.Builder(this).setTitle("Log").setMessage(logs).create()
        dialog.show()
        val textView = dialog.findViewById<TextView>(android.R.id.message)
        textView.textSize = 12f
    }
}
