package com.kamil.connectioncounter

import android.content.Context
import java.io.*
import java.util.*
import java.util.concurrent.TimeUnit

const val LOG_NAME = "Log.txt"
class LogController (context: Context){
    private val activityLogFile: File by lazy { File(context.filesDir, LOG_NAME) }

    var activityLog: String
        get() = getLogs()
        set(value) = saveToLog(value)

    fun saveDurationToLog(isPlaying: Boolean, startTime: Long, stopTime:Long){
        val message = (if(isPlaying) "Playing " else "Standby ") +
                "${formatDateAndTime(startTime)} - ${formatDateAndTime(stopTime)}" + " "
                formatDifferenceInMinutes(stopTime - startTime)
        activityLog = message
    }

    private fun formatDateAndTime(time: Long): String{
        val calendar = GregorianCalendar().apply { timeInMillis = time }
        return String.format("%1\$te/%1\$tm/%1\$tY %1\$tT", calendar)
    }

    private fun formatDifferenceInMinutes(difference: Long): String{
        return String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(difference))
    }

    private fun saveToLog(message: String){
        if(message.isEmpty()){
            clearLog()
            return
        }

        val outputStream = FileOutputStream(activityLogFile, true)
        outputStream.write(message.toByteArray())
        outputStream.write("\n".toByteArray())
        outputStream.close()
    }

    private fun clearLog(){
        if(activityLogFile.exists()) activityLogFile.delete()
    }

    private fun getLogs(): String{
        if(!activityLogFile.exists()){
            return String()
        }
        val input = BufferedReader(InputStreamReader(FileInputStream(activityLogFile)))
        val buffer = StringBuffer()
        var line = input.readLine()
        while (line != null) {
            buffer.append(line).append("\n")
            line = input.readLine()
        }
        return buffer.toString().trim()
    }
}