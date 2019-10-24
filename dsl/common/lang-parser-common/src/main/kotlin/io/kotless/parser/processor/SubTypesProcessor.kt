package io.kotless.parser.processor

import io.kotless.parser.utils.psi.gatherClassesWithSubtypes
import io.kotless.parser.utils.psi.gatherStaticObjectsWithSubtypes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass

abstract class SubTypesProcessor<Output : Any> : Processor<Output>() {
    abstract val klasses: Set<KClass<*>>

    fun processObjects(files: Set<KtFile>, binding: BindingContext, body: (KtObjectDeclaration, KClass<*>) -> Unit) {
        for (file in files) {
            for (klass in klasses) {
                for (obj in file.gatherStaticObjectsWithSubtypes(binding, klass)) {
                    body(obj, klass)
                }
            }
        }
    }

    fun processClasses(files: Set<KtFile>, binding: BindingContext, body: (KtClass, KClass<*>) -> Unit) {
        for (file in files) {
            for (klass in klasses) {
                for (obj in file.gatherClassesWithSubtypes(binding, klass)) {
                    body(obj, klass)
                }
            }
        }
    }
}
