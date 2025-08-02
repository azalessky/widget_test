package com.example.widget_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmCallbackReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Logger.i("AlarmCallbackReceiver.onReceive()", "Alarm received")
        
        LessonRepository.loadLessons(context)
        AlarmPlanner.handleAlarm(context, intent)
    }
}