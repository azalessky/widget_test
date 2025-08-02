package com.example.widget_test

import HomeWidgetGlanceStateDefinition
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class ScheduleWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*>
        get() = HomeWidgetGlanceStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = getAppWidgetState(context, HomeWidgetGlanceStateDefinition(), id).preferences
        val reason = prefs.getString("updateReason", null) ?: "unknown"
        val now = LocalDateTime.now().stripSeconds()

        provideContent { GlanceContent(now, reason) }
    }

    @Composable
    private fun GlanceContent(time: LocalDateTime, reason: String) {
        val lessons = LessonRepository.getTodayLessons()
        val (status, lesson) = LessonRepository.getLessonStatus(time)

        Logger.i("ScheduleWidget.GlanceContent()", "Update widget, time = $time, reason = $reason")

        val context = LocalContext.current
        val glanceId = LocalGlanceId.current

        val prefs = runBlocking {
            getAppWidgetState(context, HomeWidgetGlanceStateDefinition(), glanceId)
        }

        val lastUpdated = prefs.preferences.getLong("lastUpdated", 0L)

        val timestamp = if (lastUpdated > 0) {
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(lastUpdated))
        } else {
            "never"
        }

        Column {
            if (lessons.isNotEmpty()) {
                Text(text = "Updated: $timestamp", style = TextStyle(fontSize = 12.sp))
                StatusBar(status, lesson)
                LessonList(lessons)
            } else {
                EmptyPlaceholder()
            }
        }
    }
}
