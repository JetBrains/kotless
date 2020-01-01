package io.kotless.dsl.reflection

import io.kotless.dsl.config.KotlessAppConfig
import org.reflections.Reflections
import org.reflections.scanners.*
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

object ReflectionScanner {
    private val reflections by lazy {
        val configurationBuilder = ConfigurationBuilder()
        Reflections(configurationBuilder
            .apply {
                KotlessAppConfig.packages.forEach { configurationBuilder.addUrls(ClasspathHelper.forPackage(it)) }
            }
            .filterInputsBy { file: String? ->
                KotlessAppConfig.packages.any { pckg -> file?.startsWith(pckg) ?: false }
            }
            .setScanners(MethodAnnotationsScanner(), TypeAnnotationsScanner(), SubTypesScanner(), FieldAnnotationsScanner())
        )
    }

    inline fun <reified T : Annotation, reified E : Any> fieldsWithAnnotation() = fieldsWithAnnotation<T, E>(T::class)

    fun <T : Annotation, E : Any> fieldsWithAnnotation(annotation: KClass<T>): Map<T, E> {
        val fields = reflections.getFieldsAnnotatedWith(annotation.java)
        return fields.mapNotNull {
            it.isAccessible = true
            it.getAnnotation(annotation.java) as T to it.get(it.declaringClass) as E
        }.toMap()
    }

    inline fun <reified T : Annotation> funcsWithAnnotation() = funcsWithAnnotation(T::class)

    fun <T : Annotation> funcsWithAnnotation(annotation: KClass<T>): Set<KFunction<*>> {
        val methods = reflections.getMethodsAnnotatedWith(annotation.java)
        return methods.mapNotNull { it.kotlinFunction }.toSet()
    }

    inline fun <reified T : Annotation> methodsWithAnnotation() = methodsWithAnnotation(T::class)

    fun <T : Annotation> methodsWithAnnotation(annotation: KClass<T>): Set<Method> {
        val methods = reflections.getMethodsAnnotatedWith(annotation.java)
        return methods.mapNotNull { it }.toSet()
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
