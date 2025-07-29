package com.example.widget_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i("WidgetAlarmReceiver", "Будильник сработал")

        CoroutineScope(Dispatchers.Default).launch {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(HomeWidget::class.java)
            Log.i("WidgetAlarmReceiver", "Получено glanceIds: ${glanceIds.size}")

            glanceIds.forEach { id ->
                Log.i("WidgetAlarmReceiver", "Обновление виджета: $id") 
                HomeWidget().update(context, id)
            }
        }
        WidgetAlarmScheduler.scheduleNextCycle(context)
        Log.i("WidgetAlarmReceiver", "Запланирован следующий будильник")
    }
}
