package com.example.widget_test

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun StatusBar(title: String, minutes: Int?, lesson: Lesson?) {
    Column(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text(
            text = if (minutes != null) "$title · $minutes мин" else title,
            style = TextStyle(fontSize = 16.sp),
            modifier = GlanceModifier.padding(bottom = 4.dp)
        )
        lesson?.let {
            Row {
                Text(
                    text = "${formatTime(it.start)} ${it.subject}",
                    style = TextStyle(fontSize = 14.sp)
                )
            }
            Text(
                text = formatTime(it.end),
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}

private fun formatTime(time: LocalDateTime): String {
    return time.format(DateTimeFormatter.ofPattern("HH:mm"))
}
