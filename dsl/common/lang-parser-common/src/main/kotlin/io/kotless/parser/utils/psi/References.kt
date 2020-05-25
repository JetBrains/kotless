package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.source.getPsi

fun KtReferenceExpression.getTargets(binding: BindingContext): List<KtElement> {
    return getReferenceTargets(binding).mapNotNull { (it as? DeclarationDescriptorWithSource)?.source?.getPsi() as? KtElement }
}

fun KtReferenceExpression.getFqName(binding: BindingContext): String? {
    return getReferenceTargets(binding).singleOrNull()?.fqNameSafe?.asString()
}
