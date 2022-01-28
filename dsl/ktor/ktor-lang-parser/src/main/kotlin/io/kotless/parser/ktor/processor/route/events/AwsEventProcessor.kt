package io.kotless.parser.ktor.processor.route.events

import io.kotless.Application
import io.kotless.resource.Lambda
import io.kotless.utils.TypedStorage
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

interface AwsEventProcessor {
    fun process(callExpression: KtCallExpression, binding: BindingContext, func: KtNamedFunction, key: TypedStorage.Key<Lambda>): List<Application.Events.Event>
}
