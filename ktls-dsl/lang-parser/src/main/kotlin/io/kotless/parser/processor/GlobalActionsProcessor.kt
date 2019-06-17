package io.kotless.parser.processor

import io.kotless.*
import io.kotless.dsl.lang.LambdaInit
import io.kotless.dsl.lang.LambdaWarming
import io.kotless.dsl.lang.http.HttpRequestInterceptor
import io.kotless.parser.utils.buildSet
import io.kotless.parser.utils.psi.filter.*
import io.kotless.parser.utils.psi.filter.gatherAllExpressions
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext


internal object GlobalActionsProcessor {
    fun process(context: BindingContext, ktFiles: Set<KtFile>) = buildSet<Permission> {
        ktFiles.forEach { ktFile ->
            addAll(gatherGlobalActions<LambdaInit>(LambdaInit::init.name, context, ktFile).flatMap { it.gatherPermissions(context) })
            addAll(gatherGlobalActions<LambdaWarming>(LambdaWarming::warmup.name, context, ktFile).flatMap { it.gatherPermissions(context) })
            addAll(gatherGlobalActions<HttpRequestInterceptor>(HttpRequestInterceptor::intercept.name, context, ktFile)
                    .flatMap { it.gatherPermissions(context) })
        }
    }

    private fun KtNamedFunction.gatherPermissions(context: BindingContext) = buildSet<Permission> {
        val annotatedExpressions = gatherAllExpressions(context, andSelf = true).filterIsInstance<KtAnnotated>()
        addAll(AwsResource.values().flatMap { PermissionsProcessor.process(context, annotatedExpressions) })
        add(Permission(AwsResource.CloudWatchLogs, PermissionLevel.ReadWrite, setOf("*")))
    }

    private inline fun <reified T : Any> gatherGlobalActions(function: String, context: BindingContext, ktFile: KtFile) = with(ktFile) {
        gatherStaticObjectWithSubtype<T>(context).flatMap { it.gatherNamedFunctions { it.name == function } }.toSet()
    }

}
