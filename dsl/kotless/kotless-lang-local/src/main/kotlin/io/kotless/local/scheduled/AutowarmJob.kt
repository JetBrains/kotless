package io.kotless.local.scheduled

import io.kotless.ScheduledEventType
import io.kotless.dsl.LambdaHandler
import io.kotless.dsl.model.CloudWatch
import io.kotless.dsl.utils.Json
import io.kotless.local.Environment
import io.kotless.utils.everyNMinutes
import org.quartz.*
import java.io.ByteArrayOutputStream

internal class AutowarmJob : Job {
    companion object {
        const val HANDLER_KEY = "LAMBDA_HANDLER"

        fun getJob(handler: LambdaHandler): Pair<Trigger, JobDetail>? {
            val minutes = Environment.autowarmMinutes ?: return null

            val id = ScheduledEventType.Autowarm.prefix

            val map = JobDataMap().apply {
                this[HANDLER_KEY] = handler
            }

            val job = JobBuilder
                .newJob(AutowarmJob::class.java)
                .withIdentity(id)
                .usingJobData(map)
                .build()

            val trigger = CronScheduleBuilder
                .cronSchedule(everyNMinutes(minutes).toQuartzCron())
                .build()
                .triggerBuilder
                .withIdentity(id)
                .build()

            return trigger to job
        }
    }

    override fun execute(context: JobExecutionContext) {
        val handler = context.mergedJobDataMap[HANDLER_KEY] as LambdaHandler

        val apiRequest = CloudWatch(
            `detail-type` = "Scheduled Event",
            source = "aws.events",
            resources = setOf(ScheduledEventType.Autowarm.prefix)
        )

        handler.handleRequest(
            input = Json.string(CloudWatch.serializer(), apiRequest).byteInputStream(),
            output = ByteArrayOutputStream(),
            any = null
        )
    }

}
