package com.example.widget_test

import HomeWidgetGlanceStateDefinition
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ScheduleWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*>
        get() = HomeWidgetGlanceStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { GlanceContent() }
    }

    @Composable
    private fun GlanceContent() {
        val now = LocalDateTime.now()
        val lessons = LessonRepository.getTodayLessons()
        val (status, lesson) = LessonRepository.getLessonStatus(now)

        Logger.i("ScheduleWidget.GlanceContent()", "Update widget, now = $now")
        Logger.i("ScheduleWidget.GlanceContent()", "Display lessons, lessons = $lessons")

        Column {
            if (lessons.isNotEmpty()) {
                StatusBar(status, lesson)
                LessonList(lessons)
            } else {
                EmptyPlaceholder()
            }
        }
    }

   fun updateAll(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            Logger.i("ScheduleWidget.updateAll()", "Update widgets")

            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(ScheduleWidget::class.java)

            glanceIds.forEach { glanceId ->
                ScheduleWidget().update(context, glanceId)
            }
        }
    }
}
