package com.example.widget_test

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat

object ScheduleWidget {
    fun updateAll(context: Context) {
        Logger.i("ScheduleWidget.updateAll()", "Update all widgets")
        
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, WidgetProvider::class.java)
        )

        val views = buildContent(context)
        ids.forEach { widgetId ->
            try {
                manager.updateAppWidget(widgetId, views)
            } catch (e: Exception) {
                Logger.e("ScheduleWidget.updateAll()", "Cannot update widget", e)
            }
        }
    }

    private fun buildContent(context: Context): RemoteViews {
        val lessons = LessonRepository.getTodayLessons()
        val (status, lesson) = LessonRepository.getActiveLesson()
        val views = RemoteViews(context.packageName, R.layout.schedule_widget)
        val hasLessons = lessons.isNotEmpty()

        if (hasLessons) {
            buildStatusBar(views, lesson, status)
            buildLessonList(context, views, lesson, status, lessons)
        }
        showEmptyText(views, !hasLessons)

        return views
    }

    private fun showEmptyText(views: RemoteViews, show: Boolean) {
        val showEmpty = if (show) View.VISIBLE else View.GONE
        val showContent = if (show) View.GONE else View.VISIBLE

        views.setViewVisibility(R.id.status_container, showContent)
        views.setViewVisibility(R.id.lesson_list, showContent)
        views.setViewVisibility(R.id.empty_text, showEmpty)
    }

    private fun buildStatusBar(views: RemoteViews, activeLesson: Lesson?, status: LessonStatus) {
        val (statusText, timeText) = formatStatusText(status, activeLesson)

        views.setTextViewText(R.id.status_text, statusText)
        views.setTextViewText(R.id.time_text, timeText)
    }

    private fun buildLessonList(context: Context, views: RemoteViews, activeLesson: Lesson?, status: LessonStatus, lessons: List<Lesson>) {
        val builder = RemoteViews.RemoteCollectionItems.Builder()
        lessons.forEachIndexed { index, lesson ->
            val selected = lesson == activeLesson && status == LessonStatus.ACTIVE
            val itemViews = buildListItem(context, lesson, index + 1, selected)
            builder.addItem(index.toLong(), itemViews)
        }

        val items = builder.setViewTypeCount(1).build()
        views.setRemoteAdapter(R.id.lesson_list, items)

        if (status == LessonStatus.ACTIVE || status == LessonStatus.WAITING) {
            val activeIndex = lessons.indexOf(activeLesson)
            views.setScrollPosition(R.id.lesson_list, activeIndex)
        }
    }

    private fun buildListItem(context: Context, lesson: Lesson, number: Int, selected: Boolean): RemoteViews {
        val start = lesson.start.formatTime()
        val end = lesson.end.formatTime()

        val views = RemoteViews(context.packageName, R.layout.lesson_item)
        views.setTextViewText(R.id.number_text, number.toString())      
        views.setTextViewText(R.id.time_text, "$start - $end")
        views.setTextViewText(R.id.subject_text, lesson.subject)

        val colors = ColorsProvider(context)
        val textColor = if (selected) colors.selectedItemText else colors.itemText
        val bgColor = if (selected) colors.selectedItemBackground else colors.itemBackground

        views.setInt(R.id.item_container, "setBackgroundColor", bgColor)
        views.setInt(R.id.number_text, "setTextColor", textColor)
        views.setInt(R.id.time_text, "setTextColor", textColor)
        views.setInt(R.id.subject_text, "setTextColor", textColor)

        return views    
    }
}