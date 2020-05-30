package io.kotless.parser.utils.psi.analysis

import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.idea.KotlinLanguage
import java.io.File

/** Wrapper for VirtualFile that retains path on machine */
class KotlinLightVirtualFile(file: File, text: String) : LightVirtualFile(file.name, KotlinLanguage.INSTANCE, text) {
    private val path = file.canonicalPath

    override fun getPath(): String = path
}
