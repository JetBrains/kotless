package io.kotless

import io.kotless.resource.Lambda
import io.kotless.resource.StaticResource
import io.kotless.utils.TypedStorage
import io.kotless.utils.Visitable

/**
 * Kotless web application
 *
 * It includes ApiGateway REST API definition and Route53 alias with SSL certificate, if present.
 *
 * @param dns alias to ApiGateway, if present
 */
data class Application(val dns: DNS?, val api: API, val events: Events) : Visitable {
    /**
     * Route53 CNAME alias
     *
     * @param zone a qualified name of zone, alias is created in
     * @param alias name of alias
     * @param certificate a fully qualified name of certificate, for SSL connection
     */
    data class DNS(val zone: String, val alias: String, val certificate: String) : Visitable {
        /** fully qualified name of route53 record */
        val fqdn = "$alias.$zone"
    }

    /**
     * Events processed by different functions of Webapp
     *
     * @param scheduled scheduled functions of Webapp
     */
    data class Events(val events: Set<Event>) : Visitable {

        open class Event(private val id: String) : Visitable

        val scheduled: Set<Scheduled>
            get() = events.filterIsInstance<Scheduled>().toSet()

        /**
         * Definition of scheduled event
         *
         * @param id unique name of event
         * @param cron expression in a crontab-like syntax defining scheduler
         * @param lambda function to trigger by scheduled event
         */
        data class Scheduled(private val id: String, val cron: String, val type: CloudwatchEventType, val lambda: TypedStorage.Key<Lambda>) : Event(id) {
            val fqId = "${type.prefix}-$id"
        }

        /**
         * Definition of s3 event
         *
         */
        data class S3(val id: String, val bucket: String, val types: List<String>, val lambda: TypedStorage.Key<Lambda>) : Event(id) {
            val fqId = "${types.joinToString("-") { it }}-$id"
        }

        /**
         * Definition of sqs event
         *
         */
        data class SQS(val id: String, val queueArn: String, val lambda: TypedStorage.Key<Lambda>) : Event(id) {
            val fqId = "$queueArn-$id"
        }

        /**
         * Definition of custom aws event
         *
         */
        data class CustomAwsEvent(val id: String, val path: String, val lambda: TypedStorage.Key<Lambda>) : Event(id) {
            val fqId = "$path-$id"
        }

        override fun visit(visitor: (Any) -> Unit) {
            events.forEach { visitor(it) }
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
    data class API(val name: String, val deployment: Deployment, val dynamics: Set<DynamicRoute>, val statics: Set<StaticRoute>) : Visitable {

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
        dns?.visit(visitor)
        api.visit(visitor)
        events.visit(visitor)
        visitor(this)
    }
}
