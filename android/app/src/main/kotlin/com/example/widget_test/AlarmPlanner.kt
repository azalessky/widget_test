package com.example.widget_test

import android.content.Context
import java.time.LocalDateTime
import java.time.LocalTime

object AlarmPlanner {
    fun scheduleAlarms(context: Context) {
        Logger.i("AlarmPlanner.scheduleAlarms()", "Plan alarms for today")
        AlarmScheduler.cancelAll(context)

        val lessons = LessonRepository.getTodayLessons()
    //    scheduleLessons(context, lessons)
        scheduleTicker(context, lessons)     
        scheduleReset(context)
    }

    private fun scheduleLessons(context: Context, lessons: List<Lesson>) {
        for (lesson in lessons) {
            val start = lesson.start
            val end = lesson.end

            Logger.i("AlarmPlanner.scheduleLessons()", "Plan alarms, subject = ${lesson.subject}, start = $start, end = $end")

            AlarmScheduler.schedule(context, start) {
                Logger.i("AlarmPlanner.scheduleLessons()", "Lesson started, subject = ${lesson.subject}")
                ScheduleWidget().updateWidgets(context)
            }

            AlarmScheduler.schedule(context, end) {
                Logger.i("AlarmPlanner.scheduleLessons()", "Lesson finished, subject = ${lesson.subject}")
                ScheduleWidget().updateWidgets(context)
            }
        }
    }

    private fun scheduleTicker(context: Context, lessons: List<Lesson>) {
        if (lessons.isEmpty()) return

        val now = LocalDateTime.now().plusMinutes(1)
        val start = lessons.first().start.minusHours(1)
        val end = lessons.last().end
        val initial = if (now.isBefore(start)) start else now

        if (initial.isAfter(end)) {
            Logger.i("AlarmPlanner.scheduleTicker()", "Skipped ticker")
            return
        }
        startTicker(context, initial, end)
    }

    private fun startTicker(context: Context, current: LocalDateTime, end: LocalDateTime) {
        if (current.isAfter(end)) {
            Logger.i("AlarmPlanner.startTicker()", "Ticker finished")
            return
        }
        Logger.i("AlarmPlanner.startTicker()", "Ticker update at $current")
        
        AlarmScheduler.schedule(context, current) {
            ScheduleWidget().updateWidgets(context)
            startTicker(context, current.plusMinutes(1), end)
        }
    }

    private fun scheduleReset(context: Context) {
        Logger.i("AlarmPlanner.scheduleReset()", "Schedule alarms reset")
    
        val midnight = LocalDateTime.now()
            .plusDays(1)
            .with(LocalTime.MIDNIGHT)

        AlarmScheduler.schedule(context, midnight) {
            Logger.i("AlarmPlanner.scheduleReset()", "Triggered alarms reset")
            scheduleAlarms(context)
        }
    }
}
