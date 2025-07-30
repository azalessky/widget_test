package com.example.widget_test

import HomeWidgetGlanceStateDefinition
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration
import java.time.LocalDate

class ScheduleWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*>
        get() = HomeWidgetGlanceStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {        
        val lessons = LessonRepository.getLessons()
        val todayLessons = lessons.filter { it.start.toLocalDate() == LocalDate.now() }
        provideContent { GlanceContent(todayLessons) }
    }

    @Composable
    private fun GlanceContent(lessons: List<Lesson>) {  
        val now = LocalDateTime.now()
        val (status, lesson) = LessonRepository.getLessonStatus(now)

        Log.i("ScheduleWidget", "Updating widget at: $now")
        Log.i("ScheduleWidget", "Displaying lessons: $lessons")

        Column(modifier = GlanceModifier.fillMaxSize().padding(8.dp)) {
            if (lessons.isEmpty()) {
                EmptyPlaceholder()
            } else {
                when (status) {
                    LessonStatus.ACTIVE -> {
                        val minsLeft = Duration.between(now, lesson!!.end).toMinutes()
                        StatusBar("Идёт урок", minsLeft.toInt(), lesson)
                    }
                    LessonStatus.WAITING -> {
                        val minsToStart = Duration.between(now, lesson!!.start).toMinutes()
                        StatusBar("Следующий урок", minsToStart.toInt(), lesson)
                    }
                    LessonStatus.NONE -> {
                        StatusBar("Нет уроков", null, null)
                    }
                }
                LessonList(lessons)
            }
        }
    }
}
