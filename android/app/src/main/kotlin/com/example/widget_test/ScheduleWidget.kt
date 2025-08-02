package com.example.widget_test

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.LocalDateTime

object ScheduleWidget {
    fun buildContent(context: Context): RemoteViews {
        val now = LocalDateTime.now().stripSeconds()
        val (status, lesson) = LessonRepository.getLessonStatus(now)

        val statusText = getStatusText(status, lesson)

        val views = RemoteViews(context.packageName, R.layout.schedule_widget)
        views.setTextViewText(R.id.status_text, statusText)

        val serviceIntent = Intent(context, LessonListService::class.java)
        @Suppress("DEPRECATION")
        views.setRemoteAdapter(R.id.lesson_list, serviceIntent)

        return views
    }

    fun updateAll(context: Context) {
        Logger.i("ScheduleWidget.updateAll()", "Update all widgets")
        
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, ScheduleWidgetProvider::class.java)
        )

        val views = buildContent(context)
        ids.forEach { widgetId ->
            manager.updateAppWidget(widgetId, views)
            @Suppress("DEPRECATION")
            manager.notifyAppWidgetViewDataChanged(widgetId, R.id.lesson_list)
        }
    }
}