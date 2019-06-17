package io.kotless.parser.processor

import io.kotless.*
import io.kotless.dsl.lang.http.StaticGet
import io.kotless.parser.utils.buildSet
import io.kotless.parser.utils.psi.annotation.*
import io.kotless.parser.utils.psi.annotation.getAnnotations
import io.kotless.parser.utils.psi.annotation.isAnnotatedWith
import io.kotless.parser.utils.psi.filter.gatherStaticObjects
import io.kotless.parser.utils.psi.filter.gatherVariables
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

internal object StaticRoutesProcessor {
    fun process(context: BindingContext, ktFiles: Set<KtFile>, bucket: String, workDir: File): Pair<Set<Webapp.ApiGateway.StaticRoute>, Set<StaticResource>> {
        val routes = buildSet<Webapp.ApiGateway.StaticRoute> {
            ktFiles.forEach { ktFile ->
                addAll(gatherStaticVariables(context, ktFile).mapNotNull { route ->
                    route.getAnnotations<StaticGet>(context).singleOrNull()?.let { annotation ->
                        val path = annotation.getUriPath(context, StaticGet::path)!!
                        val mime = annotation.getEnumValue(context, StaticGet::mime)!!
                        val file = (route.initializer as KtCallExpression).valueArguments.single().text.trim('"')
                        val resource = StaticResource(bucket, URIPath(listOf("static") + path.parts), File(workDir, file), mime)
                        Webapp.ApiGateway.StaticRoute(path, resource)
                    }
                })
            }
        }
        return routes to routes.map { it.resource }.toSet()
    }

    private fun gatherStaticVariables(context: BindingContext, ktFile: KtFile) = with(ktFile) {
        gatherVariables { it.isAnnotatedWith<StaticGet>(context) } + gatherStaticObjects().flatMap { gatherVariables { it.isAnnotatedWith<StaticGet>(context) } }
    }
}
