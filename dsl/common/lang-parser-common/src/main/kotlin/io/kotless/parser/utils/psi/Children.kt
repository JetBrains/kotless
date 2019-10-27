package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement

inline fun <reified T: PsiElement> PsiElement.getChildAt(index: Int) = children[index] as? T?
