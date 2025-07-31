package com.example.widget_test

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.ZoneId

object AlarmScheduler {
    fun schedule(
        context: Context,
        time: LocalDateTime,
        callback: () -> Unit
    ) {
        val millis = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val key = "alarm_$millis"
        val intent = createIntent(context, key)

        AlarmCallbackRegistry.register(key, callback)
        scheduleIntent(context, millis, intent)

        Logger.i("AlarmScheduler.schedule()", "Set alarm, key = $key, time = $time")
    }

    fun cancelAll(context: Context) {
        Logger.i("AlarmScheduler.cancelAll()", "Cancel all alarms")

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

class AlarmCallbackReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val key = intent.getStringExtra("alarm_key") ?: return
        AlarmCallbackRegistry.trigger(key)

        Logger.i("AlarmCallbackReceiver.onReceive()", "Trigger alarm, key = $key")
    }
}

object AlarmCallbackRegistry {
    private val callbacks = mutableMapOf<String, () -> Unit>()

    fun getKeys(): Set<String> = callbacks.keys

    fun register(key: String, callback: () -> Unit) {
        callbacks[key] = callback
    }

    fun trigger(key: String) {
        callbacks.remove(key)?.invoke()
    }
    
    fun clear() {
        callbacks.clear()
    }
}
