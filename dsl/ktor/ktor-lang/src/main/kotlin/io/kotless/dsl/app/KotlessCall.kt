package io.kotless.dsl.app

import io.kotless.dsl.model.HttpRequest
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.http.Parameters
import io.ktor.request.ApplicationRequest
import io.ktor.response.ApplicationResponse
import io.ktor.util.Attributes

class KotlessCall(override val application: Application, request: HttpRequest) : ApplicationCall {
    override val attributes: Attributes = Attributes()
    override val request: ApplicationRequest = KotlessRequest(request, this)
    override val response: ApplicationResponse
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val parameters: Parameters get() = request.queryParameters

}
