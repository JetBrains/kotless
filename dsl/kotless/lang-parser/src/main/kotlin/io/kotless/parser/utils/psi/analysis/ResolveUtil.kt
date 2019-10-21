package io.kotless.parser.utils.psi.analysis

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.load.kotlin.PackagePartProvider
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.lazy.ForceResolveUtil

object ResolveUtil {
    fun analyze(files: Collection<KtFile>, environment: KotlinCoreEnvironment): AnalysisResult =
        analyze(files, environment, environment.configuration)

    fun analyze(files: Collection<KtFile>, environment: KotlinCoreEnvironment, configuration: CompilerConfiguration): AnalysisResult =
        analyze(environment.project, files, configuration, environment::createPackagePartProvider)

    private fun analyze(project: Project, files: Collection<KtFile>, configuration: CompilerConfiguration,
                        packagePartProviderFactory: (GlobalSearchScope) -> PackagePartProvider, trace: BindingTrace = CliBindingTrace()): AnalysisResult {
        return TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            project, files, trace, configuration, packagePartProviderFactory
        )
    }
}

/** Forcefully resolves all contents inside KtElement or Descriptor */
internal fun <T> T.forced(): T = ForceResolveUtil.forceResolveAllContents(this)
