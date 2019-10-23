package io.kotless.parser.utils.psi.utils

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtElement.gatherNamedFunctions(filter: (KtNamedFunction) -> Boolean) = filterFor(filter).toSet()

fun KtCallExpression.getFqName(binding: BindingContext): String? {
    return (referenceExpression() as KtNameReferenceExpression).getReferenceTargets(binding).singleOrNull()?.fqNameSafe?.asString()
}
