package com.example.widget_test

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class WidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val views = ScheduleWidget.buildContent(context)
        appWidgetIds.forEach { widgetId ->
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}