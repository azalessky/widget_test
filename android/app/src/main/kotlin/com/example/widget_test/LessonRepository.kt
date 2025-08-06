package com.example.widget_test

import android.content.Context
import org.json.JSONArray
import java.time.LocalDate
import java.time.LocalDateTime

data class Lesson(
    val subject: String,
    val start: LocalDateTime,
    val end: LocalDateTime
)

data class ActiveLesson(
    val lesson: Lesson?,
    val status: LessonStatus,
)

enum class LessonStatus {
    UPCOMING,
    ONGOING,
    WAITING,
    COMPLETED
}

object LessonRepository {
    private var lessons: List<Lesson> = emptyList()

    fun loadLessons(context: Context) {
        val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
        val json = prefs.getString("lessons", null) ?: "[]"
        val parsed = LessonParser.parse(json)

        lessons = parsed.sortedBy { it.start }
        Logger.i("LessonRepository.loadLessons()", "lessons = ${lessons.size}")
    }

    fun getTodayLessons(): List<Lesson> {
        return lessons.filter { it.start.toLocalDate() == LocalDate.now() }
    }

   fun getActiveLesson(): ActiveLesson {
        val now = LocalDateTime.now().stripSeconds()
        val todayLessons = getTodayLessons()

        for ((index, lesson) in todayLessons.withIndex()) {
            if (!now.isBefore(lesson.start) && now.isBefore(lesson.end)) {
                return ActiveLesson(lesson, LessonStatus.ONGOING)
            }
            if (now.isBefore(lesson.start)) {
                val status = if (index == 0) LessonStatus.UPCOMING else LessonStatus.WAITING
                return ActiveLesson(lesson, status)
            }
        }
        return ActiveLesson(null, LessonStatus.COMPLETED)
    }
}

object LessonParser {
    fun parse(json: String): List<Lesson> {
        return try {
            val array = JSONArray(json)
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                val subject = obj.getString("subject")
                val start = LocalDateTime.parse(obj.getString("start"))
                val end = LocalDateTime.parse(obj.getString("end"))

                Lesson(subject, start, end)
            }
        } catch (e: Exception) {
            Logger.e("LessonParser.parse()", "Error parsing lessons", e)
            emptyList()
        }
    }
}

fun ActiveLesson.getStatusText(): String {
    val (statusText, timeText) = when (status) {
        LessonStatus.UPCOMING -> "Начало уроков" to lesson?.start?.formatTimeLeft(60)
        LessonStatus.ONGOING -> "Идёт урок" to lesson?.end?.formatTimeLeft()
        LessonStatus.WAITING -> "Следующий урок" to lesson?.start?.formatTimeLeft(60)
        LessonStatus.COMPLETED -> "Уроки закончены" to null
    }
    return if (timeText != null) "$statusText • $timeText" else statusText
}
