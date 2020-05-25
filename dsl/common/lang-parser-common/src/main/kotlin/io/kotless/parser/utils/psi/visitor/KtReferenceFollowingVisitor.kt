package io.kotless.parser.utils.psi.visitor

import io.kotless.parser.utils.psi.getTargets
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext

abstract class KtReferenceFollowingVisitor(private val binding: BindingContext, protected val alreadySeen: MutableSet<KtElement> = HashSet()) : KtDefaultVisitor() {
    protected open fun shouldFollowReference(expression: KtReferenceExpression, target: KtElement) = true

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        val targets = expression.getTargets(binding)
        for (target in targets) {
            if (target in alreadySeen || !shouldFollowReference(expression, target)) continue

            alreadySeen.add(target)
            target.accept(this)
        }

        super.visitReferenceExpression(expression)
    }
}
