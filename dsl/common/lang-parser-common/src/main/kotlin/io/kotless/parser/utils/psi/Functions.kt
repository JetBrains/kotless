package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtElement.visitNamedFunctions(filter: (KtNamedFunction) -> Boolean, body: (KtNamedFunction) -> Unit) = accept(object : KtVisitorVoid() {
    override fun visitNamedFunction(function: KtNamedFunction) {
        if (!filter(function)) return

        body(function)
    }

    override fun visitElement(element: PsiElement) = element.acceptChildren(this)
})

fun KtElement.visitNamedFunctions(body: (KtNamedFunction) -> Unit) = acceptChildren(object : KtVisitorVoid() {
    override fun visitNamedFunction(function: KtNamedFunction) = body(function)

    override fun visitElement(element: PsiElement) = element.acceptChildren(this)
})


/** Tell if this function `static` -- either top-level or in Kotlin Object */
fun KtNamedFunction.isStatic() = isTopLevel || this.parents.any { it is KtObjectDeclaration }

fun KtCallExpression.getReferencedDescriptor(binding: BindingContext): CallableDescriptor? {
    return referenceExpression()?.getReferenceTargets(binding)?.singleOrNull() as CallableDescriptor?
}

fun KtCallExpression.getFqName(binding: BindingContext): String? {
    return getReferencedDescriptor(binding)?.fqNameSafe?.asString()
}
