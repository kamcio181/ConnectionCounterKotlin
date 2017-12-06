package com.kamil.connectioncounter

import android.content.Context
import android.content.SharedPreferences

const val PREFERENCES_NAME = "MainPreferences"
const val PLAYING_DURATION = "PlayingDuration"
const val STANDBY_DURATION = "StandbyDuration"
class PreferencesController (context: Context){
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getPlayingDuration(): Long {
        return sharedPreferences.getLong(PLAYING_DURATION, 0)
    }

    fun getStandbyDuration(): Long {
        return sharedPreferences.getLong(STANDBY_DURATION, 0)
    }

    data class Durations(val playingDuration: Long, val standbyDuration: Long)
    fun getDurations(): Durations{
        return Durations(getPlayingDuration(), getStandbyDuration())
    }

    fun saveDuration(playingDuration: Long, standbyDuration: Long){
        editor.putLong(PLAYING_DURATION, playingDuration)
                .putLong(STANDBY_DURATION, standbyDuration).apply()
    }

    fun savePlayingDuration(playingDuration: Long){
        editor.putLong(PLAYING_DURATION, playingDuration).apply()
    }
}