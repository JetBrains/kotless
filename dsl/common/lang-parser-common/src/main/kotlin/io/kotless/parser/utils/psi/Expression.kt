package io.kotless.parser.utils.psi

import io.kotless.parser.utils.psi.visitor.KtDefaultVisitor
import io.kotless.parser.utils.psi.visitor.KtReferenceFollowingVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiverOrThis
import org.jetbrains.kotlin.resolve.BindingContext


fun KtElement.visitAnnotatedWithReferences(context: BindingContext, filter: (KtAnnotated) -> Boolean = { true }, body: (KtAnnotated) -> Unit) {
    accept(object : KtReferenceFollowingVisitor(context) {
        override fun visitKtElement(element: KtElement) {
            if (element is KtAnnotated && filter(element)) body(element)

            super.visitKtElement(element)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            val thisExpr = function.getQualifiedExpressionForReceiverOrThis()

            if (thisExpr in alreadySeen || thisExpr !is KtAnnotated) return

            alreadySeen.add(thisExpr)
            thisExpr.accept(this)

            super.visitNamedFunction(function)
        }
    })
}

fun KtElement.visitBinaryExpressions(filter: (KtBinaryExpression) -> Boolean = { true }, body: (KtBinaryExpression) -> Unit) {
    accept(object : KtDefaultVisitor() {
        override fun visitBinaryExpression(expression: KtBinaryExpression) {
            if (filter(expression)) body(expression)

            super.visitBinaryExpression(expression)
        }
    })
}

fun KtElement.visitReferencedExpressions(binding: BindingContext, body: (KtExpression, PsiElement) -> Unit) = accept(object : KtDefaultVisitor() {
    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        val targets = expression.getTargets(binding)
        for (target in targets) {
            body(expression, target)
        }

        super.visitReferenceExpression(expression)
    }
})

fun KtElement.visitCallExpressions(filter: (KtCallExpression) -> Boolean = { true },
                                   body: (KtCallExpression) -> Unit) = accept(object : KtDefaultVisitor() {
    override fun visitCallExpression(expression: KtCallExpression) {
        if (filter(expression)) body(expression)

        super.visitCallExpression(expression)
    }
})

fun KtElement.visitAllCallExpressions(binding: BindingContext, filter: (KtCallExpression) -> Boolean = { true },
                                      alreadyGot: Set<KtElement> = setOf(this), body: (KtCallExpression) -> Unit) {
    if (this is KtCallExpression && filter(this)) {
        body(this)
    }

    visitCallExpressions(filter, body)

    visitReferencedExpressions(binding) { _, target ->
        if (target in alreadyGot) return@visitReferencedExpressions

        when (target) {
            is KtFunction -> target.visitAllCallExpressions(binding, filter, alreadyGot + target, body)
        }
    }
}


