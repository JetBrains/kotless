package io.kotless

/**
 * Kotless web application
 *
 * It includes ApiGateway REST API definition and Route53 alias with SSL certificate, if present.
 *
 * @param route53 alias to ApiGateway, if present
 */
data class Webapp(val route53: Route53?, val api: ApiGateway, val events: Events) : Visitable {

    /**
     * Route53 CNAME alias
     *
     * @param zone a qualified name of zone, alias is created in
     * @param zone name of alias
     * @param zone a fully qualified name of certificate, for SSL connection
     */
    data class Route53(val zone: String, val alias: String, val certificate: String) : Visitable

    /**
     * Events processed by different functions of Webapp
     *
     * @param scheduled scheduled functions of Webapp
     * @param autowarmed autowarm events setuped for Webapp
     */
    data class Events(val scheduled: Set<Scheduled>, val autowarmed: Set<Autowarm>): Visitable {
        /**
         * Definition of autowarm event
         *
         * @param id unique name of event
         * @param cron expression in a crontab-like syntax defining scheduler for autowarm
         * @param lambda function to warm
         */
        data class Autowarm(val id: String, val cron: String, val lambda: TypedStorage.Key<Lambda>) : Visitable

        /**
         * Definition of scheduled event
         *
         * @param id unique name of event
         * @param cron expression in a crontab-like syntax defining scheduler
         * @param lambda function to trigger by scheduled event
         */
        data class Scheduled(val id: String, val cron: String, val lambda: TypedStorage.Key<Lambda>) : Visitable

        override fun visit(visitor: (Any) -> Unit) {
            scheduled.forEach { visitor(it) }
            autowarmed.forEach { visitor(it) }
            visitor(this)
        }
    }

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
        data class DynamicRoute(override val method: HttpMethod, override val path: URIPath, val lambda: TypedStorage.Key<Lambda>) : Route

        /** Definition of URI path served by static resource (only GET)*/
        data class StaticRoute(override val path: URIPath, val resource: TypedStorage.Key<StaticResource>) : Route {
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
        events.visit(visitor)
        visitor(this)
    }
}
