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

inline fun <reified T : Annotation> KtAnnotated.isAnnotatedWith(context: BindingContext) = getAnnotations<T>(context).isNotEmpty()
fun KtAnnotated.isAnnotatedWith(context: BindingContext, klass: KClass<*>) = getAnnotations(context, klass).isNotEmpty()
fun KtAnnotated.isAnnotatedWith(context: BindingContext, klasses: Collection<KClass<*>>): Boolean {
    return klasses.any { getAnnotations(context, it).isNotEmpty() }
}

inline fun <reified T : Annotation> KtAnnotated.getAnnotations(context: BindingContext) = getAnnotations(context, T::class)
inline fun <reified T : Annotation> KtAnnotated.getAnnotation(context: BindingContext): KtAnnotationEntry = getAnnotations(context, T::class).single()

fun KtAnnotated.getAnnotations(context: BindingContext, ann: KClass<*>) = annotationEntries.filter {
    it.getDescriptor(context).qualifiedName == ann.qualifiedName
}.toSet()

inline fun <reified T> KtAnnotationEntry.getValue(context: BindingContext, param: KProperty<T>): T? = getDescriptor(context).getValue(param)
inline fun <reified T> AnnotationDescriptor.getValue(param: KProperty<T>): T? = argumentValue(param.name)?.value as T?

fun KtAnnotationEntry.getURIPath(context: BindingContext, param: KProperty<String>) = getValue(context, param)?.toURIPath()
fun KtAnnotationEntry.getURIPaths(context: BindingContext, param: KProperty<Array<String>>) = getArrayValue(context, param )?.map { it.toURIPath() }

inline fun <reified T : Any> KtAnnotationEntry.getArrayValue(context: BindingContext, param: KProperty<Array<T>>): Array<T>? {
    return getDescriptor(context).getArrayValue(param)
}
inline fun <reified T : Any> AnnotationDescriptor.getArrayValue(param: KProperty<Array<T>>): Array<T>? {
    @Suppress("UNCHECKED_CAST")
    return (argumentValue(param.name)?.value as? List<T>)?.map { (it as? ConstantValue<T>)?.value ?: it }?.toTypedArray()
}

inline fun <reified T : Enum<out T>> KtAnnotationEntry.getEnumValue(context: BindingContext,
                                                                    param: KProperty1<out Annotation, Enum<out T>>): T? {
    return getDescriptor(context).getEnumValue(param)
}

inline fun <reified T : Enum<out T>> AnnotationDescriptor.getEnumValue(param: KProperty1<out Annotation, Enum<out T>>): T? {
    return T::class.java.enumConstants.find { it.name == (argumentValue(param.name) as EnumValue).enumEntryName.identifier }
}
