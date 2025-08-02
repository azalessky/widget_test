package com.example.widget_test

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
