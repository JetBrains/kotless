package io.kotless.utils

fun everyNMinutes(minutes: Int): String {
    require(minutes in 0..60) { "Cannot generate Cron expression for each N minutes, if N is $minutes (not in 0..60)" }
    return "0/$minutes * * * ? *"
}

fun everyNHours(hours: Int): String {
    require(hours in 0..24) { "Cannot generate Cron expression for each N hours, if N is $hours (not in 0..24)" }
    return "* 0/$hours * * ? *"
}
