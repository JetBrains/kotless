package io.kotless.parser.processor

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

abstract class Processor<Output : Any> {
    protected abstract fun process(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext): Output

    abstract fun mayRun(context: ProcessorContext): Boolean

    fun hasRan(context: ProcessorContext) = context.output.check(this)

    fun run(files: Set<KtFile>, binding: BindingContext, context: ProcessorContext) {
        val result = process(files, binding, context)
        context.output.register(this, result)
    }
}
