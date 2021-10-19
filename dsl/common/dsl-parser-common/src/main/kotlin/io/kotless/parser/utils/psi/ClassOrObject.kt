package io.kotless.parser.utils.psi

import io.kotless.parser.utils.psi.visitor.KtDefaultVisitor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import kotlin.reflect.KClass

fun KtElement.visitClassOrObject(filter: (KtClassOrObject) -> Boolean = { true }, body: (KtClassOrObject) -> Unit) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            if (filter(classOrObject)) body(classOrObject)

            super.visitClassOrObject(classOrObject)
        }
    })
}

fun KtElement.visitClass(filter: (KtClass) -> Boolean = { true }, body: (KtClass) -> Unit) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitClass(klass: KtClass) {
            if (filter(klass)) body(klass)

            super.visitClass(klass)
        }
    })
}

fun KtElement.visitObject(filter: (KtObjectDeclaration) -> Boolean = { true }, body: (KtObjectDeclaration) -> Unit) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
            if (filter(declaration)) body(declaration)

            super.visitObjectDeclaration(declaration)
        }
    })
}


fun KtClassOrObject.isSubtypeOf(klass: KClass<*>, context: BindingContext): Boolean {
    return isSubtypeOf(setOf(klass), context)
}

fun KtClassOrObject.isSubtypeOf(klasses: Set<KClass<*>>, context: BindingContext): Boolean {
    val names = klasses.mapNotNull { it.qualifiedName }
    return findClassDescriptor(context).getAllSuperClassifiers().filter { it is ClassDescriptor }.any {
        it.fqNameOrNull()?.asString() in names
    }
}
