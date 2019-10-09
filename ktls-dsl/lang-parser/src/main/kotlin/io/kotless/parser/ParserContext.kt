package io.kotless.parser

import io.kotless.*
import io.kotless.parser.processor.Processor
import io.kotless.parser.processor.route.DynamicRoutesProcessor
import java.io.File

class ParserContext(val workDirectory: File, val bucket: String, val file: File, val packages: Set<String>) {
    val storage = Storage()

    class Flow(private val executed: MutableSet<Processor> = HashSet()) {
        fun register(processor: Processor) {
            executed.add(processor)
        }

        fun hasRan(processor: Processor) = processor in executed
    }

    val flow = Flow()

    class Resources(private val dynamics: MutableSet<Lambda> = HashSet(), private val statics: MutableSet<StaticResource> = HashSet()) {
        fun register(lambda: Lambda) {
            dynamics.add(lambda)
        }

        fun getDynamic(name: String) = dynamics.find { it.name == name }

        fun allDynamic(): Set<Lambda> = dynamics

        fun register(static: StaticResource) {
            statics.add(static)
        }

        fun getStatic(file: File) = statics.find { it.content.canonicalFile == file.canonicalFile }

        fun allStatic(): Set<StaticResource> = statics
    }

    val resources = Resources()

    class Routes(private val dynamics: MutableSet<Webapp.ApiGateway.DynamicRoute> = HashSet(),
                 private val statics: MutableSet<Webapp.ApiGateway.StaticRoute> = HashSet()) {
        fun register(dynamic: Webapp.ApiGateway.DynamicRoute) {
            dynamics.add(dynamic)
        }

        fun getDynamic(path: URIPath) = dynamics.find { it.path == path }

        fun allDynamic(): Set<Webapp.ApiGateway.DynamicRoute> = dynamics


        fun register(static: Webapp.ApiGateway.StaticRoute) {
            statics.add(static)
        }

        fun getStatic(path: URIPath) = statics.find { it.path == path }

        fun allStatic(): Set<Webapp.ApiGateway.StaticRoute> = statics
    }

    val routes = Routes()

}
