package com.example.widget_test

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class LessonListService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return LessonListFactory(applicationContext)
    }
}

class LessonListFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var lessons: List<Lesson> = emptyList()

    override fun getCount(): Int {
        return lessons.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val lesson = lessons[position]
        val start = lesson.start.formatTime()
        val end = lesson.end.formatTime()

        val views = RemoteViews(context.packageName, R.layout.lesson_item)
        views.setTextViewText(R.id.time_text, "$start - $end")
        views.setTextViewText(R.id.subject_text, lesson.subject)
        
        return views
    }

    override fun onDataSetChanged() {
        lessons = LessonRepository.getTodayLessons()
        Logger.i("LessonListFactory.onDataSetChanged()", "Updated lessons = ${lessons.size}")
    }

    override fun onCreate() {}
    override fun onDestroy() {}
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
}