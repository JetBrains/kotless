package io.kotless.parser.utils.psi.utils

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext

fun KtExpression.gatherCallsOf(name: String, binding: BindingContext): Set<KtCallExpression> {
    return gatherAllExpressions(binding).filterIsInstance<KtCallExpression>().filter { it.getFqName(binding) == name }.toSet()
}
