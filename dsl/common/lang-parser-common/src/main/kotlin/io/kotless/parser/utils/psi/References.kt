package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.source.getPsi

fun KtNameReferenceExpression.getTargets(binding: BindingContext) = getReferenceTargets(binding).mapNotNull { (it as? DeclarationDescriptorWithSource)?.source?.getPsi() as? KtElement }
fun KtNameReferenceExpression.getFqName(binding: BindingContext) = getReferenceTargets(binding).singleOrNull()?.fqNameSafe?.asString()
