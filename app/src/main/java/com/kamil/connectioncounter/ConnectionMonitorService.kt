package com.kamil.connectioncounter

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Binder
import android.os.Handler
import android.os.IBinder

const val NOTIFICATION_ID = 1
class ConnectionMonitorService : Service() {
    private val binder: IBinder = LocalBinder()
    private val handler = Handler()
    private lateinit var logController: LogController
    private lateinit var audioManager: AudioManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: Notification.Builder
    var playingDuration: Long = 0
    var standbyDuration: Long = 0
    private var startTime: Long = 0
    private var headsetConnected = false
    private var wasScreenOnPreviously = false
    private var wasPlaying = false

    inner class LocalBinder: Binder() {
        val service: ConnectionMonitorService
            get() = this@ConnectionMonitorService
    }

    override fun onBind(intent: Intent): IBinder {
        logDebug("onBind")
        return binder
    }

    override fun onCreate() {
        logDebug("onCreate")
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logDebug("onStartCommand")
        setUpClassFields()
        setUpNotification()

        startWorkingRunnable()
        return START_STICKY
    }

    private fun setUpClassFields() {
        logController = LogController(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = Notification.Builder(this) //TODO handle deprecation
        startTime = System.currentTimeMillis()
        wasPlaying = audioManager.isMusicActive
        playingDuration = sharedPreferences.playingDuration
        standbyDuration = sharedPreferences.standbyDuration
        headsetConnected = true  //TODO no needed if bind service does not start it
    }

    private fun setUpNotification() {
        val startActivityIntent = Intent(this, MainActivity::class.java)
        val startActivityPendingIntent = PendingIntent.getActivity(this, 0,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(startActivityPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
    }

    private fun startWorkingRunnable() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(object : Runnable {
            override fun run() {
                val isScreenOn = isScreenOn()
                val isMusicActive = audioManager.isMusicActive
                if (isMusicActive) {
                    playingDuration++
                    showNotificationIfNeeded(playingDuration, isScreenOn)
                } else {
                    standbyDuration++
                    showNotificationIfNeeded(standbyDuration, isScreenOn)
                }


                if (isMusicActive != wasPlaying) {
                    logController.saveDurationToLog(wasPlaying, startTime, System.currentTimeMillis())
                    startTime = System.currentTimeMillis()
                }

                if (isScreenOn && !wasScreenOnPreviously) {
                    notifyImmediately(false)
                }

                wasScreenOnPreviously = isScreenOn
                wasPlaying = isMusicActive
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun showNotificationIfNeeded(duration:Long, isScreenOn: Boolean) {
        if (isScreenOn && duration % 60 == 0L) {
            notifyImmediately(false)
        }
    }

    private fun notifyImmediately(addSeconds: Boolean){
        notificationBuilder.setStyle(Notification.BigTextStyle().bigText(composeNotificationContent(addSeconds)))
                .setContentTitle(composeNotificationTitle(addSeconds))
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun composeNotificationTitle(addSeconds: Boolean): String{
        return "Total: ${formatDuration(playingDuration + standbyDuration, addSeconds)}"
    }

    private fun composeNotificationContent(addSeconds: Boolean): String{
        return "Playing: ${formatDuration(playingDuration, addSeconds)}" +
                "\nStandby: ${formatDuration(standbyDuration, addSeconds)}"
    }

    override fun onDestroy() {
        logDebug("onDestroy")
        super.onDestroy()

        handler.removeCallbacksAndMessages(null)
        notificationBuilder.setOngoing(false)
        notifyImmediately(true)
        sharedPreferences.durations(playingDuration, standbyDuration)
    }

    fun resetTimer(){
        logDebug("resetTimer")
        playingDuration = 0
        standbyDuration = 0
    }
}
