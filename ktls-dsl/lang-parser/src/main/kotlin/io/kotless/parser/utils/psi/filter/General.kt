package io.kotless.parser.utils.psi.filter

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import kotlin.reflect.KClass

internal inline fun <reified T : PsiElement> PsiElement.filterFor(filter: (T) -> Boolean = { true }): Set<T> = filterFor(T::class, filter).toSet()
internal inline fun <T : PsiElement> PsiElement.filterFor(klass: KClass<T>, filter: (T) -> Boolean = { true }): Set<T> {
    return PsiTreeUtil.collectElementsOfType(this, klass.java).filter(filter).toSet()
}
