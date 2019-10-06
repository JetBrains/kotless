package io.kotless.gen.factory.route

import io.kotless.URIPath
import io.kotless.gen.GenerationContext
import io.kotless.gen.Names
import io.kotless.gen.factory.apigateway.RestAPIFactory
import io.kotless.hcl.ref
import io.kotless.terraform.provider.aws.resource.apigateway.api_gateway_resource
import io.kotless.Storage

/**
 * Generic implementation of ApiGateway resources creation
 *
 * Will create all intermediate resources
 */
abstract class AbstractRouteFactory {
    companion object {
        private val resource = Storage.Key<HashMap<URIPath, String>>()
    }

    fun getResource(resourcePath: URIPath, api: RestAPIFactory.RestAPIOutput, context: GenerationContext): String {
        val resources = context.storage.getOrPut(resource) { HashMap() }

        if (URIPath() !in resources) {
            resources[URIPath()] = api.root_resource_id
        }

        var path = URIPath()
        for (part in resourcePath.parts) {
            val prev = path
            path = URIPath(path, part)

            if (path !in resources) {
                val resource = api_gateway_resource(Names.tf(path.parts)) {
                    rest_api_id = api.rest_api_id
                    parent_id = resources[prev]!!
                    path_part = part
                }
                context.entities.register(resource)
                resources[path] = resource::id.ref
            }
        }

        return resources[path]!!
    }
}
