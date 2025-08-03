package com.example.widget_test

import android.content.Context
import android.util.Log

object StateManager {
    fun updateState(context: Context, updateAlarms: Boolean = true) {
        Logger.i("StateManager.updateState()", "updateAlarms = $updateAlarms")

        LessonRepository.loadLessons(context)
        ScheduleWidget.updateAll(context)
        if (updateAlarms) AlarmPlanner.scheduleAlarms(context)
    }
}
