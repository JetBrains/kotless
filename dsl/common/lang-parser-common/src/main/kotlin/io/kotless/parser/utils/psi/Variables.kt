package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

inline fun KtElement.gatherVariables(filter: (KtProperty) -> Boolean) = filterFor(filter)

fun KtProperty.getTypeFqName(context: BindingContext): FqName? {
    return (context.get(BindingContext.DECLARATION_TO_DESCRIPTOR, this) as PropertyDescriptor).type.constructor.declarationDescriptor?.fqNameSafe
}
