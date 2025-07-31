package com.example.widget_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmCallbackReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val key = intent.getStringExtra("alarm_key") ?: return
        Logger.i("AlarmCallbackReceiver.onReceive()", "Trigger alarm, key = $key")

        AlarmCallbackRegistry.trigger(key)
        AlarmCallbackRegistry.remove(key)
    }
}