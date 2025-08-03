package com.example.widget_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Logger.i("AlarmReceiver.onReceive()", "Alarm received at ${LocalDateTime.now()}")
        
        StateManager.updateState(context, false)
        AlarmPlanner.handleAlarm(context, intent)
    }
}