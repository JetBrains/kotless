package io.kotless.parser.processor

import io.kotless.*
import io.kotless.dsl.kotlessLambdaEntrypoint
import io.kotless.dsl.lang.http.Get
import io.kotless.dsl.lang.http.Post
import io.kotless.parser.utils.buildSet
import io.kotless.parser.utils.psi.annotation.*
import io.kotless.parser.utils.psi.filter.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

internal object DynamicRoutesProcessor {
    private val ROUTE_ANNOTATIONS_CLASSES = listOf(Get::class, Post::class)

    fun process(context: BindingContext, ktFiles: Set<KtFile>, globalPermissions: Set<Permission>,
                config: Lambda.Config, jarFile: File): Pair<Set<Webapp.ApiGateway.DynamicRoute>, Set<Lambda>> {
        val routes = buildSet<Webapp.ApiGateway.DynamicRoute> {
            ktFiles.forEach { ktFile ->
                addAll(gatherRoutesFunctions(context, ktFile).flatMap { route ->
                    ROUTE_ANNOTATIONS_CLASSES.mapNotNull { routeClass ->
                        route.getAnnotations(context, routeClass).singleOrNull()?.let { annotation ->
                            val permissions = gatherPermissionsForRoute(context, route) + globalPermissions

                            val functionName = prepareFunctionName(route, config.packages)

                            val function = Lambda(functionName, jarFile,
                                    Lambda.Entrypoint(kotlessLambdaEntrypoint, emptySet()),
                                    config,
                                    permissions)

                            val routeType = when (routeClass) {
                                Get::class -> HttpMethod.GET
                                Post::class -> HttpMethod.POST
                                else -> error("Not supported class $routeClass")
                            }
                            Webapp.ApiGateway.DynamicRoute(routeType, URIPath(annotation.getValue(context, Get::path)!!.split("/").filter { it.isNotBlank() }), function)
                        }
                    }
                })
            }
        }
        return routes to routes.map { it.lambda }.toSet()
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

    /** Get annotated @Get and @Post top-level functions and object functions */
    private fun gatherRoutesFunctions(context: BindingContext, ktFile: KtFile) = with(ktFile) {
        gatherNamedFunctions { it.isAnnotatedWith(context, ROUTE_ANNOTATIONS_CLASSES) } +
                gatherStaticObjects().flatMap { it.gatherNamedFunctions { it.isAnnotatedWith(context, ROUTE_ANNOTATIONS_CLASSES) } }
    }
}
