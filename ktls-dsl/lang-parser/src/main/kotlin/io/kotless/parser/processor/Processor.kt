package io.kotless.parser.processor

import io.kotless.parser.ParserContext
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

abstract class Processor {
    protected abstract fun process(files: Set<KtFile>, binding: BindingContext, context: ParserContext)

    abstract fun mayRun(context: ParserContext): Boolean

    fun hasRan(context: ParserContext) = context.flow.hasRan(this)

    fun run(files: Set<KtFile>, binding: BindingContext, context: ParserContext) {
        process(files, binding, context)
        context.flow.register(this)
    }
}
