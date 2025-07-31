package com.example.widget_test

import androidx.compose.runtime.Composable
import androidx.glance.text.Text
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun StatusBar(status: LessonStatus, lesson: Lesson?) {
    val now = LocalDateTime.now()
    val message: String
    val minutes: Int?

    when (status) {
        LessonStatus.ACTIVE -> {
            message = "Идёт урок"
            minutes = lesson?.end?.let { Duration.between(now, it).toMinutes().toInt() }
        }
        LessonStatus.WAITING -> {
            message = "Следующий урок"
            minutes = lesson?.start?.let { Duration.between(now, it).toMinutes().toInt() }
        }
        LessonStatus.NONE -> {
            message = "Нет уроков"
            minutes = null
        }
    }
    
    Text("$message${minutes?.let { " • $it мин." } ?: ""}")
}
