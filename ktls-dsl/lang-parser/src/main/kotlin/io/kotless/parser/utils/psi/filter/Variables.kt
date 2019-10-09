package io.kotless.parser.utils.psi.filter

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.reflect.KClass

internal inline fun KtElement.gatherVariables(filter: (KtVariableDeclaration) -> Boolean) = filterFor(filter)
