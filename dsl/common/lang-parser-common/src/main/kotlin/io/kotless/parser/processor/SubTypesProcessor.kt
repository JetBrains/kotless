package io.kotless.parser.processor

import io.kotless.parser.utils.psi.isSubtypeOf
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass

abstract class SubTypesProcessor<Output : Any> : Processor<Output>() {
    abstract val klasses: Set<KClass<*>>

    private inner class MyVisitor(
        private val binding: BindingContext,
        private val processClassOrObject: (KtClassOrObject, KClass<*>) -> Unit = { _, _ -> },
        private val processClass: (KtClass, KClass<*>) -> Unit = { _, _ -> },
        private val processObject: (KtObjectDeclaration, KClass<*>) -> Unit = { _, _ -> }
    ) : KtTreeVisitorVoid() {
        override fun visitClassOrObject(classOrObject: KtClassOrObject) {
            for (curKlass in klasses) {
                if (classOrObject.isSubtypeOf(curKlass, binding)) {
                    processClassOrObject(classOrObject, curKlass)
                }
            }
        }

        override fun visitClass(klass: KtClass) {
            for (curKlass in klasses) {
                if (klass.isSubtypeOf(curKlass, binding)) {
                    processClass(klass, curKlass)
                }
            }
        }

        override fun visitObjectDeclaration(obj: KtObjectDeclaration) {
            for (curKlass in klasses) {
                if (obj.isSubtypeOf(curKlass, binding)) {
                    processObject(obj, curKlass)
                }
            }
        }
    }

    fun processClassOrObject(files: Set<KtFile>, binding: BindingContext, body: (KtClassOrObject, KClass<*>) -> Unit) {
        val visitor = MyVisitor(binding, processClassOrObject = body)

        for (file in files) {
            file.accept(visitor)
        }
    }

    fun processClass(files: Set<KtFile>, binding: BindingContext, body: (KtClass, KClass<*>) -> Unit) {
        val visitor = MyVisitor(binding, processClass = body)

        for (file in files) {
            file.accept(visitor)
        }
    }

    fun processObject(files: Set<KtFile>, binding: BindingContext, body: (KtObjectDeclaration, KClass<*>) -> Unit) {
        val visitor = MyVisitor(binding, processObject = body)

        for (file in files) {
            file.accept(visitor)
        }
    }
}
