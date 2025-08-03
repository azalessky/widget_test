    package com.example.widget_test

    import android.appwidget.AppWidgetManager
    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent

    class ActionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val action = intent?.action
            Logger.i("ActionReceiver.onReceive()", "Received action $action")

            if (action == Intent.ACTION_BOOT_COMPLETED ||
                action == Intent.ACTION_USER_PRESENT
            ) {
                Logger.i("ActionReceiver.onReceive()", "Received system action $action")

                LessonRepository.loadLessons(context)
                ScheduleWidget.updateAll(context)
                AlarmPlanner.scheduleAlarms(context)
            }
        }
    }
