package com.example.widget_test

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.RemoteViews
import java.time.LocalDateTime

object ScheduleWidget {
    private fun buildContent(context: Context): RemoteViews {
        val lessons = LessonRepository.getTodayLessons()
        val views = RemoteViews(context.packageName, R.layout.schedule_widget)

        if (lessons.isEmpty()) {
            views.setViewVisibility(R.id.status_text, View.GONE)
            views.setViewVisibility(R.id.lesson_list, View.GONE)

            views.setViewVisibility(R.id.empty_text, View.VISIBLE)
            views.setTextViewText(R.id.empty_text, "Нет уроков")
        } else {
            val (status, lesson) = LessonRepository.getActiveLesson()
            val statusText = getStatusText(status, lesson)

            views.setTextViewText(R.id.status_text, statusText)
            views.setViewVisibility(R.id.status_text, View.VISIBLE)
            views.setViewVisibility(R.id.lesson_list, View.VISIBLE)
            views.setViewVisibility(R.id.empty_text, View.GONE)

            if (status == LessonStatus.ACTIVE || status == LessonStatus.WAITING) {
                val activeIndex = lessons.indexOf(lesson)
                views.setScrollPosition(R.id.lesson_list, activeIndex)
            }
            
            val serviceIntent = Intent(context, LessonListService::class.java)
            @Suppress("DEPRECATION")
            views.setRemoteAdapter(R.id.lesson_list, serviceIntent)
        }

        return views
    }

    fun updateAll(context: Context) {
        Logger.i("ScheduleWidget.updateAll()", "Update all widgets")
        
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, WidgetProvider::class.java)
        )

        val views = buildContent(context)
        ids.forEach { widgetId ->
            manager.updateAppWidget(widgetId, views)
            @Suppress("DEPRECATION")
            manager.notifyAppWidgetViewDataChanged(widgetId, R.id.lesson_list)         
        }
    }
}