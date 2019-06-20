package io.kotless.parser

import io.kotless.*
import io.kotless.parser.processor.*
import io.kotless.parser.utils.psi.analysis.AnalysisUtils
import io.kotless.parser.utils.psi.analysis.EnvironmentManager
import org.jetbrains.kotlin.psi.KtFile
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
class KotlessDslParser(private val libs: Set<File>) {
    data class ParsedResult(val dynamicRoutes: Set<Webapp.ApiGateway.DynamicRoute>,
                            val staticRoutes: Set<Webapp.ApiGateway.StaticRoute>,
                            val lambdas: Set<Lambda>,
                            val statics: Set<StaticResource>)


    /**
     * Parse Kotlin code for Kotless constructs.
     * It will reuse already parsed KtFiles/
     */
    fun parseFromKtFiles(jarFile: File, lambdaConfig: Lambda.Config, bucket: String, workDir: File, files: Set<KtFile>): ParsedResult {
        val environment = EnvironmentManager.createEnvironment(libs)
        val context = AnalysisUtils.analyzeFiles(files, environment)

        val permissions = GlobalActionsProcessor.process(context, files)
        val (dynamicRoutes, lambdas) = DynamicRoutesProcessor.process(context, files, permissions, lambdaConfig, jarFile)
        val (staticRoutes, statics) = StaticRoutesProcessor.process(context, files, bucket, workDir)

        return ParsedResult(dynamicRoutes, staticRoutes, lambdas, statics)
    }

    /**
     * Parse Kotlin code for Kotless constructs.
     * It will firstly parse Files into KtFiles.
     */
    fun parseFromFiles(jarFile: File, lambdaConfig: Lambda.Config, bucket: String, workDir: File, files: Set<File>): ParsedResult {
        val environment = EnvironmentManager.createEnvironment(libs)
        val ktFiles = AnalysisUtils.parseFiles(files, environment)
        val context = AnalysisUtils.analyzeFiles(ktFiles, environment)

        val permissions = GlobalActionsProcessor.process(context, ktFiles)
        val (dynamicRoutes, lambdas) = DynamicRoutesProcessor.process(context, ktFiles, permissions, lambdaConfig, jarFile)
        val (staticRoutes, statics) = StaticRoutesProcessor.process(context, ktFiles, bucket, workDir)

        return ParsedResult(dynamicRoutes, staticRoutes, lambdas, statics)
    }
}
