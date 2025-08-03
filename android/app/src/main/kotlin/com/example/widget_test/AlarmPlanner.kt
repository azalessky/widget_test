package com.example.widget_test

import android.content.Context
import android.content.Intent
import java.time.LocalDateTime

object AlarmPlanner {
    fun scheduleAlarms(context: Context) {
        Logger.i("AlarmPlanner.scheduleAlarms()", "Schedule alarms for today")

        AlarmScheduler.cancelAll(context)
        scheduleTicker(context)     
        scheduleReset(context)
    }

    fun handleAlarm(context: Context, intent: Intent) {
        val key = intent.getStringExtra("alarm_key") ?: return
        Logger.i("AlarmPlanner.handleAlarm()", "Handle alarm $key") 

        when {
            key.startsWith("ticker") -> {
                Logger.i("AlarmPlanner.handleAlarm()", "Ticker alarm triggered")
                scheduleTicker(context)
            }
            key.startsWith("reset") -> {
                Logger.i("AlarmPlanner.handleAlarm()", "Reset alarm triggered")
                scheduleAlarms(context)
            }
            else -> Logger.i("AlarmPlanner.handleAlarm()", "Unknown alarm $key")
        } 
    }

    private fun scheduleTicker(context: Context) {
        val lessons = LessonRepository.getTodayLessons()
        if (lessons.isEmpty()) 
        {
            Logger.i("AlarmPlanner.scheduleTicker()", "No lessons")
            return
        }

        val now = LocalDateTime.now().stripSeconds().plusMinutes(1)
        val start = lessons.first().start.minusHours(1)
        val end = lessons.last().end
        val time = if (now < start) start else now

        if (time <= end) {
            Logger.i("AlarmPlanner.scheduleTicker()", "Schedule ticker at $time")

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("alarm_key", "ticker_${time.toEpochMillis()}")
            }
            AlarmScheduler.schedule(context, time, intent)
        }
        else {
            Logger.i("AlarmPlanner.scheduleTicker()", "Finished ticker")
        }
    }

    private fun scheduleReset(context: Context) {
        val time = LocalDateTime.now().tomorrowMidnight()
        Logger.i("AlarmPlanner.scheduleReset()", "Schedule alarms reset at $time")

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_key", "reset_${time.toEpochMillis()}")
        }
        AlarmScheduler.schedule(context, time, intent)
    }
}
