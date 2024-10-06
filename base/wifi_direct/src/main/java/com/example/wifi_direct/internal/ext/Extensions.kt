package com.example.wifi_direct.internal.ext

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getTime(format: String = "dd/MM/yyyy hh:mm:ss.SSS"): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(format, Locale.US)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    return formatter.format(calendar.time)
}