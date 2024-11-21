package com.example.entities

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val MinSec = "mm:ss.SSS"
const val HoursMinSec = "hh:mm:ss.SSS"
const val DayMonthYearHoursMinSec = "dd/MM/yyyy hh:mm:ss.SSS"

fun getTime(format: String = DayMonthYearHoursMinSec): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(format, Locale.US)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = System.currentTimeMillis()
    return formatter.format(calendar.time)
}