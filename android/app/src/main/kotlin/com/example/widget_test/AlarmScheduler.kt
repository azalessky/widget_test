package com.example.widget_test

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.Duration
import java.time.LocalDateTime

object AlarmScheduler {
    private val alarms = mutableMapOf<String, LocalDateTime>()

    fun schedule(context: Context, time: LocalDateTime, intent: Intent) {
        val key = intent.getStringExtra("alarm_key") ?: "undefined"
        if (alarms.contains(key)) {
            Logger.i("AlarmScheduler.schedule()", "Alarm $key is already scheduled")
            return
        }

        val now = LocalDateTime.now()
        val duration = Duration.between(now, time)
        if (duration.isNegative) return

        Logger.i("AlarmScheduler.schedule()", "Set alarm $key at $time")

        val pendingIntent = createIntent(context, key, intent)
        val millis = time.toEpochMillis()

        scheduleIntent(context, millis, pendingIntent)
        alarms[key] = time
    }

    fun cancelAll(context: Context) {
        alarms.forEach { (key, time) ->
            Logger.i("AlarmScheduler.cancelAll()", "Cancel alarm $key at $time")

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("alarm_key", key)
            }
            val pendingIntent = createIntent(context, key, intent)
            cancelIntent(context, pendingIntent)
        }
        alarms.clear()
    }


    private fun createIntent(context: Context, key: String, intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            key.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleIntent(context: Context, triggerTime: Long, intent: PendingIntent) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                intent)
        } catch (e: SecurityException) {
            Logger.e("AlarmScheduler.scheduleIntent()", "Error setting alarm", e)
        }  
    }

    private fun cancelIntent(context: Context, intent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(intent)
    }
}
