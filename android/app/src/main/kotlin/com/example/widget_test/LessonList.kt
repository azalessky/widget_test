package com.example.widget_test

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LessonItem(lesson: Lesson) {
    Row(modifier = GlanceModifier.padding(vertical = 4.dp)) {
        Text(
            text = "${formatTime(lesson.start)} - ${formatTime(lesson.end)}",
            modifier = GlanceModifier.padding(end = 12.dp),
            style = TextStyle(fontSize = 14.sp)
        )
        Text(
            text = lesson.subject,
            style = TextStyle(fontSize = 14.sp)
        )
    }
}

@Composable
fun LessonList(lessons: List<Lesson>) {
    LazyColumn {
        items(lessons.size) { index ->
            LessonItem(lessons[index])
        }
    }
}
private fun formatTime(time: LocalDateTime): String {
    return time.format(DateTimeFormatter.ofPattern("HH:mm"))
}
