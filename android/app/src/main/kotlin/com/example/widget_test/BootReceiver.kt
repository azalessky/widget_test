package com.example.widget_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Logger.i("BootReceiver.onReceive()", "Restore state")

            LessonRepository.loadLessons(context)
            ScheduleWidget.updateAll(context)
            AlarmPlanner.scheduleAlarms(context)
        }
    }
}
