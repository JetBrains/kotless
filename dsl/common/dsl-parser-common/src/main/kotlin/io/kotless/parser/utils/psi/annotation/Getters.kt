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

fun KtAnnotationEntry.fqName(context: BindingContext) = getDescriptor(context).qualifiedName

inline fun <reified T : Annotation> KtAnnotated.isAnnotatedWith(context: BindingContext) = getAnnotations<T>(context).isNotEmpty()
fun KtAnnotated.isAnnotatedWith(context: BindingContext, classes: Collection<KClass<*>>): Boolean = getAnnotations(context, classes.toSet()).isNotEmpty()

inline fun <reified T : Annotation> KtAnnotated.getAnnotations(context: BindingContext) = getAnnotations(context, T::class)
inline fun <reified T : Annotation> KtAnnotated.getAnnotation(context: BindingContext): KtAnnotationEntry = getAnnotations(context, T::class).single()

fun KtAnnotated.getAnnotations(context: BindingContext, annotations: Set<KClass<*>>) = annotationEntries.filter {
    it.fqName(context) in annotations.map { ann -> ann.qualifiedName }.toSet()
}.toSet()

fun KtAnnotated.getAnnotations(context: BindingContext, ann: KClass<*>) = getAnnotations(context, setOf(ann))

inline fun <reified T> KtAnnotationEntry.getValue(context: BindingContext, param: KProperty<T>): T? = getDescriptor(context).getValue(param)
inline fun <reified T> KtAnnotationEntry.getValue(context: BindingContext, param: String): T? = getDescriptor(context).getValue(param)
inline fun <reified T> AnnotationDescriptor.getValue(param: KProperty<T>): T? = getValue(param.name)
inline fun <reified T> AnnotationDescriptor.getValue(param: String): T? = argumentValue(param)?.value as T?

fun KtAnnotationEntry.getURIPath(context: BindingContext, param: KProperty<String>) = getValue(context, param)?.toURIPath()
fun KtAnnotationEntry.getURIPath(context: BindingContext, param: String) = getValue<String>(context, param)?.toURIPath()
fun KtAnnotationEntry.getURIPaths(context: BindingContext, param: KProperty<Array<String>>) = getArrayValue(context, param)?.map { it.toURIPath() }
fun KtAnnotationEntry.getURIPaths(context: BindingContext, param: String) = getArrayValue<String>(context, param)?.map { it.toURIPath() }

inline fun <reified T : Any> KtAnnotationEntry.getArrayValue(context: BindingContext, param: KProperty<Array<T>>): Array<T>? {
    return getArrayValue(context, param.name)
}

inline fun <reified T : Any> KtAnnotationEntry.getArrayValue(context: BindingContext, param: String): Array<T>? {
    return getDescriptor(context).getArrayValue(param)
}

inline fun <reified T : Any> AnnotationDescriptor.getArrayValue(param: KProperty<Array<T>>): Array<T>? = getArrayValue(param.name)

inline fun <reified T : Any> AnnotationDescriptor.getArrayValue(param: String): Array<T>? {
    return (argumentValue(param)?.value as? List<*>)?.map { (it as? ConstantValue<*>)?.value ?: it }?.map { it as T }?.toTypedArray()
}

inline fun <reified T : Enum<out T>> KtAnnotationEntry.getEnumValue(context: BindingContext, param: KProperty1<out Annotation, Enum<out T>>): T? {
    return getDescriptor(context).getEnumValue(param)
}

inline fun <reified T : Enum<out T>> AnnotationDescriptor.getEnumValue(param: KProperty1<out Annotation, Enum<out T>>): T? {
    return T::class.java.enumConstants.find { it.name == (argumentValue(param.name) as EnumValue).enumEntryName.identifier }
}

inline fun <reified T : Enum<out T>> KtAnnotationEntry.getArrayEnumValue(context: BindingContext, param: KProperty<Array<T>>): Array<T>? {
    return getDescriptor(context).getArrayEnumValue(param.name)
}

inline fun <reified T : Enum<out T>> AnnotationDescriptor.getArrayEnumValue(param: String): Array<T>? {
    val values = (argumentValue(param)?.value as? List<*>)?.map { (it as EnumValue).enumEntryName.identifier }
    return values?.map { value -> T::class.java.enumConstants.find { it.name == value }!! }?.toTypedArray()
}
