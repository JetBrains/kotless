package io.kotless.local.scheduled

import io.kotless.InternalAPI
import io.kotless.ScheduledEventType
import io.kotless.dsl.HandlerAWS
import io.kotless.dsl.cloud.aws.CloudWatch
import io.kotless.dsl.utils.JSON
import io.kotless.local.Environment
import io.kotless.utils.everyNMinutes
import org.quartz.*
import java.io.ByteArrayOutputStream

@OptIn(InternalAPI::class)
internal class AutowarmJob : Job {
    companion object {
        const val HANDLER_KEY = "LAMBDA_HANDLER"

        fun getJob(handler: HandlerAWS): Pair<Trigger, JobDetail>? {
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
        val handler = context.mergedJobDataMap[HANDLER_KEY] as HandlerAWS

        val apiRequest = CloudWatch(
            `detail-type` = "Scheduled Event",
            source = "aws.events",
            resources = setOf(ScheduledEventType.Autowarm.prefix)
        )

        handler.handleRequest(
            input = JSON.string(CloudWatch.serializer(), apiRequest).byteInputStream(),
            output = ByteArrayOutputStream(),
            any = null
        )
    }

}
