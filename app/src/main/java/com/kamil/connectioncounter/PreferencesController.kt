package com.kamil.connectioncounter

import android.content.Context
import android.content.SharedPreferences

private const val PREFERENCES_NAME = "MainPreferences"
private const val PLAYING_DURATION = "PlayingDuration"
private const val STANDBY_DURATION = "StandbyDuration"
class PreferencesController (context: Context){
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor by lazy{
        sharedPreferences.edit()
    }

    var playingDuration: Long
        get() = sharedPreferences.getLong(PLAYING_DURATION, 0)
        set(value) = editor.putLong(PLAYING_DURATION, value).apply()

    var standbyDuration: Long
        get() = sharedPreferences.getLong(STANDBY_DURATION, 0)
        set(value) = editor.putLong(STANDBY_DURATION, value).apply()

    fun durations(playingDuration: Long, standbyDuration: Long){
        editor.putLong(PLAYING_DURATION, playingDuration)
                .putLong(STANDBY_DURATION, standbyDuration).apply()
    }

//    fun getPlayingDuration(): Long {
//        return sharedPreferences.getLong(PLAYING_DURATION, 0)
//    }
//
//    fun getStandbyDuration(): Long {
//        return sharedPreferences.getLong(STANDBY_DURATION, 0)
//    }
//
//    data class Durations(val playingDuration: Long, val standbyDuration: Long)
//    fun getDurations(): Durations{
//        return Durations(getPlayingDuration(), getStandbyDuration())
//    }
//

//
//    fun savePlayingDuration(playingDuration: Long){
//        editor.putLong(PLAYING_DURATION, playingDuration).apply()
//    }
}