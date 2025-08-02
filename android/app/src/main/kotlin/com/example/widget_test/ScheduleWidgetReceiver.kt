package com.example.widget_test

import HomeWidgetGlanceWidgetReceiver
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent

class ScheduleWidgetReceiver : HomeWidgetGlanceWidgetReceiver<ScheduleWidget>() {
    override val glanceAppWidget = ScheduleWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Logger.i("ScheduleWidgetReceiver.onReceive()", "action = ${intent.action}")

        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE || 
            intent.action == Intent.ACTION_BOOT_COMPLETED
        ) {
            Logger.i("ScheduleWidgetReceiver.onReceive()", "Update data/widget/alarms")
            
            LessonRepository.loadLessons(context)
            WidgetRefresher.requestUpdate(context,"data")
            AlarmPlanner.scheduleAlarms(context)
        }
    }
}
