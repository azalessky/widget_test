package com.example.widget_test

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
        Log.i("AlarmScheduler", "Запланирован будильник для ключа: $key на время: $triggerTime")

        AlarmCallbackRegistry.register(key, callback)
        val intent = createIntent(context, key)
        scheduleIntent(context,triggerTime, intent)
    }

    fun cancelAll(context: Context) {
        Log.i("AlarmScheduler", "Отмена всех будильников")

        for (key in AlarmCallbackRegistry.getKeys()) {
            val intent = createIntent(context, key)
            cancelIntent(context, intent)
            
            Log.i("AlarmScheduler", "Отменён будильник для ключа: $key")
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
            Log.e("AlarmScheduler", "Ошибка установки будильника: ${e.message}")
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
