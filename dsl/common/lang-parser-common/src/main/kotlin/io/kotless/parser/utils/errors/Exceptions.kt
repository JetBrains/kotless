package io.kotless.parser.utils.errors

import io.kotless.parser.utils.psi.analysis.KotlinLightVirtualFile
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.psi.KtElement

fun KtElement.withExceptionHeader(body: String): String {
    val lineToColumn = StringUtil.offsetToLineColumn(containingKtFile.text, textOffset)

    val file = containingKtFile.virtualFile as? KotlinLightVirtualFile
    val path = file?.path ?: "${containingKtFile.packageFqName.asString().replace(".", "/")}/${containingKtFile.name}"
    return "e: $path: (${lineToColumn.line}, ${lineToColumn.column}): $body"
}
