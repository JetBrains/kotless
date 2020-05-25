package io.kotless.parser.utils.psi

import io.kotless.parser.utils.psi.visitor.KtDefaultVisitor
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtElement.visitNamedFunctions(filter: (KtNamedFunction) -> Boolean, body: (KtNamedFunction) -> Unit) = accept(object : KtDefaultVisitor() {
    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!filter(function)) return

        body(function)
    }
})

fun KtElement.visitNamedFunctions(body: (KtNamedFunction) -> Unit) = acceptChildren(object : KtDefaultVisitor() {
    override fun visitNamedFunction(function: KtNamedFunction) = body(function)
})


/** Tell if this function `static` -- either top-level or in Kotlin Object */
fun KtNamedFunction.isStatic() = isTopLevel || this.parents.any { it is KtObjectDeclaration }

fun KtCallExpression.getReferencedDescriptor(binding: BindingContext): CallableDescriptor? {
    return referenceExpression()?.getReferenceTargets(binding)?.singleOrNull() as CallableDescriptor?
}

fun KtCallExpression.getFqName(binding: BindingContext): String? {
    return getReferencedDescriptor(binding)?.fqNameSafe?.asString()
}
