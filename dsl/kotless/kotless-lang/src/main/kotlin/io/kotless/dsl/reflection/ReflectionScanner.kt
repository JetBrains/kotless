package io.kotless.dsl.reflection

import io.kotless.InternalAPI
import io.kotless.dsl.config.KotlessAppConfig
import org.reflections.Reflections
import org.reflections.scanners.FieldAnnotationsScanner
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

/**
 * Reflection-based annotation scanner of Kotless.
 *
 * Used to find methods, fields, classes and objects that are relevant for
 * Kotless Runtime.
 *
 * For example, it is used to find HTTP handlers and @Scheduled functions
 */
@InternalAPI
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

    //Not used in AWS
    inline fun <reified T : Annotation, reified E : Any> fieldsWithAnnotation() = fieldsWithAnnotation<T, E>(T::class)

    //Not used in AWS
    fun <T : Annotation, E : Any> fieldsWithAnnotation(annotation: KClass<T>): Map<T, E> {
        val fields = reflections.getFieldsAnnotatedWith(annotation.java)
        return fields.mapNotNull {
            it.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            it.getAnnotation(annotation.java) as T to it.get(it.declaringClass) as E
        }.toMap()
    }

}
