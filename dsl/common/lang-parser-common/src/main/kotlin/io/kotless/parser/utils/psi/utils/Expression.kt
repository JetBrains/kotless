package io.kotless.parser.utils.psi.utils

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiverOrThis
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.source.getPsi

/** Gather all expressions inside current — recursively */
fun KtExpression.gatherAllExpressions(context: BindingContext, alreadyGot: Set<KtExpression> = emptySet(), andSelf: Boolean = false): Set<KtExpression> {
    val expressions = HashSet<KtExpression>()
    expressions.addAll(gatherExpressions())

    expressions.addAll(gatherReferencedExpressions<ClassDescriptor, KtClass>(context))
    expressions.addAll(gatherReferencedExpressions<ClassDescriptor, KtObjectDeclaration>(context))
    expressions.addAll(gatherReferencedExpressions<ClassDescriptor, KtProperty>(context))
    expressions.addAll(gatherReferencedExpressions<FunctionDescriptor, KtNamedFunction>(context))

    if (andSelf) expressions.add(this@gatherAllExpressions)
    if (this is KtNamedFunction) expressions.add(this.getQualifiedExpressionForReceiverOrThis())

    return expressions + expressions.filterNot { it in alreadyGot }.flatMap { it.gatherAllExpressions(context, alreadyGot + expressions + it) }
}

fun KtExpression.gatherExpressions(filter: (KtExpression) -> Boolean = { true }) = filterFor<KtExpression>().filter(filter)

inline fun <reified Desc : DeclarationDescriptorWithSource, reified Elem : PsiElement> KtExpression.gatherReferencedExpressions(context: BindingContext): List<Elem> {
    return filterFor<KtNameReferenceExpression>().flatMap { ref ->
        ref.getReferenceTargets(context).mapNotNull { it as? Desc }.mapNotNull { it.source.getPsi() as? Elem }
    }
}

