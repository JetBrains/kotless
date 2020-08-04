package io.kotless.parser

import io.kotless.KotlessConfig
import io.kotless.resource.Lambda
import io.kotless.resource.StaticResource
import io.kotless.Application
import io.kotless.parser.processor.Processor
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.utils.psi.analysis.EnvironmentManager
import io.kotless.parser.utils.psi.analysis.ParseUtil
import io.kotless.parser.utils.psi.analysis.ResolveUtil
import io.kotless.utils.TypedStorage
import java.io.File

/**
 * Parser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes.
 */
open class Parser(private val processors: Set<Processor<*>>) {
    data class Result(val routes: Routes, val resources: Resources, val events: Events) {
        data class Routes(val dynamics: Set<Application.ApiGateway.DynamicRoute>, val statics: Set<Application.ApiGateway.StaticRoute>)
        data class Resources(val dynamics: TypedStorage<Lambda>, val statics: TypedStorage<StaticResource>)
        data class Events(val scheduled: Set<Application.Events.Scheduled>)
    }

    fun parse(sources: Set<File>, resources: Set<File>, jar: File, config: KotlessConfig, lambda: Lambda.Config, libs: Set<File>): Result {
        val environment = EnvironmentManager.create(libs)

        val ktFiles = ParseUtil.analyze(sources, environment)
        val binding = ResolveUtil.analyze(ktFiles, environment).bindingContext

        val context = ProcessorContext(jar, config, lambda)

        processResources(resources, context)

        var newExecuted = true
        while (newExecuted) {
            newExecuted = false
            for (processor in processors) {
                if (!processor.hasRan(context) && processor.mayRun(context)) {
                    newExecuted = true
                    processor.run(ktFiles, binding, context)
                }
            }
        }

        return Result(
            Result.Routes(context.routes.dynamics, context.routes.statics),
            Result.Resources(context.resources.dynamics, context.resources.statics),
            Result.Events(context.events.scheduled)
        )
    }

    open fun processResources(resources: Set<File>, context: ProcessorContext) {}
}
