package io.kotless.dsl.reflection

import io.kotless.dsl.config.KotlessConfig
import io.kotless.dsl.utils.forPackages
import org.reflections.Reflections
import org.reflections.scanners.*
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

internal object ReflectionScanner {
    private val reflections by lazy {
        Reflections(ConfigurationBuilder()
            .forPackages(KotlessConfig.packages)
            .filterInputsBy { file: String? ->
                KotlessConfig.packages.any { pckg -> file?.startsWith(pckg) ?: false }
            }
            .setScanners(MethodAnnotationsScanner(), TypeAnnotationsScanner(), SubTypesScanner())
        )
    }

    inline fun <reified T : Annotation> funcsWithAnnotation() = funcsWithAnnotation(T::class)

    fun <T : Annotation> funcsWithAnnotation(annotation: KClass<T>): Set<KFunction<*>> {
        val methods = reflections.getMethodsAnnotatedWith(annotation.java)
        return methods.mapNotNull { it.kotlinFunction }.toSet()
    }

    inline fun <reified T : Any> withSubtype() = withSubtype(T::class)

    fun <T : Any> withSubtype(klass: KClass<T>): Set<KClass<out T>> {
        val klasses = reflections.getSubTypesOf(klass.java)
        return klasses.mapNotNull { it.kotlin }.toSet()
    }

    inline fun <reified T : Any> objectsWithSubtype() = objectsWithSubtype(T::class)

    fun <T : Any> objectsWithSubtype(klass: KClass<T>): Set<T> {
        val klasses = withSubtype(klass)
        return klasses.mapNotNull { it.objectInstance }.toSet()
    }
}
