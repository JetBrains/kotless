package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtElement.gatherNamedFunctions(filter: (KtNamedFunction) -> Boolean) = filterFor(filter).toSet()

fun KtCallExpression.getReferencedDescriptor(binding: BindingContext): CallableDescriptor? {
    return referenceExpression()?.getReferenceTargets(binding)?.singleOrNull() as CallableDescriptor?
}

fun KtCallExpression.getFqName(binding: BindingContext): String? {
    return getReferencedDescriptor(binding)?.fqNameSafe?.asString()
}
