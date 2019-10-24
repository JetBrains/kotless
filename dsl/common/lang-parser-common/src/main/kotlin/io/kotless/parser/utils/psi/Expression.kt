package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiverOrThis
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.source.getPsi
import java.util.*
import kotlin.collections.HashSet

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
        ref.getReferenceTargets(context).mapNotNull { (it as? Desc)?.source?.getPsi() as? Elem }
    }
}

/** Gather all expressions inside current — recursively */
fun KtElement.visit(context: BindingContext, body: (element: KtElement, previous: List<KtElement>) -> Boolean) {
    val stack = Stack<KtElement>()
    val previous = Stack<KtElement>()
    stack.add(this)

    while (stack.isNotEmpty()) {
        val cur = stack.pop()

        if (previous.isNotEmpty() && cur == previous.peek()) {
            previous.pop()
            continue
        }

        val next = body(cur, previous)
        if (!next || cur in previous) continue

        previous.add(cur)
        stack.add(cur)
        stack.addAll(cur.children.filterIsInstance<KtElement>())
        if (cur is KtNameReferenceExpression) stack.addAll(cur.getTargets(context))

    }
}

