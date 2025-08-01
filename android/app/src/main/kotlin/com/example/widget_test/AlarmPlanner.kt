package com.example.widget_test

import android.content.Context
import java.time.LocalDateTime

object AlarmPlanner {
    fun scheduleAlarms(context: Context) {
        Logger.i("AlarmPlanner.scheduleAlarms()", "Plan alarms for today")
        AlarmScheduler.cancelAll(context)

        val lessons = LessonRepository.getTodayLessons()
        scheduleTicker(context, lessons)     
        scheduleReset(context)
    }

    private fun scheduleTicker(context: Context, lessons: List<Lesson>) {
        if (lessons.isEmpty()) return

        val now = LocalDateTime.now().stripSeconds().plusMinutes(1)
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
        Logger.i("AlarmPlanner.startTicker()", "time = $current")
        
        AlarmScheduler.schedule(context, current) {
            ScheduleWidget().updateWidgets(context)
            startTicker(context, current.plusMinutes(1), end)
        }
    }

    private fun scheduleReset(context: Context) {
        val time = LocalDateTime.now().tomorrowMidnight()
        Logger.i("AlarmPlanner.scheduleReset()", "Schedule alarms reset, time = $time")

        AlarmScheduler.schedule(context, time) {
            Logger.i("AlarmPlanner.scheduleReset()", "Triggered alarms reset")
            ScheduleWidget().updateWidgets(context)
            scheduleAlarms(context)
        }
    }
}
