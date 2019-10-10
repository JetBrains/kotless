package io.kotless.dsl.lang.event


/**
 * Scheduled function in a Kotless web application
 *
 * It can be used to setup scheduled jobs for different purposes.
 *
 * Function should not have any parameters, since it will be called via crontab-like
 * service that passes no context
 *
 * Cron syntax is taken from AWS Scheduled Events
 * @see  <a href="https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html#CronExpressions">AWS docs for Scheduled Events</a>
 *
 * @param cron cron expression defining trigger behavior
 * @param id optional id of event, otherwise it will be generated from function
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Scheduled(val cron: String, val id: String = "") {
    companion object {
        const val everyMinute = "0/1 * * * ? *"
        const val every5Minutes = "0/5 * * * ? *"
        const val every10Minutes = "0/10 * * * ? *"
        const val everyHour = "* 0/1 * * ? *"
        const val everyDay = "* * 0/1 * ? *"
    }
}
