package com.example.widget_test

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class MainApp : Application() {
    private val receiver by lazy { ActionReceiver() }

    override fun onCreate() {
        super.onCreate()

        val actions = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_CONFIGURATION_CHANGED
        )
        val filter = IntentFilter().apply {
            actions.forEach { addAction(it) }
        }
        registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        
        Logger.i("MainApp.onCreate()", "Registered receiver for ${actions.joinToString(", ")}")
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(receiver)

        Logger.i("MainApp.onTerminate()", "Unregistered receiver")
    }
}
