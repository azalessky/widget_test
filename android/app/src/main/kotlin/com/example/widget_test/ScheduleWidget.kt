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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.Duration

class ScheduleWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*>
        get() = HomeWidgetGlanceStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {        
        val lessons = LessonRepository.getLessons()
        provideContent { GlanceContent(lessons) }
    }

    @Composable
    private fun GlanceContent(lessons: List<Lesson>) {  
        val now = LocalTime.now()

        Log.i("ScheduleWidget", "Обновление UI на момент: $now")
        Log.i("ScheduleWidget", "Показываем уроки: $lessons")

        val (status, lesson) = LessonRepository.getLessonStatus(LocalTime.now())

        Column(modifier = GlanceModifier.fillMaxSize().padding(8.dp)) {

            // 🟡 Status Bar
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

            // 📚 Расписание
            Text(
                text = "📚 Расписание на сегодня",
                style = TextStyle(fontSize = 18.sp),
                modifier = GlanceModifier.padding(top = 12.dp, bottom = 8.dp)
            )

            if (lessons.isEmpty()) {
                Text(
                    text = "Нет уроков",
                    style = TextStyle(fontSize = 16.sp)
                )
            } else {
                lessons.forEach { lesson ->
                    Row(modifier = GlanceModifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "${formatTime(lesson.start)} - ${formatTime(lesson.end)}",
                            modifier = GlanceModifier.padding(end = 12.dp),
                            style = TextStyle(fontSize = 14.sp)
                        )
                        Text(
                            text = lesson.subject,
                            style = TextStyle(fontSize = 14.sp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun StatusBar(title: String, minutes: Int?, lesson: Lesson?) {
        Column(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp)) {
            Text(
                text = if (minutes != null) "$title · $minutes мин" else title,
                style = TextStyle(fontSize = 16.sp),
                modifier = GlanceModifier.padding(bottom = 4.dp)
            )
            lesson?.let {
                Row {
                    Text(
                        text = "${formatTime(it.start)} ${it.subject}",
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
                Text(
                    text = formatTime(it.end),
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }
    }

    private fun formatTime(time: LocalTime): String {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}
