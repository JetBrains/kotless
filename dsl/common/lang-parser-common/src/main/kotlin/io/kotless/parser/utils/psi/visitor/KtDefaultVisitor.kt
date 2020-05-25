package io.kotless.parser.utils.psi.visitor

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtVisitorVoid

abstract class KtDefaultVisitor : KtVisitorVoid() {
    override fun visitElement(element: PsiElement) = element.acceptChildren(this)
}
