package io.kotless.parser

import io.kotless.*
import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
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
 *
 * @param libs -- libraries that should be used as classpath during parsing.
 */
class KotlessDSLParser(private val libs: Set<File>) {
    companion object {
        val processors = setOf(GlobalActionsProcessor, DynamicRoutesProcessor, StaticRoutesProcessor)
    }

    data class ParsedResult(val dynamicRoutes: Set<Webapp.ApiGateway.DynamicRoute>, val staticRoutes: Set<Webapp.ApiGateway.StaticRoute>,
                            val lambdas: Set<Lambda>, val statics: Set<StaticResource>)

    /**
     * Parse Kotlin code for Kotless constructs.
     * It will firstly parse Files into KtFiles.
     */
    fun parseFromFiles(jarFile: File, lambdaConfig: Lambda.Config, bucket: String, workDir: File, files: Set<File>): ParsedResult {
        val environment = EnvironmentManager.create(libs)


        val ktFiles = ParseUtil.analyze(files, environment)
        val binding = ResolveUtil.analyze(ktFiles, environment).bindingContext

        val context = ParserContext(workDir, bucket, jarFile, lambdaConfig.packages)

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

        return ParsedResult(context.routes.allDynamic(), context.routes.allStatic(), context.resources.allDynamic(), context.resources.allStatic())
    }
}
