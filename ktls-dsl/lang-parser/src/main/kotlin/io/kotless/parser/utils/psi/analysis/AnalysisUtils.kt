package io.kotless.parser.utils.psi.analysis

import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import java.io.File

/**
 * AnalysisUtils object wraps complex logic of parsing Kotlin file into AST
 * and getting BindingContext (kind of type context) of it.
 */
internal object AnalysisUtils {
    /** Get binding context for set of files in specified environment. */
    fun analyzeFiles(files: Set<KtFile>, environment: KotlinCoreEnvironment): BindingContext {
        val trace = CliBindingTrace()
        val configuration = environment.configuration
        //TODO-tanvd Should fix?
//        configuration.put(JVMConfigurationKeys.ADD_BUILT_INS_FROM_COMPILER_TO_DEPENDENCIES, true)

        return TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
                environment.project,
                files,
                trace,
                configuration,
                { globalSearchScope -> environment.createPackagePartProvider(globalSearchScope) },
                { storageManager, ktFiles -> FileBasedDeclarationProviderFactory(storageManager, ktFiles) },
                TopDownAnalyzerFacadeForJVM.newModuleSearchScope(environment.project, files)
        ).bindingContext
    }

    /** Get KtFile representation for set of files in specified environment */
    fun parseFiles(files: Set<File>, environment: KotlinCoreEnvironment): Set<KtFile> {
        val psiFileFactory: PsiFileFactory = PsiFileFactory.getInstance(environment.project)
        val resultSet = HashSet<KtFile>()
        for (file in files) {
            val ktFile = psiFileFactory.createFileFromText(
                    file.name.toString(),
                    KotlinLanguage.INSTANCE,
                    StringUtilRt.convertLineSeparators(file.readText()),
                    true, true, false,
                    LightVirtualFile(file.toString())) as? KtFile ?: throw IllegalStateException("kotlin file expected")
            resultSet += ktFile
        }
        return resultSet
    }
}
