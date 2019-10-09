package io.kotless.parser.processor.route

import io.kotless.*
import io.kotless.dsl.kotlessLambdaEntrypoint
import io.kotless.dsl.lang.http.Get
import io.kotless.dsl.lang.http.Post
import io.kotless.parser.ParserContext
import io.kotless.parser.processor.AnnotationProcessor
import io.kotless.parser.processor.action.GlobalActionsProcessor
import io.kotless.parser.processor.permission.PermissionsProcessor
import io.kotless.parser.utils.buildSet
import io.kotless.parser.utils.psi.annotation.getUriPath
import io.kotless.parser.utils.psi.filter.gatherAllExpressions
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

internal object DynamicRoutesProcessor : AnnotationProcessor() {
    override val annotations = setOf(Get::class, Post::class)

    override fun mayRun(context: ParserContext) = context.flow.hasRan(GlobalActionsProcessor)

    override fun process(files: Set<KtFile>, binding: BindingContext, context: ParserContext) {
        val permissions = context.storage[GlobalActionsProcessor.permissions]!!

        processFunctions(files, binding) { func, entry ->
            val routePermissions = gatherPermissionsForRoute(binding, func) + permissions

            val functionName = prepareFunctionName(func, context.packages)

            val function = Lambda(functionName, context.file,
                Lambda.Entrypoint(kotlessLambdaEntrypoint, emptySet()),
                //TODO-tanvd fix
                Lambda.Config(1024, 300, true, 5, context.packages),
                routePermissions)

            val routeType = when (entry) {
                Get::class -> HttpMethod.GET
                Post::class -> HttpMethod.POST
                else -> error("Not supported class $entry")
            }
            val pathProperty = when (entry) {
                Get::class -> Get::path
                Post::class -> Post::path
                else -> error("Not supported class $entry")
            }
            val path = entry.getUriPath(binding, pathProperty)!!

            context.resources.register(function)
            context.routes.register(Webapp.ApiGateway.DynamicRoute(routeType, path, function))
        }
    }

    private fun prepareFunctionName(route: KtNamedFunction, packages: Set<String>): String = route.fqName!!.asString().let { fqName ->
        val pckg = packages.find { fqName.startsWith("$it.") }
        if (pckg != null) {
            fqName.drop("$pckg.".length)
        } else {
            fqName
        }
    }

    private fun gatherPermissionsForRoute(context: BindingContext, function: KtNamedFunction) = buildSet<Permission> {
        val annotatedExpressions = function.gatherAllExpressions(context, andSelf = true).filterIsInstance<KtAnnotated>()
        addAll(AwsResource.values().flatMap { PermissionsProcessor.process(context, annotatedExpressions) })
        add(Permission(AwsResource.CloudWatchLogs, PermissionLevel.ReadWrite, setOf("*")))
    }
}
