package io.kotless.parser.utils.psi.filter

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtVariableDeclaration

internal inline fun KtElement.gatherVariables(filter: (KtVariableDeclaration) -> Boolean): Set<KtVariableDeclaration> {
    return this.filterFor(filter).toSet()
}
