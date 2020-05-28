package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.psiUtil.parents

inline fun <reified T : PsiElement> PsiElement.getChildAt(index: Int) = children[index] as? T?

inline fun <reified T> PsiElement.parents(crossinline filter: (T) -> Boolean = { true }) = parents.filterIsInstance<T>().filter { filter(it) }

inline fun <reified T> Sequence<*>.filterIsInstance() = filterIsInstance(T::class.java)
