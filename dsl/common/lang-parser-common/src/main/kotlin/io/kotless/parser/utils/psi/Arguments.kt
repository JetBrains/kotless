package io.kotless.parser.utils.psi

import io.kotless.URIPath
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
import org.jetbrains.kotlin.resolve.constants.TypedCompileTimeConstant
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator


/** Use it in case of interaction with non-Kotlin code */
fun KtCallExpression.getArgumentByIndexOrNull(index: Int): KtValueArgument? = valueArguments[index]

fun KtCallExpression.getArgumentOrNull(name: String, binding: BindingContext): KtValueArgument? {
    val arg = valueArguments.find { it.name == name }
    if (arg != null) return arg
    val index = getReferencedDescriptor(binding)?.valueParameters?.withIndex()?.find { it.value.name.asString() == name }?.index ?: return null

    return valueArguments.getOrNull(index)
}

fun KtCallExpression.getArgument(name: String, binding: BindingContext): KtValueArgument {
    val arg = getArgumentOrNull(name, binding)

    require(arg != null) {
        "Error in expression ${this}: there is no argument with name $name"
    }

    return arg
}

fun KtValueArgument.asString(binding: BindingContext): String {
    val expr = this.getArgumentExpression()
    require(expr != null) {
        "Error in argument $this: argument is not an expression"
    }

    val value = ConstantExpressionEvaluator.getConstant(expr, binding)
    require(value is TypedCompileTimeConstant && value.type.nameIfStandardType?.identifier == "String") {
        "Error in argument $this: argument should be compile-time constant string"
    }

    return value.constantValue.value as String
}

fun KtValueArgument.asReferencedDescriptorOrNull(binding: BindingContext): DeclarationDescriptor? {
    return (getArgumentExpression() as? KtNameReferenceExpression)?.getReferenceTargets(binding)?.singleOrNull()
}

fun KtValueArgument.asPath(binding: BindingContext) = URIPath(asString(binding))
