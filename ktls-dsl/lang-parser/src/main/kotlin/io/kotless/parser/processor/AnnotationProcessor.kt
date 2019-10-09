package io.kotless.parser.processor

import io.kotless.dsl.lang.http.StaticGet
import io.kotless.parser.utils.psi.annotation.getAnnotations
import io.kotless.parser.utils.psi.annotation.isAnnotatedWith
import io.kotless.parser.utils.psi.filter.*
import io.kotless.parser.utils.psi.filter.gatherNamedFunctions
import io.kotless.parser.utils.psi.filter.gatherStaticObjects
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass

abstract class AnnotationProcessor: Processor() {
    abstract val annotations: Set<KClass<out Annotation>>

    fun processFunctions(files: Set<KtFile>, binding: BindingContext, body: (KtNamedFunction, KtAnnotationEntry) -> Unit) {
        for (file in files) {
            for (func in gatherFunctions(binding, file)) {
                for (annotationKClass in annotations) {
                    func.getAnnotations(binding, annotationKClass).forEach { annotation ->
                        body(func, annotation)
                    }
                }
            }
        }
    }

    fun processStaticVariables(files: Set<KtFile>, binding: BindingContext, body: (KtVariableDeclaration, KtAnnotationEntry) -> Unit) {
        for (file in files) {
            for (variable in gatherStaticVariables(binding, file)) {
                for (annotationKClass in annotations) {
                    variable.getAnnotations(binding, annotationKClass).forEach { annotation ->
                        body(variable, annotation)
                    }
                }
            }
        }
    }

    /** Get annotated @Get and @Post top-level functions and object functions */
    private fun gatherFunctions(context: BindingContext, ktFile: KtFile): Set<KtNamedFunction> {
        val named = ktFile.gatherNamedFunctions { it.isAnnotatedWith(context, annotations) }
        val objects = ktFile.gatherStaticObjects().flatMap { obj -> obj.gatherNamedFunctions { it.isAnnotatedWith(context, annotations) } }
        return (named + objects).toSet()
    }

    private fun gatherStaticVariables(context: BindingContext, ktFile: KtFile): Set<KtVariableDeclaration> {
        val topLevel = ktFile.gatherVariables { it.isAnnotatedWith<StaticGet>(context) }
        val objects = ktFile.gatherStaticObjects().flatMap { obj -> obj.gatherVariables { it.isAnnotatedWith<StaticGet>(context) } }
        return (topLevel + objects).toSet()
    }
}
