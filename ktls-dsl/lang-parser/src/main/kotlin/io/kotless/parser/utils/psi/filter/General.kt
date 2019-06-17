package io.kotless.parser.utils.psi.filter

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import kotlin.reflect.KClass

internal inline fun <reified T : PsiElement> PsiElement.filterFor(filter: (T) -> Boolean = { true }): List<T> = filterFor(T::class, filter)
internal inline fun <T : PsiElement> PsiElement.filterFor(klass: KClass<T>, filter: (T) -> Boolean = { true }): List<T> {
    return PsiTreeUtil.collectElementsOfType(this, klass.java).filter(filter).distinct()
}
