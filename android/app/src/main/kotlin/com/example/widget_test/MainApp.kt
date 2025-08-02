package com.example.widget_test

import android.app.Application

class MainApp : Application() {
    override fun onCreate() {
        Logger.i("MainApp.onCreate()", "Application started")
        super.onCreate()
    }
}
