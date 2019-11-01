package io.kotless.parser.utils.psi

import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.psi.KtElement

fun KtElement.withExceptionHeader(body: String): String {
    val lineToColumn = StringUtil.offsetToLineColumn(containingKtFile.text, textOffset)
    val path = containingKtFile.packageFqName.asString().replace(".", "/")
    val name = containingKtFile.name
    return "Kotless generation error in ${path}/${name} (${lineToColumn.line}, ${lineToColumn.column}): $body"
}
