package io.kotless.local.scheduled

import io.kotless.dsl.HandlerAWS
import org.quartz.impl.StdSchedulerFactory

internal class Scheduler(private val handler: HandlerAWS) {
    private val quartz by lazy { StdSchedulerFactory().scheduler }

    fun start() {
        for ((trigger, job) in ScheduledJob.collectJobs(handler)) {
            quartz.scheduleJob(job, trigger)
        }

        AutowarmJob.getJob(handler)?.let { (trigger, job) ->
            quartz.scheduleJob(job, trigger)
        }

        quartz.start()
    }

    fun stop() {
        quartz.shutdown()
    }
}
