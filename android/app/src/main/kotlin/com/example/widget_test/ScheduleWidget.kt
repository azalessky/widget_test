package com.example.widget_test

import HomeWidgetGlanceStateDefinition
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Duration

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

        Logger.i("ScheduleWidget.GlanceContent()", "Updating widget, now = $now")
        Logger.i("ScheduleWidget.GlanceContent()", "Display lessons, lessons = $lessons")

        Column(modifier = GlanceModifier.fillMaxSize().padding(8.dp)) {
            if (lessons.isEmpty()) {
                EmptyPlaceholder()
            } else {
                when (status) {
                    // TODO: Move text and time calculations to StatusBar
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

     fun updateAll(context: Context) {
         CoroutineScope(Dispatchers.Default).launch {
             Logger.i("ScheduleWidget.updateAll()", "Update widgets")
             ScheduleWidget().updateAll(context)
         }
     }
}
