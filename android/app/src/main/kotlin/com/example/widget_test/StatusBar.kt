package com.example.widget_test

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getStatusText(status: LessonStatus, lesson: Lesson?): String {
    val now = LocalDateTime.now().stripSeconds()
    val message: String
    val timeLabel: String?

    when (status) {
        LessonStatus.UPCOMING -> {
            message = "Начало уроков"
            timeLabel = lesson?.start?.let {
                val minutesLeft = Duration.between(now, it).toMinutes()
                if (minutesLeft > 60)
                    it.format(DateTimeFormatter.ofPattern("HH:mm"))
                else
                    formatTimeUntil(it, now)
            }
        }
        LessonStatus.ACTIVE -> {
            message = "Идёт урок"
            timeLabel = lesson?.end?.let { formatTimeUntil(it, now) }
        }
        LessonStatus.WAITING -> {
            message = "Следующий урок"
            timeLabel = lesson?.start?.let { formatTimeUntil(it, now) }
        }
        LessonStatus.DONE -> {
            message = "Уроки закончены"
            timeLabel = null
        }
    }

    return "$message${timeLabel?.let { " • $it" } ?: ""}"
}

fun formatTimeUntil(target: LocalDateTime, now: LocalDateTime): String {
    val duration = Duration.between(now, target)
    val totalMinutes = duration.toMinutes()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours == 0L -> "$minutes мин"
        minutes == 0L -> "$hours ч"
        else -> "$hours ч $minutes мин"
    }
}