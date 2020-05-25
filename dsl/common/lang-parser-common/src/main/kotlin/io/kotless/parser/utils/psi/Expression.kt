package io.kotless.parser.utils.psi

import io.kotless.parser.utils.psi.visitor.KtDefaultVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiverOrThis
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.source.getPsi
import java.util.*

fun KtExpression.visitAllExpressions(context: BindingContext, alreadyGot: Set<KtElement> = setOf(this), body: (KtExpression) -> Unit) {
    body(this)

    visitReferencedExpressions(context) { _, target ->
        if (target in alreadyGot) return@visitReferencedExpressions

        when (target) {
            is KtClassOrObject -> target.visitAllExpressions(context, alreadyGot + target, body)
            is KtProperty -> target.visitAllExpressions(context, alreadyGot + target, body)
            is KtNamedFunction -> target.visitAllExpressions(context, alreadyGot + target, body)
        }
    }

    if (this is KtNamedFunction) {
        val thisExpr = this.getQualifiedExpressionForReceiverOrThis()

        if (thisExpr in alreadyGot) return

        thisExpr.visitAllExpressions(context, alreadyGot + thisExpr, body)
    }
}

fun KtExpression.visitReferencedExpressions(binding: BindingContext, body: (KtExpression, PsiElement) -> Unit) = accept(object : KtDefaultVisitor() {
    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        val targets = expression.getReferenceTargets(binding).mapNotNull { (it as? DeclarationDescriptorWithSource)?.source?.getPsi() }
        for (target in targets) {
            body(expression, target)
        }
    }
})

/**
 * Gather all expressions inside current — recursively
 *
 * Previous is sorted from the nearest element (at the start) to the most distant
 */
fun KtElement.visit(context: BindingContext, body: (element: KtElement, previous: List<KtElement>) -> Boolean) {
    val stack = Stack<KtElement>()
    val previous = Stack<KtElement>()
    stack.push(this)

    while (stack.isNotEmpty()) {
        val cur = stack.pop()

        if (previous.isNotEmpty() && cur == previous.peek()) {
            previous.pop()
            continue
        }

        val next = body(cur, previous.reversed())
        if (!next || cur in previous) continue

        previous.add(cur)
        stack.push(cur)
        cur.children.filterIsInstance<KtElement>().reversed().forEach { stack.push(it) }
        if (cur is KtNameReferenceExpression) cur.getTargets(context).reversed().forEach { stack.push(it) }
    }
}

