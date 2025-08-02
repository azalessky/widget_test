package com.example.widget_test

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.Duration
import java.time.LocalDateTime

object AlarmScheduler {
    private val alarmKeys = mutableSetOf<String>()

    fun schedule(context: Context, time: LocalDateTime, intent: Intent) {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, time)
        if (duration.isNegative) return

        val millis = time.toEpochMillis()
        val key = intent.getStringExtra("alarm_key") ?: "undefined"
        val pendingIntent = createIntent(context, key, intent)

        Logger.i("AlarmScheduler.schedule()", "Set alarm $key at $time")
        
        scheduleIntent(context, millis, pendingIntent)
        alarmKeys.add(key)
    }

    fun cancelAll(context: Context) {
        for (key in alarmKeys) {
            Logger.i("AlarmScheduler.cancelAll()", "Cancel alarm $key")
            val intent = Intent(context, AlarmCallbackReceiver::class.java).apply {
                putExtra("alarm_key", key)
            }
            val pendingIntent = createIntent(context, key, intent)
            cancelIntent(context, pendingIntent)
        }
        alarmKeys.clear()
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
