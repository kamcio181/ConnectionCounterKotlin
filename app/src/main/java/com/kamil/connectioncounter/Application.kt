package com.kamil.connectioncounter

import android.app.Application

val sharedPreferences: PreferencesController by lazy {
    com.kamil.connectioncounter.Application.sharedPreferences!!
}

class Application: Application(){
    companion object {
        var sharedPreferences: PreferencesController? = null
    }

    override fun onCreate() {
        sharedPreferences = PreferencesController(applicationContext)
        super.onCreate()
    }
}
