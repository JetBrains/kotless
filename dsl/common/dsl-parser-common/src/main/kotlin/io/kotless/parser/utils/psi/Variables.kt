package io.kotless.parser.utils.psi

import io.kotless.parser.utils.psi.visitor.KtDefaultVisitor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

fun KtElement.visitVariables(filter: (KtProperty) -> Boolean = { true }, body: (KtProperty) -> Unit) {
    accept(object : KtDefaultVisitor() {
        override fun visitProperty(property: KtProperty) {
            if (filter(property)) body(property)

            super.visitProperty(property)
        }
    })
}

fun KtProperty.getTypeFqName(context: BindingContext): FqName? {
    return (context.get(BindingContext.DECLARATION_TO_DESCRIPTOR, this) as PropertyDescriptor).type.constructor.declarationDescriptor?.fqNameSafe
}
