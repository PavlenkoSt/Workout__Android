package com.learning.workout__android.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy", Locale.ENGLISH)
    return date.format(formatter)
}

fun formatTimestamp(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}