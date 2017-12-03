package com.kamil.connectioncounter

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

const val LOG_NAME = "Log.txt"
class LogController (private val context: Context){

    fun saveDurationToLog(isPlaying: Boolean, startTime: Long, stopTime:Long){
        val message = if(isPlaying) "Playing " else "Standby " +
                "${formatDateAndTime(startTime)} - ${formatDateAndTime(stopTime)}" +
                formatDifferenceInMinutes(stopTime - startTime)
        saveToLog(message)
    }

    private fun formatDateAndTime(time: Long): String{
        val calendar = GregorianCalendar().apply { timeInMillis = time }
        return String.format("%1\$te/%1\$tm/%1\$tY %1\$tT", calendar)
    }

    private fun formatDifferenceInMinutes(difference: Long): String{
        return String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(difference))
    }

    fun saveToLog(message: String){
        val outputStream = FileOutputStream(File(context.filesDir, LOG_NAME), true)
        outputStream.write(message.toByteArray())
        outputStream.close()
    }
}