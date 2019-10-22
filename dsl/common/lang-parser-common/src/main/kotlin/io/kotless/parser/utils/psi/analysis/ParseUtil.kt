package io.kotless.parser.utils.psi.analysis

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * AnalysisUtils object wraps complex logic of parsing Kotlin file into AST
 * and getting BindingContext (kind of type context) of it.
 */
object ParseUtil {
    /** Get KtFile representation for set of files in specified environment */
    fun analyze(files: Collection<File>, environment: KotlinCoreEnvironment): Set<KtFile> {
        val factory: PsiFileFactory = PsiFileFactory.getInstance(environment.project)
        return files.map { file ->
            factory.createFileFromText(file.name, KotlinLanguage.INSTANCE, file.readText()) as KtFile
        }.toSet()
    }
}
