package io.kotless.dsl.ktor.app

import io.kotless.dsl.model.AwsEvent
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.engine.*

/**
 * Ktor Call used by Kotless. It is mapped from APIGateway request and to APIGateway response.
 */
@EngineAPI
class AwsEventCall(application: Application, request: AwsEvent.Record) : BaseApplicationCall(application) {
    override val request = AwsEventRequest(request, this)
    override val response = KotlessResponse(this)

    override val parameters: Parameters by lazy { this.request.queryParameters }

    init {
        putResponseAttribute()
    }
}
