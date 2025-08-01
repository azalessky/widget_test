package com.example.widget_test

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.Duration
import java.time.LocalDateTime

object AlarmScheduler {
    fun schedule(context: Context, time: LocalDateTime, callback: () -> Unit) {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, time)
        if (duration.isNegative) return

        val millis = System.currentTimeMillis() + duration.toMillis()
        val key = "alarm_$millis"
        val intent = createIntent(context, key)

        AlarmCallbackRegistry.register(key, callback)
        scheduleIntent(context, millis, intent)

        Logger.i("AlarmScheduler.schedule()", "Set alarm, key = $key, time = $time millis = $millis")
    }

    fun cancelAll(context: Context) {
        for (key in AlarmCallbackRegistry.getKeys()) {
            Logger.i("AlarmScheduler.cancelAll()", "Cancel alarm, key = $key")

            val intent = createIntent(context, key)
            cancelIntent(context, intent)
        }
        AlarmCallbackRegistry.clear()
    }

    private fun createIntent(context: Context, key: String): PendingIntent {
        val intent = Intent(context, AlarmCallbackReceiver::class.java).apply {
            putExtra("alarm_key", key)
        }
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
