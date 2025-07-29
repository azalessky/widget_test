package com.example.widget_test

import android.content.Context
import org.json.JSONArray
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Lesson(
    val subject: String,
    val start: LocalTime,
    val end: LocalTime
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

    fun getLessons(): List<Lesson> {
        return lessons
    }

    fun getLessonStatus(now: LocalTime): Pair<LessonStatus, Lesson?> {
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
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun parse(json: String): List<Lesson> {
        return try {
            val array = JSONArray(json)
            List(array.length()) { i ->
                val obj = array.getJSONObject(i)
                val subject = obj.getString("subject")
                val start = LocalTime.parse(obj.getString("start"), timeFormatter)
                val end = LocalTime.parse(obj.getString("end"), timeFormatter)

                Lesson(subject, start, end)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
