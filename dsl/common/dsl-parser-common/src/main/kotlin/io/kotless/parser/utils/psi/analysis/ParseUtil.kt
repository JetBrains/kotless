package io.kotless.parser.utils.psi.analysis

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.vfs.CharsetToolkit
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.com.intellij.psi.impl.PsiFileFactoryImpl
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
        val factory: PsiFileFactoryImpl = PsiFileFactory.getInstance(environment.project) as PsiFileFactoryImpl
        return files.mapNotNull { file ->
            val virtualFile = KotlinLightVirtualFile(file, file.readText())
            virtualFile.charset = CharsetToolkit.UTF8_CHARSET
            factory.trySetupPsiForFile(virtualFile, KotlinLanguage.INSTANCE, true, false) as? KtFile
        }.toSet()
    }
}
