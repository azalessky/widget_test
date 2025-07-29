package com.example.widget_test

import HomeWidgetGlanceWidgetReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class HomeWidgetReceiver : HomeWidgetGlanceWidgetReceiver<HomeWidget>() {
    override val glanceAppWidget = HomeWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("HomeWidgetReceiver", "BOOT_COMPLETED — переназначаем будильник")
            WidgetAlarmScheduler.scheduleNextCycle(context)
        }
    }
}
