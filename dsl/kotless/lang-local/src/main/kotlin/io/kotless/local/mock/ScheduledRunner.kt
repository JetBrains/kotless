package io.kotless.local.mock

import io.kotless.ScheduledEventType
import io.kotless.dsl.LambdaHandler
import io.kotless.dsl.lang.event.Scheduled
import io.kotless.dsl.model.CloudWatch
import io.kotless.dsl.reflection.ReflectionScanner
import io.kotless.dsl.utils.Json
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import java.io.ByteArrayOutputStream
import java.lang.reflect.Method
import kotlin.math.absoluteValue
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction


class ScheduledRunner(private val handler: LambdaHandler) {
    companion object {
        private const val JOB_KEY = "SCHEDULED_JOB"
        private const val HANDLER_KEY = "LAMBDA_HANDLER"

        private fun Method.toAnnotation(): Scheduled? {
            return (kotlinFunction as KFunction<*>).findAnnotation()
        }

        private fun String.toQuartzCron() = "0 $this"

        private fun Method.toId(): String {
            val annotation = toAnnotation()!!

            return annotation.id.takeIf { it.isNotBlank() } ?: run {
                val name = "${declaringClass.kotlin.qualifiedName!!}.$name"
                "${ScheduledEventType.General.prefix}-${name.hashCode().absoluteValue}"
            }
        }
    }

    private val scheduler by lazy { StdSchedulerFactory().scheduler }

    private val methods by lazy { ReflectionScanner.methodsWithAnnotation<Scheduled>() }

    class ScheduledJob : Job {
        override fun execute(context: JobExecutionContext) {
            val handler = context.mergedJobDataMap[HANDLER_KEY] as LambdaHandler

            val method = context.mergedJobDataMap[JOB_KEY] as Method
            val id = method.toId()

            val apiRequest = CloudWatch(
                `detail-type` = "Scheduled Event",
                source = "aws.events",
                resources = setOf(id)
            )

            handler.handleRequest(
                input = Json.string(CloudWatch.serializer(), apiRequest).byteInputStream(),
                output = ByteArrayOutputStream(),
                any = null
            )
        }
    }

    fun start() {
        for (method in methods) {
            val id = method.toId()
            val annotation = method.toAnnotation()!!

            val map = JobDataMap().apply {
                this[JOB_KEY] = method
                this[HANDLER_KEY] = handler
            }
            val job = JobBuilder
                .newJob(ScheduledJob::class.java)
                .withIdentity(id)
                .usingJobData(map)
                .build()

            val trigger = CronScheduleBuilder
                .cronSchedule(annotation.cron.toQuartzCron())
                .build()
                .triggerBuilder
                .withIdentity(id)
                .build()

            scheduler.scheduleJob(job, trigger)
        }

        scheduler.start()
    }

    fun stop() {
        scheduler.shutdown()
    }
}
