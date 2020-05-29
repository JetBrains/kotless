package io.kotless.parser.processor

import io.kotless.KotlessConfig
import io.kotless.Lambda
import io.kotless.StaticResource
import io.kotless.Webapp
import io.kotless.utils.TypedStorage
import java.io.File

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

    class Routes(private val myDynamics: MutableSet<Webapp.ApiGateway.DynamicRoute> = HashSet(),
                 private val myStatics: MutableSet<Webapp.ApiGateway.StaticRoute> = HashSet()) {
        val dynamics: Set<Webapp.ApiGateway.DynamicRoute>
            get() = myDynamics.toSet()

        val statics: Set<Webapp.ApiGateway.StaticRoute>
            get() = myStatics.toSet()


        fun register(dynamic: Webapp.ApiGateway.DynamicRoute) {
            myDynamics.add(dynamic)
        }

        fun register(static: Webapp.ApiGateway.StaticRoute) {
            myStatics.add(static)
        }
    }

    val routes = Routes()

    class Events(private val myScheduled: MutableSet<Webapp.Events.Scheduled> = HashSet()) {
        val scheduled: Set<Webapp.Events.Scheduled>
            get() = myScheduled.toSet()

        fun register(scheduled: Webapp.Events.Scheduled) {
            myScheduled.add(scheduled)
        }
    }

    val events = Events()
}
