package com.example.widget_test

import android.app.Application

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.i("MainApp.onCreate()", ": Schedule alarms")
        AlarmPlanner.scheduleAlarms(this)
    }
}
