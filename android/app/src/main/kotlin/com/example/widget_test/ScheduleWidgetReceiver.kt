package com.example.widget_test

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScheduleWidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Logger.i("ScheduleWidgetReceiver.onReceive()", "action = ${intent.action}")

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE || 
            intent.action == Intent.ACTION_BOOT_COMPLETED
        ) {
            Logger.i("ScheduleWidgetReceiver.onReceive()", "Update data/widget/alarms")
            
            LessonRepository.loadLessons(context)
            ScheduleWidget.updateAll(context)
            AlarmPlanner.scheduleAlarms(context)
        }
    }
}
