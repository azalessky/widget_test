package com.example.widget_test

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.view.View
import android.widget.RemoteViews

object ScheduleWidget {
    fun updateAll(context: Context) {
        Logger.i("ScheduleWidget.updateAll()", "Update all widgets")
        
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, WidgetProvider::class.java)
        )

        val views = buildContent(context)
        ids.forEach { widgetId ->
            manager.updateAppWidget(widgetId, views)
        }
    }

    private fun buildContent(context: Context): RemoteViews {
        val lessons = LessonRepository.getTodayLessons()
        val (status, lesson) = LessonRepository.getActiveLesson()
        val views = RemoteViews(context.packageName, R.layout.schedule_widget)

        if (lessons.isEmpty()) {
            views.setViewVisibility(R.id.status_container, View.GONE)
            views.setViewVisibility(R.id.lesson_list, View.GONE)
            views.setViewVisibility(R.id.empty_text, View.VISIBLE)
        } else {
            buildStatusBar(context, views, lesson, status)
            buildLessonList(context, views, lesson, status, lessons)

            views.setViewVisibility(R.id.empty_text, View.GONE)
            views.setViewVisibility(R.id.status_container, View.VISIBLE)       
            views.setViewVisibility(R.id.lesson_list, View.VISIBLE)
        }

        return views
    }

    private fun buildStatusBar(context: Context, views: RemoteViews, lesson: Lesson?, status: LessonStatus) {
        val (statusText, timeText) = formatStatusText(status, lesson)

        views.setTextViewText(R.id.status_text, statusText)
        views.setTextViewText(R.id.time_text, timeText)
    }

    private fun buildLessonList(context: Context, views: RemoteViews, lesson: Lesson?, status: LessonStatus, lessons: List<Lesson>) {
        val builder = RemoteViews.RemoteCollectionItems.Builder() 
        lessons.forEachIndexed { index, item ->
            val itemViews = buildListItem(context, item)
            builder.addItem(index.toLong(), itemViews)
        }

        val items = builder.setViewTypeCount(1).build()
        views.setRemoteAdapter(R.id.lesson_list, items)

        if (status == LessonStatus.ACTIVE || status == LessonStatus.WAITING) {
            val activeIndex = lessons.indexOf(lesson)
            views.setScrollPosition(R.id.lesson_list, activeIndex)
        }
    }

    private fun buildListItem(context: Context, lesson: Lesson): RemoteViews {
        val start = lesson.start.formatTime()
        val end = lesson.end.formatTime()

        val views = RemoteViews(context.packageName, R.layout.lesson_item)
        views.setTextViewText(R.id.time_text, "$start - $end")
        views.setTextViewText(R.id.subject_text, lesson.subject)

        return views
    }
}