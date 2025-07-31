package com.example.widget_test

import android.app.Application
import android.util.Log

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Log.i("MainApp", "onCreate(): Schedule alarms")
        AlarmPlanner.scheduleAlarms(this)
    }
}
