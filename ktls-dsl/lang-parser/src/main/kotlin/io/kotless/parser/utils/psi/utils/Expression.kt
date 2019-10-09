package io.kotless.parser.utils.psi.utils

import io.kotless.parser.utils.buildSet
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.source.getPsi

/** Gather all expressions inside current — recursively */
internal fun KtExpression.gatherAllExpressions(context: BindingContext, alreadyGot: Set<KtExpression> = emptySet(), andSelf: Boolean = false): Set<KtExpression> {
    val exprs = buildSet<KtExpression> {
        addAll(gatherExpressions())

        addAll(gatherReferencedExpressions<ClassDescriptor, KtClass>(context))
        addAll(gatherReferencedExpressions<ClassDescriptor, KtObjectDeclaration>(context))
        addAll(gatherReferencedExpressions<ClassDescriptor, KtProperty>(context))
        addAll(gatherReferencedExpressions<FunctionDescriptor, KtNamedFunction>(context))

        if (andSelf) add(this@gatherAllExpressions)
    }

    return exprs + exprs.filterNot { it in alreadyGot }.flatMap { it.gatherAllExpressions(context, alreadyGot + exprs + it) }
}

internal fun KtExpression.gatherExpressions(filter: (KtExpression) -> Boolean = { true }) = filterFor<KtExpression>().filter(filter)

internal inline fun <reified Desc : DeclarationDescriptorWithSource, reified Elem : PsiElement>
    KtExpression.gatherReferencedExpressions(context: BindingContext, filter: (Elem) -> Boolean = { true }): List<Elem> {
    return filterFor<KtNameReferenceExpression>().flatMap {
        it.getReferenceTargets(context).mapNotNull { it as? Desc }.mapNotNull { it.source.getPsi() }.mapNotNull { it as? Elem }
    }.filter(filter)
}

