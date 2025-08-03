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

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BOOT_COMPLETED)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)

        val actions = buildList {
            for (i in 0 until filter.countActions()) {
                val action = filter.getAction(i)
                val value = action.substringAfterLast('.') 
                add(value)
            }
        }
        Logger.i("MainApp.onCreate()", "Registered receiver for ${actions.joinToString(", ")}")
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(receiver)

        Logger.i("MainApp.onTerminate()", "Unregistered receiver")
    }
}
