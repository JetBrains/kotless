package io.kotless

/**
 * Kotless web application
 *
 * It includes ApiGateway REST API definition and Route53 alias with SSL certificate, if present.
 *
 * @param route53 alias to ApiGateway, if present
 */
data class Webapp(val route53: Route53?, val api: ApiGateway) : Visitable {

    /**
     * Route53 CNAME alias
     *
     * @param zone a qualified name of zone, alias is created in
     * @param zone name of alias
     * @param zone a fully qualified name of certificate, for SSL connection
     */
    data class Route53(val zone: String, val alias: String, val certificate: String) : Visitable

    /**
     * ApiGateway REST API.
     *
     * It includes Lambdas and Static Resources and it
     * is an HTTP interface of Kotless Web application
     *
     * @param name A unique name of ApiGateway
     * @param deployment Deployment resource of ApiGateway
     * @param dynamics Dynamic routes of this ApiGateway served by lambdas
     * @param statics  Static routes of ApiGateway served by static resources
     */
    data class ApiGateway(val name: String, val deployment: Deployment, val dynamics: Set<DynamicRoute>, val statics: Set<StaticRoute>) : Visitable {

        /**
         * Deployment definition of ApiGateway. Recreated each redeploy
         *
         * @param name a unique name of deployment
         * @param version version of this deployment; will be used for versioned Kotless deployments
         */
        data class Deployment(val name: String, val version: String) : Visitable

        interface Route : Visitable {
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
            deployment.visit(visitor)
            dynamics.forEach { it.visit(visitor) }
            statics.forEach { it.visit(visitor) }
            visitor(this)
        }
    }

    override fun visit(visitor: (Any) -> Unit) {
        route53?.visit(visitor)
        api.visit(visitor)
        visitor(this)
    }
}
