    package com.example.widget_test

    import android.content.BroadcastReceiver
    import android.content.Context
    import android.content.Intent

    class ActionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val action = intent?.action
            Logger.i("ActionReceiver.onReceive()", "Received action $action")

            when (action) {
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_USER_PRESENT -> {
                    StateManager.updateState(context)
                }
                Intent.ACTION_CONFIGURATION_CHANGED -> {
                    ScheduleWidget.updateAll(context)
                }
            }
        }
}
