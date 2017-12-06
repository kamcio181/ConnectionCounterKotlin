package com.kamil.connectioncounter

import android.app.Application

val sharedPreferences: PreferencesController by lazy {
    com.kamil.connectioncounter.Application.sharedPreferences!!
}

val logController: LogController by lazy {
    com.kamil.connectioncounter.Application.logs!!
}

class Application: Application(){
    companion object {
        var sharedPreferences: PreferencesController? = null
        var logs: LogController? = null
    }

    override fun onCreate() {
        sharedPreferences = PreferencesController(applicationContext)
        logs = LogController(applicationContext)
        super.onCreate()
    }
}
