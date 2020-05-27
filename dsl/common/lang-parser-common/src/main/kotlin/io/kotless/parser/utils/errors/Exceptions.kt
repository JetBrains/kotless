package io.kotless.parser.utils.errors

import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.psi.KtElement

fun KtElement.withExceptionHeader(body: String): String {
    val lineToColumn = StringUtil.offsetToLineColumn(containingKtFile.text, textOffset)
    //TODO-tanvd try to fix path
    val path = containingKtFile.packageFqName.asString().replace(".", "/") + "/" +  containingKtFile.name
    return "Kotless generation error at $path (${lineToColumn.line}, ${lineToColumn.column}): $body"
}
