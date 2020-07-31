package io.kotless.local.scheduled

/** Create Cron expression with seconds (used by Quartz) from AWS Scheduled Cron expression (without seconds) */
internal fun String.toQuartzCron() = "0 $this"
