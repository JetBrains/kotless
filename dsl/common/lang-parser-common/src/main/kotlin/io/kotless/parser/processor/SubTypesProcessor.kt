package io.kotless.parser.processor

import io.kotless.parser.utils.psi.isSubtypeOf
import io.kotless.parser.utils.psi.visitClass
import io.kotless.parser.utils.psi.visitClassOrObject
import io.kotless.parser.utils.psi.visitObject
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass

abstract class SubTypesProcessor<Output : Any> : Processor<Output>() {
    abstract val klasses: Set<KClass<*>>

    fun processClassesOrObjects(files: Set<KtFile>, binding: BindingContext, body: (KtClassOrObject, KClass<*>) -> Unit) {
        for (file in files) {
            file.visitClassOrObject(filter = { it.isSubtypeOf(klasses, binding) }) { klassOrObj ->
                for (curKlass in klasses) {
                    if (klassOrObj.isSubtypeOf(curKlass, binding)) {
                        body(klassOrObj, curKlass)
                    }
                }
            }
        }
    }

    fun processClasses(files: Set<KtFile>, binding: BindingContext, body: (KtClass, KClass<*>) -> Unit) {
        for (file in files) {
            file.visitClass(filter = { it.isSubtypeOf(klasses, binding) }) { klass ->
                for (curKlass in klasses) {
                    if (klass.isSubtypeOf(curKlass, binding)) {
                        body(klass, curKlass)
                    }
                }
            }
        }
    }

    fun processObjects(files: Set<KtFile>, binding: BindingContext, body: (KtObjectDeclaration, KClass<*>) -> Unit) {
        for (file in files) {
            file.visitObject(filter = { it.isSubtypeOf(klasses, binding) }) { obj ->
                for (curKlass in klasses) {
                    if (obj.isSubtypeOf(curKlass, binding)) {
                        body(obj, curKlass)
                    }
                }
            }
        }
    }
}
