package io.kotless.dsl.app.http

import io.kotless.InternalAPI
import io.kotless.dsl.lang.KotlessContext
import io.kotless.dsl.lang.http.*
import io.kotless.dsl.model.HttpRequest
import io.kotless.dsl.model.HttpResponse
import io.reflekt.Reflekt
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException

@InternalAPI
object RoutesDispatcher {
    private val logger = LoggerFactory.getLogger(RoutesDispatcher::class.java)

    private val pipeline by lazy { preparePipeline(Reflekt.objects().withSupertype<HttpRequestInterceptor>().toList().sortedBy { it.priority }) }

    fun dispatch(request: HttpRequest, resourceKey: RouteKey): HttpResponse {
        return try {
            KotlessContext.HTTP.request = request
            pipeline(request, resourceKey)
        } finally {
            KotlessContext.HTTP.reset()
        }
    }

    private fun preparePipeline(left: List<HttpRequestInterceptor>): (HttpRequest, RouteKey) -> HttpResponse {
        if (left.isNotEmpty()) {
            val interceptor = left.first()
            return { req, key ->
                interceptor.intercept(req, key, preparePipeline(left.drop(1)))
            }
        } else {
            return { req, key -> processRequest(req, key) }
        }
    }

    private fun processRequest(request: HttpRequest, resourceKey: RouteKey): HttpResponse {
        logger.debug("Passing request to route {}", resourceKey)

        val (func, mime) = RoutesStorage[resourceKey] ?: return notFound()
        logger.debug("Found $func for key $resourceKey")

        val result = try {
            func()
//            FunctionCaller.call(func, request.params.orEmpty())
        } catch (e: Exception) {
            logger.error("Failed on call of function", if (e is InvocationTargetException) e.targetException else e)
            return serverError(e.message)
        }

        logger.debug("Route returned result")
        return okResponse(result.toString(), mime)
    }
}
