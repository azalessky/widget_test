package com.example.widget_test

import android.content.Intent
import android.widget.RemoteViewsService

class LessonListService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return LessonListFactory(applicationContext)
    }
}
