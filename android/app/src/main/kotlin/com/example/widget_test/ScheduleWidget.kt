package com.example.widget_test

import HomeWidgetGlanceStateDefinition
import android.content.Context
import androidx.core.content.edit
import androidx.compose.runtime.Composable
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ScheduleWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*>
        get() = HomeWidgetGlanceStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { GlanceContent() }
    }

    @Composable
    private fun GlanceContent() {
        val now = LocalDateTime.now().stripSeconds()
        val lessons = LessonRepository.getTodayLessons()
        val (status, lesson) = LessonRepository.getLessonStatus(now)

        Logger.i("ScheduleWidget.GlanceContent()", "Update widget, now = $now")

        Column {
            if (lessons.isNotEmpty()) {
                StatusBar(status, lesson)
                LessonList(lessons)
            } else {
                EmptyPlaceholder()
            }
        }
    }

   fun updateWidgets(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            Logger.i("ScheduleWidget.updateWidgets()", "Trigger widget refresh")

            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(ScheduleWidget::class.java)
            glanceIds.forEach { id ->
                updateAppWidgetState(context, HomeWidgetGlanceStateDefinition(), id ) { state ->
                    state.preferences.edit {
                        putLong("lastUpdated", System.currentTimeMillis())
                    }
                    state
                }
            }
            withContext(Dispatchers.Main) {
                ScheduleWidget().updateAll(context)
            }   
        }
    }
}
