package io.kotless

/**
 * Kotless web application
 *
 * It includes ApiGateway REST API definition and Route53 alias with SSL certificate, if present.
 */
data class Webapp(
    /** Alias to ApiGateway, if present */
    val route53: Route53?,
    val api: ApiGateway) : Visitable {

    /** Route53 CNAME alias */
    data class Route53(
        /** A qualified name of zone, alias is created in */
        val zone: String,
        /** Name of alias */
        val alias: String,
        /** A fully qualified name of certificate, for SSL connection */
        val certificate: String)

    /**
     * ApiGateway REST API.
     *
     * It includes Lambdas and Static Resources and it
     * is an HTTP interface of Kotless Web application
     */
    data class ApiGateway(
        /** A unique name of ApiGateway */
        val name: String,
        /** Deployment resource of ApiGateway */
        val deployment: Deployment,
        /** Dynamic routes of this ApiGateway served by lambdas */
        val dynamics: Set<DynamicRoute>,
        /** Static routes of ApiGateway served by static resources */
        val statics: Set<StaticRoute>) : Visitable {

        /** Deployment definition of ApiGateway. Recreated each redeploy */
        data class Deployment(
            /** A unique name of deployment */
            val name: String,
            /** Version of this deployment.
             *  Will be used for versioned Kotless deployments */
            val version: String)

        interface Route {
            val method: HttpMethod
            val path: URIPath
        }

        /** Definition of URI path served by a lambda (POST or GET depending on `method`) */
        data class DynamicRoute(override val method: HttpMethod, override val path: URIPath, val lambda: Lambda) : Route

        /** Definition of URI path served by static resource (only GET)*/
        data class StaticRoute(override val path: URIPath, val resource: StaticResource) : Route {
            override val method = HttpMethod.GET
        }

        override fun visit(visitor: (Any) -> Unit) {
            visitor(deployment)
            dynamics.forEach { visitor(it) }
            statics.forEach { visitor(it) }
            visitor(this)
        }
    }

    override fun visit(visitor: (Any) -> Unit) {
        visitor(route53!!)
        api.visit(visitor)
        visitor(this)
    }
}
