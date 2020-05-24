package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import kotlin.reflect.KClass

fun KtElement.visitClassOrObject(filter: (KtClassOrObject) -> Boolean, body: (KtClassOrObject) -> Unit) = acceptChildren(object : KtVisitorVoid() {
    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (!filter(classOrObject)) return

        body(classOrObject)
    }

    override fun visitElement(element: PsiElement) = element.acceptChildren(this)
})

fun KtElement.visitClassOrObject(body: (KtClassOrObject) -> Unit) = acceptChildren(object : KtVisitorVoid() {
    override fun visitClassOrObject(classOrObject: KtClassOrObject) = body(classOrObject)

    override fun visitElement(element: PsiElement) = element.acceptChildren(this)
})


fun KtClassOrObject.isSubtypeOf(klass: KClass<*>, context: BindingContext): Boolean {
    return findClassDescriptor(context).getAllSuperClassifiers().filter { it is ClassDescriptor }.any {
        it.fqNameOrNull()?.asString() == klass.qualifiedName
    }
}
