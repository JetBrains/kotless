package io.kotless.parser.processor

import io.kotless.Application
import io.kotless.KotlessConfig
import io.kotless.resource.Lambda
import io.kotless.resource.StaticResource
import io.kotless.utils.TypedStorage
import java.io.File

/**
 * Context of code analysis.
 *
 * It includes resulting elements of Kotless Schema, set of already ran processors
 * and part of schema explicitly defined by user
 */
class ProcessorContext(val jar: File, val config: KotlessConfig, val lambda: Lambda.Config) {
    class Output(private val outputs: MutableMap<Processor<*>, Any> = HashMap()) {
        fun <T : Any> register(processor: Processor<T>, output: T) {
            outputs[processor] = output
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> get(processor: Processor<T>): T {
            return outputs[processor] as T
        }

        fun <T : Any> check(processor: Processor<T>) = processor in outputs
    }

    val output = Output()

    class Resources(val dynamics: TypedStorage<Lambda> = TypedStorage(), val statics: TypedStorage<StaticResource> = TypedStorage()) {
        fun register(key: TypedStorage.Key<Lambda>, lambda: Lambda) {
            dynamics[key] = lambda
        }

        fun register(key: TypedStorage.Key<StaticResource>, static: StaticResource) {
            statics[key] = static
        }
    }

    val resources = Resources()

    class Routes(
        private val myDynamics: MutableSet<Application.API.DynamicRoute> = HashSet(),
        private val myStatics: MutableSet<Application.API.StaticRoute> = HashSet()
    ) {
        val dynamics: Set<Application.API.DynamicRoute>
            get() = myDynamics.toSet()

        val statics: Set<Application.API.StaticRoute>
            get() = myStatics.toSet()


        fun register(dynamic: Application.API.DynamicRoute) {
            myDynamics.add(dynamic)
        }

        fun register(static: Application.API.StaticRoute) {
            myStatics.add(static)
        }
    }

    val routes = Routes()

    class Events(private val myEvents: MutableSet<Application.Events.Event> = HashSet()) {

        val events: Set<Application.Events.Event>
            get() = myEvents

        fun register(scheduled: Application.Events.Scheduled) {
            myEvents.add(scheduled)
        }

        fun register(s3Event: Application.Events.S3) {
            myEvents.add(s3Event)
        }
    }

    val events = Events()
}
