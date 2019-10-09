package io.kotless.parser

import io.kotless.*
import io.kotless.parser.processor.ProcessorContext
import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.route.DynamicRoutesProcessor
import io.kotless.parser.processor.route.StaticRoutesProcessor
import io.kotless.parser.utils.psi.analysis.*
import java.io.File

/**
 * KotlessDslParser parses Kotlin code with Kotlin embeddable compiler looking
 * for Kotless DSL constructs.
 *
 * The result of parsing is a number of Lambdas and StaticResources and associated
 * with them Dynamic and Static routes
 */
object KotlessParser {
    private val processors = setOf(GlobalActionsProcessor, DynamicRoutesProcessor, StaticRoutesProcessor)

    data class Result(val routes: Routes, val resources: Resources) {
        data class Routes(val dynamics: Set<Webapp.ApiGateway.DynamicRoute>, val statics: Set<Webapp.ApiGateway.StaticRoute>)
        data class Resources(val dynamics: Set<Lambda>, val statics: Set<StaticResource>)
    }

    fun parse(files: Set<File>, jar: File, config: KotlessConfig, lambda: Lambda.Config, libs: Set<File>): Result {
        val environment = EnvironmentManager.create(libs)

        val ktFiles = ParseUtil.analyze(files, environment)
        val binding = ResolveUtil.analyze(ktFiles, environment).bindingContext

        val context = ProcessorContext(jar, config, lambda)

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

        return Result(Result.Routes(context.routes.dynamics, context.routes.statics), Result.Resources(context.resources.dynamics, context.resources.statics))
    }
}
