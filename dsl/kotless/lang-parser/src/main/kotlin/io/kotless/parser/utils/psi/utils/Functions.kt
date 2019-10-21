package io.kotless.parser.utils.psi.utils

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction

internal fun KtElement.gatherNamedFunctions(filter: (KtNamedFunction) -> Boolean) = filterFor(filter).toSet()
