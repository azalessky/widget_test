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

data class ScheduleState(
    val status: ScheduleStatus,
    val lesson: Lesson?
)

enum class ScheduleStatus {
    EMPTY, WAITING, RUNNING, BREAK, DONE
}

enum class LessonStatus {
    PLANNED, ACTIVE, COMPLETED
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
        return lessons.filter {
            it.start.toLocalDate() == LocalDate.now()
        }
    }

    fun getRemainingLessons(): List<Lesson> {
        return getTodayLessons().filter {
            getLessonStatus(it) != LessonStatus.COMPLETED
        }
    }

    fun getLessonStatus(lesson: Lesson): LessonStatus {
        val now = LocalDateTime.now().stripSeconds()
        return if (now.isBefore(lesson.start)) {
            LessonStatus.PLANNED
        } else if (!now.isBefore(lesson.start) && now.isBefore(lesson.end)) {
            LessonStatus.ACTIVE
        } else {
            LessonStatus.COMPLETED
        }
    }

   fun getScheduleState(): ScheduleState {
        val todayLessons = getTodayLessons()
        if (todayLessons.isEmpty()) {
            return ScheduleState(ScheduleStatus.EMPTY, null)
        }

        val state = todayLessons.withIndex().firstNotNullOfOrNull { (index, lesson) ->
            when (getLessonStatus(lesson)) {
                LessonStatus.ACTIVE -> ScheduleState(ScheduleStatus.RUNNING, lesson)
                LessonStatus.PLANNED -> {
                    val status = if (index == 0) ScheduleStatus.WAITING else ScheduleStatus.BREAK
                    ScheduleState(status, lesson)
                }
                else -> null
            }
        }
        return state ?: ScheduleState(ScheduleStatus.DONE, null)
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

fun ScheduleState.getStatusText(): String {
    val statusText = when (status) {
        ScheduleStatus.EMPTY -> "Уроков нет"
        ScheduleStatus.WAITING -> "Начало уроков"
        ScheduleStatus.RUNNING -> "Идёт урок"
        ScheduleStatus.BREAK -> "Следующий урок"
        ScheduleStatus.DONE -> "Уроки закончены"
    }
    val timeText = when (status) {
        ScheduleStatus.EMPTY -> null
        ScheduleStatus.WAITING -> lesson?.start?.formatTimeLeft(60)
        ScheduleStatus.RUNNING -> lesson?.end?.formatTimeLeft()
        ScheduleStatus.BREAK -> lesson?.start?.formatTimeLeft(60)
        ScheduleStatus.DONE -> null
    }

    return if (timeText != null) "$statusText • $timeText" else statusText
}

