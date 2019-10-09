package io.kotless.parser.processor

import io.kotless.*
import java.io.File

class ProcessorContext(val jar: File, val config: KotlessConfig, val lambda: Lambda.Config) {
    class Output(private val outputs: MutableMap<Processor<*>, Any> = HashMap()) {
        fun <T : Any> register(processor: Processor<T>, output: T) {
            outputs[processor] = output
        }

        fun <T : Any> get(processor: Processor<T>): T {
            return outputs[processor] as T
        }

        fun <T : Any> check(processor: Processor<T>) = processor in outputs
    }

    val output = Output()

    class Resources(private val myDynamics: MutableSet<Lambda> = HashSet(), private val myStatics: MutableSet<StaticResource> = HashSet()) {
        val dynamics: Set<Lambda>
            get() = myDynamics.toSet()

        val statics: Set<StaticResource>
            get() = myStatics.toSet()

        fun register(lambda: Lambda) {
            myDynamics.add(lambda)
        }

        fun getDynamic(name: String) = myDynamics.find { it.name == name }

        fun register(static: StaticResource) {
            myStatics.add(static)
        }

        fun getStatic(file: File) = myStatics.find { it.content.canonicalFile == file.canonicalFile }
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

        fun getDynamic(path: URIPath) = myDynamics.find { it.path == path }


        fun register(static: Webapp.ApiGateway.StaticRoute) {
            myStatics.add(static)
        }

        fun getStatic(path: URIPath) = myStatics.find { it.path == path }
    }

    val routes = Routes()

}
