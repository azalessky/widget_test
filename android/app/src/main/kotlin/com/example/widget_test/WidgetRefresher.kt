package com.example.widget_test

import HomeWidgetGlanceStateDefinition
import android.content.Context
import androidx.core.content.edit
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object WidgetRefresher {
    private const val UPDATE_DELAY = 60_000L
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var scheduledJob: Job? = null
    private var lastUpdate = 0L

    fun requestUpdate(context: Context, reason: String) {
        Logger.i("WidgetRefresher.requestUpdate()", "Update requested, reason = $reason")
        
        val now = System.currentTimeMillis()
        val timeSpent = now - lastUpdate
        val delayTime = UPDATE_DELAY - timeSpent

        if (scheduledJob?.isActive == true) {
            Logger.i("WidgetRefresher.requestUpdate()", "Wait $delayTime ms for another update")
            return
        }
        
        scheduledJob = scope.launch {
            if (lastUpdate == 0L || timeSpent >= UPDATE_DELAY) {
                Logger.i("WidgetRefresher.requestUpdate()", "Running update immediately")
            } else {
                Logger.i("WidgetRefresher.requestUpdate()", "Wait $delayTime ms before update")
                delay(delayTime)
            }

            Logger.i("WidgetRefresher.requestUpdate()", "Updating all widgets")

            updateWidgetState(context, reason)
            ScheduleWidget().updateAll(context)

            lastUpdate = System.currentTimeMillis()
        }
    }

    private suspend fun updateWidgetState(context: Context, reason: String) {
        val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(ScheduleWidget::class.java)
        glanceIds.forEach { id ->
            updateAppWidgetState(context, HomeWidgetGlanceStateDefinition(), id) { state ->
                state.preferences.edit {
                    putLong("lastUpdated", System.currentTimeMillis())
                    putString("updateReason", reason)
                }
                state
            }
        }
    }
}
