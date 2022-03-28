package io.kotless.examples.page

import io.kotless.dsl.lang.event.*
import io.kotless.dsl.lang.event.S3Event
import io.kotless.dsl.lang.event.SQSEvent
import io.kotless.dsl.lang.http.Get
import io.kotless.dsl.lang.http.notFound
import io.kotless.dsl.model.HttpResponse
import io.kotless.dsl.model.events.*
import io.kotless.examples.SSMAwsEvent
import kotlinx.serialization.KSerializer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ShortenerKt")

@Get("/r")
fun redirectUrl(k: String): HttpResponse {
    return notFound("Unknown URL")
}


@S3Event("kotless-test-bucket", "ObjectRemoved:*")
fun s3EventHandler(event: AwsEventInformation) {
    logger.info("Object $event removed")
}

@SQSEvent("arn:aws:sqs:eu-west-1:<account>:kotless-test-queue")
fun anotherHandler(event: AwsEventInformation) {
    logger.info("Object $event created by another handler")
}

@CustomAwsEvent("ssm/path/example")
fun customEventHandler(event: SSMAwsEvent.SSMAwsEventInformation) {
    logger.info("Ssm event $event")
}

@CustomEventGenerator
object SSMAwsEventInformationGenerator : AwsEventGenerator() {
    override val serializer: KSerializer<out AwsEvent> = SSMAwsEvent.serializer()

    override fun mayDeserialize(jsonObject: String): Boolean = jsonObject.contains("aws.ssm")
}
