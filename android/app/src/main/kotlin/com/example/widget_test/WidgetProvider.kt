package com.example.widget_test

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Logger.i("WidgetProvider.onUpdate()", "Update widgets #${appWidgetIds.joinToString(", ")}")

        LessonRepository.loadLessons(context)
        AlarmPlanner.scheduleAlarms(context)

        val views = ScheduleWidget.buildContent(context)
        appWidgetIds.forEach { widgetId ->
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}