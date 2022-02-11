package io.kotless.examples

import io.kotless.dsl.ktor.KotlessAWS
import io.kotless.dsl.model.events.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.KSerializer
import org.slf4j.LoggerFactory

class Server : KotlessAWS() {
    private val logger = LoggerFactory.getLogger(Server::class.java)

    init {
        registerAwsEvent(SSMAwsEventInformationGenerator)
    }


    @OptIn(EngineAPI::class)
    override fun prepare(app: Application) {
        app.routing {
            get("/") {
                call.respond("success")
            }
            ssm("ssm/path/{region}") {
                logger.info("Received ssm object ${call.request.queryParameters}")
                call.respond(HttpStatusCode.OK, "success")
            }
            sqs("arn:aws:sqs:eu-west-1:<account>:kotless-test-queue") {
                logger.info("Received object ${call.request.queryParameters}")
                call.respond(HttpStatusCode.OK, "success")
            }
            s3("kotless-test-bucket", "ObjectCreated:*") {
                logger.info("Received object ${call.request.queryParameters}")
                call.respond(HttpStatusCode.OK, "success")
            }
        }
    }

    fun Route.ssm(route: String, body: PipelineInterceptor<Unit, ApplicationCall>): Route {
        return route(
            route,
            HttpMethod("aws.ssm")
        ) { handle(body) }
    }

}
