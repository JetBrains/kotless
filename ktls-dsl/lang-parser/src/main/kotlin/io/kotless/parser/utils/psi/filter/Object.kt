package io.kotless.parser.utils.psi.filter

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import kotlin.reflect.KClass

/** Gathers top-level and companion objects in a file */
internal fun KtFile.gatherStaticObjects(filter: (KtObjectDeclaration) -> Boolean = { true }): Set<KtObjectDeclaration> {
    return this.filterFor(filter).toSet()
}

/** Gathers top-level and companion objects in a file */
internal inline fun <reified T : Any> KtFile.gatherStaticObjectWithSubtype(
    context: BindingContext,
    noinline filter: (KtObjectDeclaration) -> Boolean = { true }): Set<KtObjectDeclaration> {
    return gatherStaticObjectsWithSubtypes(context, T::class, filter = filter)
}

/** Gathers top-level and companion objects in a file */
internal fun KtFile.gatherStaticObjectsWithSubtypes(context: BindingContext, vararg klasses: KClass<*>,
                                                    filter: (KtObjectDeclaration) -> Boolean = { true }): Set<KtObjectDeclaration> {
    return this.filterFor<KtObjectDeclaration> { obj ->
        obj.findClassDescriptor(context).getAllSuperClassifiers().filter { it is ClassDescriptor }.any {
            it.fqNameOrNull()?.asString() in klasses.map { it.qualifiedName }
        } && filter(obj)
    }.toSet()
}
