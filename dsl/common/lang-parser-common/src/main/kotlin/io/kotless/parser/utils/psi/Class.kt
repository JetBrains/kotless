package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import kotlin.reflect.KClass

/** Gathers top-level and companion objects in a file */
fun KtFile.gatherClasses(filter: (KtClass) -> Boolean = { true }) = filterFor(filter).toSet()

/** Gathers top-level and companion objects in a file */
inline fun <reified T : Any> KtFile.gatherClassesWithSubtypes(context: BindingContext) = gatherClassesWithSubtypes(context, T::class)

/** Gathers top-level and companion objects in a file */
fun KtFile.gatherClassesWithSubtypes(context: BindingContext, klass: KClass<*>): Set<KtClass> {
    return filterFor { obj ->
        obj.findClassDescriptor(context).getAllSuperClassifiers().filter { it is ClassDescriptor }.any {
            it.fqNameOrNull()?.asString() == klass.qualifiedName
        }
    }
}
