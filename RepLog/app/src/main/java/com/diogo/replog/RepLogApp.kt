package com.diogo.replog

import android.app.Application
import android.util.Log

class RepLogApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("RepLogCrash", "FATAL EXCEPTION in thread ${thread.name}", throwable)
        }

        Log.d("RepLogApp", "Application Created")
    }
}
