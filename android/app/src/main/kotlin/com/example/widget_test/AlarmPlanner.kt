package com.example.widget_test

import android.content.Context
import android.util.Log
import java.time.LocalDateTime
import java.time.LocalTime

object AlarmPlanner {
    fun scheduleAlarms(context: Context) {
        Log.i("AlarmPlanner", "scheduleAlarms(): Plan alarms for today")
        AlarmScheduler.cancelAll(context)

        val lessons = LessonRepository.getTodayLessons()
        scheduleLessons(context, lessons)
        scheduleReset(context)
    }

    private fun scheduleLessons(context: Context, lessons: List<Lesson>) {
        for (lesson in lessons) {
            val start = lesson.start
            val end = lesson.end

            Log.i("AlarmPlanner", "scheduleLessons(): Plan alarms for subject = ${lesson.subject}, start = $start, end = $end")

            AlarmScheduler.schedule(context, start) {
                Log.i("AlarmPlanner", "scheduleLessons(): Lesson started, subject = ${lesson.subject}")
                ScheduleWidget().updateAll(context)
            }

            AlarmScheduler.schedule(context, end) {
                Log.i("AlarmPlanner", "scheduleLessons(): Lesson finished, subject = ${lesson.subject}")
                ScheduleWidget().updateAll(context)
            }
        }
    }

    private fun scheduleReset(context: Context) {
        Log.i("AlarmPlanner", "scheduleReset(): Schedule alarms reset")
    
        val midnight = LocalDateTime.now()
            .plusDays(1)
            .with(LocalTime.MIDNIGHT)

        AlarmScheduler.schedule(context, midnight) {
            Log.i("AlarmPlanner", "scheduleReset(): Triggered alarms reset")
            scheduleAlarms(context)
        }
    }
}
