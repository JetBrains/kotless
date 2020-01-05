package io.kotless.local.scheduled

fun String.toQuartzCron() = "0 $this"
