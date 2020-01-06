package io.kotless.dsl.reflection

import io.kotless.InternalAPI
import io.kotless.dsl.conversion.ConversionService
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.*
import kotlin.reflect.jvm.isAccessible

@InternalAPI
internal object FunctionCaller {
    private object NULL

    fun <T> call(func: KCallable<T>, params: Map<String, String>): T {
        func.isAccessible = true

        val args = func.parameters.map { param ->
            val stringValue = params[param.name]
            param to transformToArg(func, param, stringValue)
        }.filter { it.second != NULL }.toMap()

        return func.callBy(args)
    }

    private fun transformToArg(func: KCallable<*>, param: KParameter, value: String?): Any? {
        val isNullable = param.type.isMarkedNullable
        return when {
            param.kind == KParameter.Kind.INSTANCE -> func.boundReceiver()!!
            (value == null || value == "null") && isNullable -> null
            value == "" && isNullable && param.type.classifier != String::class -> null
            value != null -> ConversionService.convertFrom(value, param.type) ?: error("Bad argument ${param.name}='$value'")
            param.isOptional -> NULL
            isNullable -> null
            else -> error("Required argument '${param.name}' is missing")
        }
    }

    private fun KCallable<*>.boundReceiver() = (this as? FunctionReference)?.boundReceiver
        ?: (parameters.find { it.kind == KParameter.Kind.INSTANCE && it.index == 0 }?.type?.classifier as? KClass<*>)?.objectInstance
}
