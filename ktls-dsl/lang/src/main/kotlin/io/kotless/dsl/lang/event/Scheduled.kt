package io.kotless.dsl.lang.event


/**
 * Scheduled function in a Kotless web application
 *
 * It can be used to setup scheduled jobs for different purposes.
 *
 * Function should not have any parameters, since it will be called via crontab-like
 * service that passes no context
 *
 * @param cron -- cron expression defining trigger behavior; use [eachNDays], [eachNHours], [eachNMinutes]
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Scheduled(val cron: String)

fun eachNMinutes(minutes: Int) = "0/$minutes * * * *"
fun eachNHours(hours: Int) = "* 0/$hours * * *"
fun eachNDays(days: Int) = "* * 0/$days * *"
