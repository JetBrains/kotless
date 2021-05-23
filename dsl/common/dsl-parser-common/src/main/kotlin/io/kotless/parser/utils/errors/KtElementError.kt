package io.kotless.parser.utils.errors

import org.jetbrains.kotlin.psi.KtElement
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun error(element: KtElement, message: String): Nothing = error(element.withExceptionHeader(message))

@OptIn(ExperimentalContracts::class)
fun require(element: KtElement, condition: Boolean, message: () -> String) {
    contract {
        returns() implies condition
    }
    require(condition) {
        element.withExceptionHeader(message())
    }
}
