package io.kotless.gen.factory.aws.resource.static

import io.kotless.Application
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.terraformkt.aws.data.sqs.SqsQueue
import io.terraformkt.aws.data.sqs.sqs_queue

object SQSDataFactory : GenerationFactory<Application.Events.SQS, SQSDataFactory.Output> {
    data class Output(val sqsQueue: SqsQueue)

    private val cache = mutableMapOf<String, SqsQueue>()

    override fun mayRun(entity: Application.Events.SQS, context: GenerationContext) = true

    override fun generate(entity: Application.Events.SQS, context: GenerationContext): GenerationFactory.GenerationResult<Output> {
        val key = context.names.tf(entity.queueArn)
        if (key in cache) return GenerationFactory.GenerationResult(Output(cache[key]!!))

        val sqsQueue = sqs_queue(context.names.tf(entity.fqId)) {
            name = entity.queueArn.split(":").last()
        }
        cache[key] = sqsQueue
        return GenerationFactory.GenerationResult(Output(sqsQueue), sqsQueue)
    }
}
