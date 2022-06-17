package io.kotless.local.scheduled

import io.kotless.dsl.HandlerAWS
import io.kotless.dsl.app.events.EventsReflectionScanner
import io.kotless.dsl.cloud.aws.CloudWatch
import io.kotless.dsl.lang.event.Cloudwatch
import io.kotless.dsl.utils.JSON
import org.quartz.*
import java.io.ByteArrayOutputStream

internal class ScheduledJob : Job {
    companion object {
        const val ID_KEY = "SCHEDULED_ID"
        const val HANDLER_KEY = "LAMBDA_HANDLER"

        fun collectJobs(handler: HandlerAWS): Map<Trigger, JobDetail> {
            val jobs = HashMap<Trigger, JobDetail>()

            for ((ids, _, annotation) in EventsReflectionScanner.getEvents()) {
                if(annotation !is Cloudwatch) continue
                val id = ids.first()

                val map = JobDataMap().apply {
                    this[ID_KEY] = id
                    this[HANDLER_KEY] = handler
                }
                val job = JobBuilder
                    .newJob(ScheduledJob::class.java)
                    .withIdentity(id.key)
                    .usingJobData(map)
                    .build()

                val trigger = CronScheduleBuilder
                    .cronSchedule(annotation.cron.toQuartzCron())
                    .build()
                    .triggerBuilder
                    .withIdentity(id.key)
                    .build()

                jobs[trigger] = job
            }

            return jobs
        }
    }

    override fun execute(context: JobExecutionContext) {
        val handler = context.mergedJobDataMap[HANDLER_KEY] as HandlerAWS
        val id = context.mergedJobDataMap[ID_KEY] as String

        val apiRequest = CloudWatch(
            `detail-type` = "Scheduled Event",
            source = "aws.events",
            resources = setOf(id)
        )

        handler.handleRequest(
            input = JSON.string(CloudWatch.serializer(), apiRequest).byteInputStream(),
            output = ByteArrayOutputStream(),
            any = null
        )
    }

}
