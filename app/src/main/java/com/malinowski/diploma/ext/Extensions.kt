package com.malinowski.diploma.ext

import java.text.SimpleDateFormat
import java.util.*

fun getTime(): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS",  Locale.US)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    return formatter.format(calendar.time)
}