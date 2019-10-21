package io.kotless.parser.utils.psi.utils

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import kotlin.reflect.KClass

inline fun <reified T : PsiElement> PsiElement.filterFor(filter: (T) -> Boolean = { true }): Set<T> = filterFor(T::class, filter).toSet()
inline fun <T : PsiElement> PsiElement.filterFor(klass: KClass<T>, filter: (T) -> Boolean = { true }): Set<T> {
    return PsiTreeUtil.collectElementsOfType(this, klass.java).filter(filter).toSet()
}
