package io.kotless.parser.utils.psi

import io.kotless.parser.utils.psi.visitor.KtDefaultVisitor
import io.kotless.parser.utils.psi.visitor.KtReferenceFollowingVisitor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiverOrThis
import org.jetbrains.kotlin.resolve.BindingContext


fun KtElement.visitAnnotatedWithReferences(binding: BindingContext, filter: (KtAnnotated) -> Boolean = { true }, body: (KtAnnotated) -> Unit) {
    accept(object : KtReferenceFollowingVisitor(binding) {
        override fun shouldFollowReference(expression: KtReferenceExpression, target: KtElement): Boolean {
            return target is KtAnnotated
        }

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

fun KtElement.visitCallExpressionsWithReferences(binding: BindingContext, filter: (KtCallExpression) -> Boolean = { true }, body: (KtCallExpression) -> Unit) {
    accept(object : KtReferenceFollowingVisitor(binding) {
        override fun shouldFollowReference(expression: KtReferenceExpression, target: KtElement): Boolean {
            return target is KtFunction
        }

        override fun visitCallExpression(expression: KtCallExpression) {
            if (filter(expression)) body(expression)

            super.visitCallExpression(expression)
        }
    })
}


