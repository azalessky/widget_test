package com.example.widget_test

import android.app.Application
import android.util.Log
import android.os.Handler
import android.os.Looper

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("MainApp", "Запускаем цикл обновления виджетов")
        WidgetAlarmScheduler.scheduleNextCycle(this)
    }
}
