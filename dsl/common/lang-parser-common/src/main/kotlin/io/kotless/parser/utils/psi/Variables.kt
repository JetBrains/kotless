package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtElement.visitVariables(filter: (KtProperty) -> Boolean, body: (KtProperty) -> Unit) = accept(object : KtVisitorVoid() {
    override fun visitProperty(property: KtProperty) {
        if (!filter(property)) return

        body(property)
    }

    override fun visitElement(element: PsiElement) = element.acceptChildren(this)
})

fun KtElement.visitVariables(body: (KtProperty) -> Unit) = accept(object : KtVisitorVoid() {
    override fun visitProperty(property: KtProperty) = body(property)

    override fun visitElement(element: PsiElement) = element.acceptChildren(this)
})


fun KtProperty.getTypeFqName(context: BindingContext): FqName? {
    return (context.get(BindingContext.DECLARATION_TO_DESCRIPTOR, this) as PropertyDescriptor).type.constructor.declarationDescriptor?.fqNameSafe
}
