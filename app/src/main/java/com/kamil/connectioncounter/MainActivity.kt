package com.kamil.connectioncounter

import android.app.AlertDialog
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
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_set_time.*

const val BOLD_START_TAG = "<b>"
const val BOLD_END_TAG = "</b>"
const val NEW_LINE_TAG = "<br>"
class MainActivity : AppCompatActivity() {
    private lateinit var preferencesController: PreferencesController
    private lateinit var logController: LogController
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
        preferencesController = PreferencesController(this)
        logController = LogController(this)
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, ConnectionMonitorService::class.java),
                connection, 0)
        if(!bound){
            val (playingDuration, standbyDuration) = preferencesController.getDurations()
            updateDurationTextView(playingDuration, standbyDuration)
        }
    }

    private fun startUpdatingTextView() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val (playingDuration, standbyDuration) = service.getDurations()
                updateDurationTextView(playingDuration, standbyDuration)
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
                .create().show()
    }

    private fun clearConnectionDurations() {
        toast("Time cleared")
        preferencesController.saveDuration(0, 0)
        if (bound) service.resetTimer()
        updateDurationTextView(0, 0)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        logController.clearLog()
    }

    private fun handleSetAction() {
        val layout = inflate(R.layout.dialog_set_time)
        AlertDialog.Builder(this).setTitle("Set playing time")
                .setView(layout)
                .setPositiveButton("Confirm", { _, _ -> setPlayingTime() })
                .setNegativeButton("Cancel", { _, _ -> toast("Cancelled")})
                .create().show()
    }

    private fun setPlayingTime() {
        var value = if (timeEditText.isEmpty()) 0 else timeEditText.trimmedText().toLong() * 60
        if (setTimeRadioButton.isChecked) {
            value += if (bound) service.getDurations().playingDuration else preferencesController.getPlayingDuration()
            logController.saveToLog("Added $value min")
        } else {
            logController.clearLog()
            logController.saveToLog("Set $value min")
        }
        preferencesController.savePlayingDuration(value)
        toast("Time updated")
        updateDurationTextView(value, if (bound) service.getDurations().standbyDuration else preferencesController.getStandbyDuration())
    }

    private fun handleLogAction() {
        val logs = logController.getLogs()
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
        AlertDialog.Builder(this).setTitle("Log").setMessage(logs).create().show()
    }
}
