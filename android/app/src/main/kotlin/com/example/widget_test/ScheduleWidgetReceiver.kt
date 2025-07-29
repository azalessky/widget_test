package com.example.widget_test

import HomeWidgetGlanceWidgetReceiver
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log

class ScheduleWidgetReceiver : HomeWidgetGlanceWidgetReceiver<ScheduleWidget>() {
    override val glanceAppWidget = ScheduleWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.i("ScheduleWidgetReceiver", "Получено событие: ${intent.action}")

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            Log.i("ScheduleWidgetReceiver", "APPWIDGET_UPDATE — загружаем уроки")
            LessonRepository.loadLessons(context)
        }
  
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("ScheduleWidgetReceiver", "BOOT_COMPLETED — переназначаем будильник")
            // TODO: Uncomment the line below to schedule the next cycle
           // AlarmPlanner.scheduleNextCycle(context)
        }
    }
}
