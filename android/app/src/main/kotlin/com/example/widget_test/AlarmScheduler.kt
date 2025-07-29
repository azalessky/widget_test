package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

object AlarmScheduler {
    fun schedule(
        context: Context,
        triggerTime: Long,
        key: String,
        callback: () -> Unit
    ) {
        AlarmCallbackRegistry.register(key, callback)

        val intent = Intent(context, AlarmCallbackReceiver::class.java).apply {
            putExtra("alarm_key", key)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            key.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}

class AlarmCallbackReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val key = intent.getStringExtra("alarm_key") ?: return
        AlarmCallbackRegistry.trigger(key)
    }
}

object AlarmCallbackRegistry {
    private val callbacks = mutableMapOf<String, () -> Unit>()

    fun register(key: String, callback: () -> Unit) {
        callbacks[key] = callback
    }

    fun trigger(key: String) {
        callbacks.remove(key)?.invoke()
    }
}
