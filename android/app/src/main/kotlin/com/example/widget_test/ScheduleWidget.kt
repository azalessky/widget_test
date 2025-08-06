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
            try {
                manager.updateAppWidget(widgetId, views)
            } catch (e: Exception) {
                Logger.e("ScheduleWidget.updateAll()", "Cannot update widget", e)
            }
        }
    }

    private fun buildContent(context: Context): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.schedule_widget)
        val lessons = LessonRepository.getRemainingLessons()
        val schedule = LessonRepository.getScheduleState()
        val hasLessons = lessons.isNotEmpty()

        if (hasLessons) {
            buildStatusBar(views, schedule)
            buildLessonList(context, views, schedule, lessons)
        } else {
            buildPlaceholder(views, schedule)
        }
        showDataContent(views, hasLessons)

        return views
    }

    private fun showDataContent(views: RemoteViews, hasLessons: Boolean){
        val showPlaceholder = if (hasLessons) View.GONE else View.VISIBLE
        val showData = if (hasLessons) View.VISIBLE else View.GONE

        views.setViewVisibility(R.id.placeholder_text, showPlaceholder)
        views.setViewVisibility(R.id.status_text, showData)
        views.setViewVisibility(R.id.lesson_list, showData)
    }

    private fun buildPlaceholder(views: RemoteViews, schedule: ScheduleState) {
        views.setTextViewText(R.id.placeholder_text, schedule.getStatusText())
    }

    private fun buildStatusBar(views: RemoteViews, schedule: ScheduleState) {
        val text = schedule.getStatusText()
        views.setTextViewText(R.id.status_text, text)
    }

    private fun buildLessonList(context: Context, views: RemoteViews, schedule: ScheduleState, lessons: List<Lesson>) {
        val builder = RemoteViews.RemoteCollectionItems.Builder()
        var activeIndex: Int? = null

        lessons.forEachIndexed { index, lesson ->
            val status = LessonRepository.getLessonStatus(lesson)
            val view = buildListItem(context, lesson, status)
            builder.addItem(index.toLong(), view)

            if (lesson == schedule.lesson &&
                schedule.status == ScheduleStatus.RUNNING ||
                schedule.status == ScheduleStatus.WAITING) {
                activeIndex = index
            }
        }

        val items = builder.setViewTypeCount(1).build()
        views.setRemoteAdapter(R.id.lesson_list, items)

        activeIndex?.let {
            views.setScrollPosition(R.id.lesson_list, it)
        }
    }

    private fun buildListItem(context: Context, lesson: Lesson, status: LessonStatus): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.lesson_item)
        val timeText = "${lesson.start.formatTime()} - ${lesson.end.formatTime()}"
        views.setTextViewText(R.id.time_text, timeText)  
        views.setTextViewText(R.id.subject_text, lesson.subject)


        val colors = ColorsProvider(context)
        val textColor = when (status) {
            LessonStatus.PLANNED -> colors.colorOnSurface
            LessonStatus.ACTIVE -> colors.colorOnTertiaryContainer
            LessonStatus.COMPLETED -> colors.colorOnSurface
        }
        val background = when (status) {
            LessonStatus.PLANNED -> R.drawable.planned_background
            LessonStatus.ACTIVE -> R.drawable.active_background
            LessonStatus.COMPLETED -> R.drawable.completed_background
        }

        views.setImageViewResource(R.id.background_image, background)
        views.setInt(R.id.time_text, "setTextColor", textColor)
        views.setInt(R.id.subject_text, "setTextColor", textColor)

        return views    
    }
}