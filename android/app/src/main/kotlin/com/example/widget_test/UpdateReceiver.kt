package com.example.widget_test

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            Logger.i("UpdateReceiver.onReceive()", "Update state")

            LessonRepository.loadLessons(context)
            ScheduleWidget.updateAll(context)
            AlarmPlanner.scheduleAlarms(context)
        }
    }
}
