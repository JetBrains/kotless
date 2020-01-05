package io.kotless.local.scheduled

import io.kotless.dsl.LambdaHandler
import org.quartz.impl.StdSchedulerFactory


class ScheduledRunner(private val handler: LambdaHandler) {
    private val scheduler by lazy { StdSchedulerFactory().scheduler }

    fun start() {
        for ((trigger, job) in ScheduledJob.collectJobs(handler)) {
            scheduler.scheduleJob(job, trigger)
        }

        AutowarmJob.getJob(handler)?.let { (trigger, job) ->
            scheduler.scheduleJob(job, trigger)
        }

        scheduler.start()
    }

    fun stop() {
        scheduler.shutdown()
    }
}
