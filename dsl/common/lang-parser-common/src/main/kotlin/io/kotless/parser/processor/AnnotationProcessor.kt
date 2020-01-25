package io.kotless.parser.processor

import io.kotless.parser.utils.psi.*
import io.kotless.parser.utils.psi.annotation.getAnnotations
import io.kotless.parser.utils.psi.annotation.isAnnotatedWith
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass

abstract class AnnotationProcessor<Output : Any> : Processor<Output>() {
    abstract val annotations: Set<KClass<out Annotation>>

    fun processClassesOrObjects(files: Set<KtFile>, binding: BindingContext, body: (KtClassOrObject, KtAnnotationEntry, KClass<*>) -> Unit) {
        for (file in files) {
            for (func in gatherClassesOrObjects(binding, file)) {
                for (annotationKClass in annotations) {
                    func.getAnnotations(binding, annotationKClass).forEach { annotation ->
                        body(func, annotation, annotationKClass)
                    }
                }
            }
        }
    }

    fun processFunctions(files: Set<KtFile>, binding: BindingContext, body: (KtNamedFunction, KtAnnotationEntry, KClass<*>) -> Unit) {
        for (file in files) {
            for (func in gatherFunctions(binding, file)) {
                for (annotationKClass in annotations) {
                    func.getAnnotations(binding, annotationKClass).forEach { annotation ->
                        body(func, annotation, annotationKClass)
                    }
                }
            }
        }
    }

    fun processStaticVariables(files: Set<KtFile>, binding: BindingContext, body: (KtProperty, KtAnnotationEntry, KClass<*>) -> Unit) {
        for (file in files) {
            for (variable in gatherStaticVariables(binding, file)) {
                for (annotationKClass in annotations) {
                    variable.getAnnotations(binding, annotationKClass).forEach { annotation ->
                        body(variable, annotation, annotationKClass)
                    }
                }
            }
        }
    }

    /** Get annotated @Get and @Post top-level functions and object functions */
    private fun gatherClassesOrObjects(context: BindingContext, ktFile: KtFile): Set<KtClassOrObject> {
        val named = ktFile.gatherClasses { it.isAnnotatedWith(context, annotations) }
        val objects = ktFile.gatherStaticObjects { it.isAnnotatedWith(context, annotations) }
        return (named + objects).toSet()
    }

    /** Get annotated @Get and @Post top-level functions and object functions */
    private fun gatherFunctions(context: BindingContext, ktFile: KtFile): Set<KtNamedFunction> {
        val named = ktFile.gatherNamedFunctions { it.isAnnotatedWith(context, annotations) }
        val objects = ktFile.gatherStaticObjects().flatMap { obj -> obj.gatherNamedFunctions { it.isAnnotatedWith(context, annotations) } }
        return (named + objects).toSet()
    }

    private fun gatherStaticVariables(context: BindingContext, ktFile: KtFile): Set<KtProperty> {
        val topLevel = ktFile.gatherVariables { it.isAnnotatedWith(context, annotations) }
        val objects = ktFile.gatherStaticObjects().flatMap { obj -> obj.gatherVariables { it.isAnnotatedWith(context, annotations) } }
        return (topLevel + objects).toSet()
    }
}
