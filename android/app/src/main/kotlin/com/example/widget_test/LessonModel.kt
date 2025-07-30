package com.example.widget_test

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.time.LocalDate
import java.time.LocalDateTime

data class Lesson(
    val subject: String,
    val start: LocalDateTime,
    val end: LocalDateTime
)

enum class LessonStatus {
    ACTIVE,
    WAITING,
    NONE
}

object LessonRepository {
    private var lessons: List<Lesson> = emptyList()

    fun loadLessons(context: Context) {
        val prefs = context.getSharedPreferences("HomeWidgetPreferences", Context.MODE_PRIVATE)
        val json = prefs.getString("lessons", null) ?: "[]"
        val parsed = LessonParser.parse(json)
        lessons = parsed.sortedBy { it.start }
    }

    fun getTodayLessons(): List<Lesson> {
        return lessons.filter { it.start.toLocalDate() == LocalDate.now() }
    }

    fun getLessonStatus(now: LocalDateTime): Pair<LessonStatus, Lesson?> {
        for (lesson in lessons) {
            if (now.isAfter(lesson.start) && now.isBefore(lesson.end)) {
                return LessonStatus.ACTIVE to lesson
            }
            if (now.isBefore(lesson.start)) {
                return LessonStatus.WAITING to lesson
            }
        }
        return LessonStatus.NONE to null
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
            Log.i("LessonParser", "Error parsing lessons: ${e.message}")
            emptyList()
        }
    }
}
