package io.kotless.parser.utils.psi.visitor

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtVisitorVoid

/**
 * Default implementation of Kotlin Visitor.
 *
 * [shouldVisitElement] can be used to control elements that are visited
 */
abstract class KtDefaultVisitor : KtVisitorVoid() {
    protected open fun shouldVisitElement(element: PsiElement) = true

    override fun visitElement(element: PsiElement) {
        if (!shouldVisitElement(element)) return

        element.acceptChildren(this)
    }
}
