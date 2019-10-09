package io.kotless.parser.processor

import io.kotless.parser.utils.psi.filter.gatherStaticObjectsWithSubtypes
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass

abstract class SubTypesProcessor : Processor() {
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
}
