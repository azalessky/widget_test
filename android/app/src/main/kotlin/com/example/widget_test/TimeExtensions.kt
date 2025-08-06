package com.example.widget_test

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun LocalDateTime.stripSeconds(): LocalDateTime =
    this.truncatedTo(ChronoUnit.MINUTES)

fun LocalDateTime.tomorrowMidnight(): LocalDateTime =
    this.toLocalDate().plusDays(1).atStartOfDay()

fun LocalDateTime.toEpochMillis(): Long {
    return this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun LocalDateTime.formatTime(): String {
    return format(DateTimeFormatter.ofPattern("HH:mm"))
}

fun LocalDateTime.formatTimeLeft(minutesThreshold: Int = 0): String {
    val now = LocalDateTime.now().stripSeconds()

    val totalMinutes = Duration.between(now, this).toMinutes()
    if (minutesThreshold > 0 && totalMinutes >= minutesThreshold) {
        return formatTime()
    }
    
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours == 0L -> "$minutes мин"
        minutes == 0L -> "$hours ч"
        else -> "$hours ч $minutes мин"
    }
}