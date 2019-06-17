@file:Suppress("TooManyFunctions")

package io.kotless.parser.utils.psi.annotation

import io.kotless.toURIPath
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.annotations.argumentValue
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.constants.EnumValue
import kotlin.reflect.*

internal inline fun <reified T : Annotation> KtAnnotated.isAnnotatedWith(context: BindingContext) = getAnnotations<T>(context).isNotEmpty()
internal fun KtAnnotated.isAnnotatedWith(context: BindingContext, klass: KClass<*>) = getAnnotations(context, klass).isNotEmpty()
internal fun KtAnnotated.isAnnotatedWith(context: BindingContext, klasses: Collection<KClass<*>>): Boolean {
    return klasses.any { getAnnotations(context, it).isNotEmpty() }
}

internal inline fun <reified T : Annotation> KtAnnotated.getAnnotations(context: BindingContext) = getAnnotations(context, T::class)
internal fun KtAnnotated.getAnnotations(context: BindingContext, ann: KClass<*>) = annotationEntries.filter {
    it.getDescriptor(context).qualifiedName == ann.qualifiedName
}.toSet()

internal fun <T> KtAnnotationEntry.getValue(context: BindingContext, param: KProperty<T>): T? = getDescriptor(context).getValue(param)
@Suppress("UNCHECKED_CAST")
internal fun <T> AnnotationDescriptor.getValue(param: KProperty<T>): T? = argumentValue(param.name)?.value as T?

internal fun KtAnnotationEntry.getUriPath(context: BindingContext, param: KProperty<String>) = getValue(context, param)?.toURIPath()


internal inline fun <reified T : Any> KtAnnotationEntry.getArrayValue(context: BindingContext,
                                                                      param: KProperty1<out Annotation, Array<out T>>): Array<T> {
    return getDescriptor(context).getArrayValue(param)
}

internal inline fun <reified T : Any> AnnotationDescriptor.getArrayValue(param: KProperty1<out Annotation, Array<out T>>): Array<T> {
    @Suppress("UNCHECKED_CAST")
    return (argumentValue(param.name)?.value as? List<T>)?.map { (it as? ConstantValue<T>)?.value ?: it }?.toTypedArray() ?: emptyArray()
}

internal inline fun <reified T : Enum<out T>> KtAnnotationEntry.getEnumValue(context: BindingContext,
                                                                             param: KProperty1<out Annotation, Enum<out T>>): T? {
    return getDescriptor(context).getEnumValue(param)
}

internal inline fun <reified T : Enum<out T>> AnnotationDescriptor.getEnumValue(param: KProperty1<out Annotation, Enum<out T>>): T? {
    return T::class.java.enumConstants.find { it.name == (argumentValue(param.name) as EnumValue).enumEntryName.identifier }
}
